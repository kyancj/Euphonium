package net.cancheta.ai.path.world;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.WallSignBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.chunk.PalettedContainer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;

import java.lang.reflect.Field;
import java.util.stream.Stream;

import org.jetbrains.annotations.NotNull;


public class WorldData {
	private static final int BARRIER_ID = BlockSet.getStateId(Blocks.BARRIER.getDefaultState());
	private static final int AIR_ID = BlockSet.getStateId(Blocks.AIR.getDefaultState());
	private static final int CACHE_ENTRIES = 10;
	
	private static final int CACHE_INVALID = 0x10000000;
	private static final double FLOOR_HEIGHT = .55;
	
	private static class FastBlockStorageAccess {
		private static final int FORCED_SIZE = MathHelper.log2DeBruijn(Block.STATE_IDS.size());
		private final PalettedContainer<BlockState> data;
		
		private static final Field BITS_FIELD;
		private static final Field STORAGE_FIELD;
		private BitArray array;
		static {
			BITS_FIELD = Stream.of(PalettedContainer.class.getDeclaredFields()).filter(f -> f.getType() == Integer.TYPE).findFirst().get();
			BITS_FIELD.setAccessible(true);
			STORAGE_FIELD = Stream.of(PalettedContainer.class.getDeclaredFields()).filter(f -> f.getType() == BitArray.class).findFirst().get();
			STORAGE_FIELD.setAccessible(true);
		}

		public FastBlockStorageAccess(ChunkSection extendedBlockStorage) {
			data = extendedBlockStorage.getContainer();
			try {
				int bits = BITS_FIELD.getInt(data);
				if (bits != FORCED_SIZE) {
					// Don't care about memory. We care about speed
					data.onResize(FORCED_SIZE, Blocks.AIR.getDefaultState());
				}
				array = (BitArray) STORAGE_FIELD.get(data);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}

		public int get(int lx, int ly, int lz) {
			// Same as return Block.BLOCK_STATE_IDS.get(data.get(lx, ly, lz));
			int index = ly << 8 | lz << 4 | lx;
			return array.getAt(index);
		}
		
	}
	
	public static abstract class ChunkAccessor {
		protected ChunkSection[] blockStorage;
		private FastBlockStorageAccess[] access;

		public int getBlockIdWithMeta(int x, int y, int z) {
			int blockId = 0;
			if (y >> 4 < blockStorage.length) {
				if (access == null) {
					access = new FastBlockStorageAccess[blockStorage.length];
				}
				FastBlockStorageAccess myAccess = access[y >> 4];
				if (myAccess == null && blockStorage[y >> 4] != null) {
					myAccess = new FastBlockStorageAccess(blockStorage[y >> 4]);
					access[y >> 4] = myAccess;
				}
				if (myAccess != null) {
					final int lx = x & 15;
					final int ly = y & 15;
					final int lz = z & 15;
					blockId = myAccess.get(lx, ly, lz);
				}
			}

			return blockId;
		}
	}
	
	private final long[] cachedPos = new long[CACHE_ENTRIES];
	private final ChunkAccessor[] cached = new ChunkAccessor[CACHE_ENTRIES];
	
	private int chunkCacheReplaceCounter = 0;
	
	protected final ClientWorld theWorld;
	private final PlayerEntity thePlayerToGetPositionFrom;

	public WorldData(ClientWorld theWorld,
					 PlayerEntity thePlayerToGetPositionFrom) {
		this.theWorld = theWorld;
		this.thePlayerToGetPositionFrom = thePlayerToGetPositionFrom;
	}
	
	public int getBlockStateId(int x, int y, int z) {
		if (y < 0 || y >= 258) {
			return BARRIER_ID;
		} else if (y >= 256) {
			return AIR_ID;
		}
		ChunkAccessor a = getChunkAccessor(x, z);

		return a == null ? BARRIER_ID : a.getBlockIdWithMeta(x, y, z);
	}
	
	private ChunkAccessor getChunkAccessor(int x, int z) {
		int chunkX = x >> 4;
		int chunkZ = z >> 4;
		ChunkAccessor chunk = null;
		long posForCache = cachePosition(chunkX, chunkZ);

		for (int i = 0; i < CACHE_ENTRIES; i++) {
			if (cachedPos[i] == posForCache) {
				return cached[i];
			}
		}

		chunk = generateChunkAccessor(chunkX, chunkZ);

		cachedPos[chunkCacheReplaceCounter] = posForCache;
		cached[chunkCacheReplaceCounter] = chunk;

		chunkCacheReplaceCounter++;
		if (chunkCacheReplaceCounter >= CACHE_ENTRIES) {
			chunkCacheReplaceCounter = 0;
		}
		return chunk;
	}
	
