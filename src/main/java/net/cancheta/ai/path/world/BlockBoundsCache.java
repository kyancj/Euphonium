package net.cancheta.ai.path.world;

import java.util.Arrays;
import java.util.HashMap;

import org.jetbrains.annotations.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.RaycastContext;

public abstract class BlockBoundsCache {
	private static final HashMap<BlockBounds, BlockBounds> usedBounds = new HashMap<BlockBounds, BlockBounds>();
	private static final BlockPos POS_FAKE_BLOCK_IS_AT = new BlockPos(0, 100, 0);
	
	public static final BlockBoundsCache COLLISION = new BlockBoundsCache() {
        @Override
        protected VoxelShape computeBounds(BlockState state, BlockView world, BlockPos position) {
            return state.getCollisionShape(world, position);
        }
    };
	
	public static final BlockBoundsCache RAYCAST = new BlockBoundsCache() {
        @Override
        protected VoxelShape computeBounds(BlockState state, BlockView world, BlockPos position) {
            return state.getCameraCollisionShape(world, position, null);
        }
    };
    
    private BlockBounds[] bounds = new BlockBounds[10];
    
    public BlockBounds get(int x, int y, int z, WorldData worldData) {
        int blockStateId = worldData.getBlockStateId(x, y, z);
        BlockBounds myBounds = get(blockStateId);
        if (myBounds == BlockBounds.UNKNOWN_BLOCK) {
            // Bounds depend on world state
            try {
                BlockState state = BlockSet.getStateById(blockStateId);
                BlockPos pos = new BlockPos(x, y, z);
                FakeBlockReaderWithWorld worldReader = new FakeBlockReaderWithWorld(state, pos, worldData);
                return BlockBounds.from(computeBounds(state, worldReader, pos));
            } catch (FakeBlockReader.CannotComputeBounds e) {
                return BlockBounds.FULL_BLOCK;
            }
        } else {
            return myBounds;
        }
    }
    
    public BlockBounds get(int blockStateId) {
        if (blockStateId < bounds.length && bounds[blockStateId] != null) {
            return bounds[blockStateId];
        }

        if (bounds.length <= blockStateId) {
            bounds = Arrays.copyOf(bounds, Math.max(blockStateId + 1, bounds.length * 2));
        }

        // Compute bounds
        BlockState state = BlockSet.getStateById(blockStateId);
        BlockBounds myBounds = computeBounds(state);
        bounds[blockStateId] = myBounds;
        return bounds[blockStateId];
    }
    
    private BlockBounds computeBounds(BlockState state) {
        try {
            BlockView world = new FakeBlockReader(state);
            BlockBounds bounds = BlockBounds.from(computeBounds(state, world, POS_FAKE_BLOCK_IS_AT));
            // dedup
            usedBounds.computeIfAbsent(bounds, __ -> bounds);
            return usedBounds.get(bounds);
        } catch (FakeBlockReader.CannotComputeBounds e) {
            // Do not dedub, since we need == to check this.
            return BlockBounds.UNKNOWN_BLOCK;
        }
    }
    
    protected abstract VoxelShape computeBounds(BlockState state, BlockView world, BlockPos position);

    private static class FakeBlockReader implements BlockView {
        protected final BlockState state;

        public FakeBlockReader(BlockState state) {
            this.state = state;
        }

        @Override
        public BlockState getBlockState(BlockPos pos) {
            if (POS_FAKE_BLOCK_IS_AT.equals(pos)) {
                return state;
            }
            throw new CannotComputeBounds("Bounds depend on other block at relative position " + pos.subtract(POS_FAKE_BLOCK_IS_AT));
        }

        @Override
        public FluidState getFluidState(BlockPos pos) {
            throw new CannotComputeBounds("Bounds depend on fluid state");
        }

        @Override
        public int getLuminance(BlockPos pos) {
            return 0;
        }

        @Override
        public int getMaxLightLevel() {
            return 7;
        }

        @Override
        public int getHeight() {
            return 255;
        }

        @Override
        public BlockHitResult raycast(RaycastContext context) {
            throw new CannotComputeBounds("Bounds use ray trace");
        }

        @Nullable
        @Override
        public BlockHitResult raycastBlock(Vec3d p_217296_1_, Vec3d p_217296_2_, BlockPos p_217296_3_,
                                                  VoxelShape p_217296_4_, BlockState p_217296_5_) {
            throw new CannotComputeBounds("Bounds use ray trace");
        }

        private class CannotComputeBounds extends RuntimeException {
            public CannotComputeBounds(String message) {
                super(message);
            }
        }

		@Override
		public int getBottomY() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public BlockEntity getBlockEntity(BlockPos pos) {
			// TODO Auto-generated method stub
			return null;
		}
    }
    
    private static class FakeBlockReaderWithWorld extends FakeBlockReader {
        private final BlockPos posOfThatState;
        private final WorldData world;

        FakeBlockReaderWithWorld(BlockState state, BlockPos posOfThatState, WorldData world) {
            super(state);
            this.posOfThatState = posOfThatState;
            this.world = world;
        }

        @Override
        public BlockState getBlockState(BlockPos pos) {
            if (pos.equals(posOfThatState)) {
                return state;
            } else {
                return world.getBlockState(pos);
            }
        }
    }
}
