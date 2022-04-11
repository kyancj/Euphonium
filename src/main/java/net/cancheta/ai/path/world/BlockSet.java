package net.cancheta.ai.path.world;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.IntStream;

import org.lwjgl.system.NonnullDefault;

import net.cancheta.ai.utils.BlockCuboid;
import net.cancheta.ai.utils.BlockFilteredArea;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class BlockSet implements Iterable<BlockState>{
	protected final long[] set;
	
	private final boolean remaining;
	
	private BlockSet(int[] ids) {
		int maxId = IntStream.of(ids).max().orElse(0);
		set = new long[maxId / 64 + 1];
		for (int i : ids) {
			setBlock(i);
		}
		remaining = false;
	}
	
	private BlockSet(long[] set, boolean remaining) {
		this.set = set;
		this.remaining = remaining;
	}
	
	public static Builder builder() {return new Builder();}
	
	private void setBlock(int i) {
		set[i / 64] |= 1L << (i & 63);
	}
	
	public boolean contains(int blockStateId) {
		int index = blockStateId / 64;
		if (index >= set.length) {
			return remaining;
		}
		long query = set[index];
		return (query & (1L << (blockStateId & 63))) != 0;
	}
	
	public boolean contains(BlockState state) { return contains(BlockSet.getStateId(state));}
	
	public BlockSet invert() {
		long[] newSet = new long[set.length];
		for (int i = 0; i < set.length; i++) {
			newSet[i] = ~set[i];
		}
		return new BlockSet(newSet, !remaining);
	}
	
	public boolean isAt(WorldData world, BlockPos pos) {
		return isAt(world, pos.getX(), pos.getY(), pos.getZ());
	}

	public boolean isAt(WorldData world, int x, int y, int z) {
		return contains(world.getBlockStateId(x, y, z));
	}

	public boolean isAt(WorldData world, BlockCuboid area) {
		return new BlockFilteredArea(area, this).getVolume(world) == area
				.getVolume(world);
	}
	
	@Deprecated
	public List<BlockPos> findBlocks(WorldData world, BlockPos around,
			int radius) {
		ArrayList<BlockPos> pos = new ArrayList<BlockPos>();
		// FIXME: Use areas for this.
		for (int x = around.getX() - radius; x <= around.getX() + radius; x++) {
			for (int z = around.getZ() - radius; z <= around.getZ() + radius; z++) {
				for (int y = around.getY() - radius; y <= around.getY()
						+ radius; y++) {
					if (isAt(world, x, y, z)) {
						pos.add(new BlockPos(x, y, z));
					}
				}
			}
		}
		return pos;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(set);
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BlockSet other = (BlockSet) obj;
		if (!Arrays.equals(set, other.set))
			return false;
		return true;
	}
	
	public String getBlockString() {
		Map<Block, List<BlockState>> map = new LinkedHashMap<>();
		for (BlockState state : this) {
			map.computeIfAbsent(state.getBlock(), __ -> new ArrayList<>()).add(state);
		}
		StringBuilder sb = new StringBuilder();
		map.forEach((block, states) -> {
			if (sb.length() > 0) {
				sb.append(", ");
			}
			sb.append(block.getDefaultState().toString())
					.append(" (");

			if (block.getStateManager().getStates().size() == states.size()) {
				sb.append(block.toString());
			} else {
				// Not all of block contained, add states
				states.forEach(state -> {
					sb.append(block.toString());
					sb.append("@");
					//state.getProperties().forEach(prop -> {
					//	sb.append(prop.getName());
					//	sb.append(state.get(prop).toString());
					//});
					//Needs Fixing I think?
				});
			}
			sb.append(")");
		});
		return sb.toString();
	}
	
	@Override
	public Iterator<BlockState> iterator() {
		return new Iterator<BlockState>() {
			int nextId = -1;

			@Override
			public boolean hasNext() {
				if (nextId < 0) {
					scanNext();
				}
				return nextId < getMaxIndex();
			}

			/**
			 * @return 1 more than the maximum block state id this iterator could return
			 */
			private int getMaxIndex() {
				return remaining ? Block.STATE_IDS.size() :  set.length * 64;
			}

			private void scanNext() {
				do {
					nextId++;
				} while (nextId < getMaxIndex()
						&& !contains(nextId));
			}

			@Override
			public BlockState next() {
				if (!hasNext()) {
					throw new IllegalStateException();
				}

				BlockState next = BlockSet.getStateById(nextId);
				scanNext();
				return next;
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}
	public BlockSet minus(BlockSet other) {
		return BlockSet.builder().add(this.invert()).add(other).build().invert();
	}

	public BlockSet intersect(BlockSet other) {
		return BlockSet.builder().add(this.invert()).add(other.invert()).build().invert();
	}

    public static class Builder {
		// May contain duplicates => we don't care
		private final List<Integer> statesToAdd = new ArrayList<>();
		private Builder() {
		}

		private Builder add(int stateId) {
			statesToAdd.add(stateId);
			return this;
		}

		private Builder add(int... stateIds) {
			for (int stateId : stateIds) {
				add(stateId);
			}
			return this;
		}

		public Builder add(BlockState state) {
			statesToAdd.add(getStateId(state));
			return this;
		}

		public Builder add(BlockState ...states) {
			for (BlockState state : states) {
				add(state);
			}
			return this;
		}

		public Builder add(Block block) {
			block.getStateManager().getStates().forEach(this::add);
			return this;
		}

		public Builder add(Block... blocks) {
			for (Block block : blocks) {
				add(block);
			}
			return this;
		}

		public Builder add(BlockSet blockSet) {
			for (BlockState state: blockSet) {
				add(state);
			}
			return this;
		}

		public Builder add(Predicate<BlockState> condition) {
			return add(BlockSets.EMPTY.invert());
		}

		public Builder add(BlockSet set, Predicate<BlockState> condition) {
			if (Block.STATE_IDS.size() == 0) {
				throw new IllegalStateException("Block states not initialized yet.");
			}
			for (BlockState state: set) {
				add(state);
			}
			return this;
		}

		public BlockSet build() {
			return new BlockSet(statesToAdd.stream().mapToInt(it -> it).toArray());
		}

		public Builder intersectWith(BlockSet toIntersectWith) {
			statesToAdd.removeIf(state -> !toIntersectWith.contains(state));
			return this;
		}
    }

	public static int getStateId(@NonnullDefault BlockState state) {
		if (Block.STATE_IDS.size() == 0) {
			throw new IllegalStateException("Block states not initialized yet.");
		}
		return Block.STATE_IDS.getRawId(state);
	}

	public static BlockState getStateById(int id) {
		if (Block.STATE_IDS.size() == 0) {
			throw new IllegalStateException("Block states not initialized yet.");
		}
		return Block.STATE_IDS.get(id);
	}
}