	protected ChunkAccessor generateChunkAccessor(int chunkX, int chunkZ) {
		return new ChunkAccessorUnmodified(theWorld.getChunk(
				chunkX, chunkZ));
	}
	
	public int getBlockStateId(BlockPos pos) {
		return getBlockStateId(pos.getX(), pos.getY(), pos.getZ());
	}
	
	public BlockState getBlockState(BlockPos pos) {
		return BlockSet.getStateById(getBlockStateId(pos));
	}
	
	public boolean isSideTorch(BlockPos pos) {
		int id = getBlockStateId(pos);
		return BlockSets.WALL_TORCHES.contains(id);
	}
	
	public BlockPos getHangingOnBlock(BlockPos pos) {
		BlockState meta = getBlockState(pos);
		Direction facing = null;
		if (BlockSets.TORCH.contains(meta)) {
			facing = getTorchDirection(meta);
		} else if (BlockSets.WALL_SIGN.contains(meta)) {
			facing = getSignDirection(meta);
			// TODO Ladder and other hanging blocks.
		} else if (BlockSets.FEET_CAN_WALK_THROUGH.contains(meta)) {
			facing = Direction.UP;
		}
		return facing == null ? null : pos.offset(facing, -1);
	}
	
	private Direction getSignDirection(BlockState metaValue) {
		return (Direction) metaValue.get(WallSignBlock.FACING);
	}

	
	@NotNull
	public Direction getTorchDirection(BlockState metaValue) {
		if (BlockSets.TORCH.contains(metaValue)) {
			return Direction.UP;
		} else if (BlockSets.WALL_TORCHES.contains(metaValue)) {
			return metaValue.get(HorizontalFacingBlock.FACING);
		} else {
		 throw new IllegalArgumentException("Not a torch meta: " + metaValue);
		}
	}
	
	protected long cachePosition(int chunkX, int chunkZ) {
		return (long) chunkX << 32 | (chunkZ & 0xffffffffl);
	}
	
	public void invalidateChunkCache() {
		for (int i = 0; i < CACHE_ENTRIES; i++) {
			// chunk coord would be invalid.
			cachedPos[i] = CACHE_INVALID;
		}
	}
	
	public ClientWorld getBackingWorld() {
		return theWorld;
	}
	
	public static class ChunkAccessorUnmodified extends ChunkAccessor {

		public ChunkAccessorUnmodified(Chunk chunk) {
			blockStorage = chunk.getSectionArray();
		}
	}

	public BlockPos getPlayerPosition() {
		final int x = (int) Math.floor(thePlayerToGetPositionFrom.getX());
		final int y = (int) Math.floor(thePlayerToGetPositionFrom
				.getBoundingBox().minY + FLOOR_HEIGHT);
		final int z = (int) Math.floor(thePlayerToGetPositionFrom.getZ());
		return new BlockPos(x, y, z);
	}
	
	public Vec3d getExactPlayerPosition() {
		return new Vec3d(thePlayerToGetPositionFrom.getX(),
				thePlayerToGetPositionFrom.getBoundingBox().minY,
				thePlayerToGetPositionFrom.getZ());
	}
	
	public WorldData getCurrentState() {
		return this;
	}

	public BlockBounds getFacingBounds(BlockPos pos) {
		BlockBounds bounds = getRaytraceBounds(pos);
		return bounds.clampY(0, 1);
	}
	
	public BlockBounds getRaytraceBounds(BlockPos pos) {
		return BlockBoundsCache.RAYCAST.get(pos.getX(), pos.getY(), pos.getZ(), this);
	}
	
	public BlockBounds getCollisionBounds(BlockPos pos) {
		return getCollisionBounds(pos.getX(), pos.getY(), pos.getZ());
	}

	
	public BlockBounds getCollisionBounds(int x, int y, int z) {
		return BlockBoundsCache.COLLISION.get(x, y, z, this);
	}

	public long getWorldTime() {
		return theWorld.getTime();
	}
}
