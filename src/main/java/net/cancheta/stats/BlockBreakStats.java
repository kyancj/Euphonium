package net.cancheta.stats;

import net.cancheta.ai.path.world.WorldData;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class BlockBreakStats {
	public static interface BlockBreakStatsChangeListener {

		void blockStatsChanged();

	}

	public class BlockBreakStatsSlice {
		private int start;
		private int end;

		public BlockBreakStatsSlice(int start, int end) {
			this.start = start;
			this.end = end;
		}

		public float getAverage() {
			float sum = 0;
			for (int i = start; i < end; i++) {
				sum += entries.get(i).getCount();
			}
			return sum / (end - start);
		}
	}

	public static class BlockBreakStatEntry {

		private List<BlockBreakStatBreak> breaks;

		public BlockBreakStatEntry(List<BlockBreakStatBreak> toAdd) {
			this.breaks = toAdd;
		}

		public int getCount() {
			return breaks.size();
		}

	}

	private static class BlockBreakStatBreak {
		private BlockPos pos;
		private BlockState block;

		public BlockBreakStatBreak(BlockPos pos, BlockState block) {
			this.pos = pos;
			this.block = block;
		}
	}

	private static final int TICKS_PER_SECOND = 20;
	private int ticksCollected = 0;

	private ArrayList<BlockBreakStatEntry> entries = new ArrayList<BlockBreakStatEntry>();

	private ArrayList<BlockBreakStatBreak> currentTickEntries = new ArrayList<BlockBreakStatBreak>();
	private CopyOnWriteArrayList<BlockBreakStatsChangeListener> listeners = new CopyOnWriteArrayList<BlockBreakStatsChangeListener>();

	public synchronized void nextGameTick() {
		ticksCollected++;
		if (ticksCollected >= TICKS_PER_SECOND) {
			ticksCollected = 0;
			List<BlockBreakStatBreak> toAdd;
			if (currentTickEntries.size() == 0) {
				toAdd = Collections.<BlockBreakStatBreak> emptyList();
			} else {
				toAdd = currentTickEntries;
				currentTickEntries = new ArrayList<BlockBreakStatBreak>();
			}
			entries.add(new BlockBreakStatEntry(toAdd));
			for (BlockBreakStatsChangeListener l : listeners) {
				l.blockStatsChanged();
			}
		}
	}

	public void addBlockBreak(WorldData world, BlockPos pos) {
		BlockState block = world.getBlockState(pos);
		addBlockBreak(pos, block);
	}

	public synchronized void addBlockBreak(BlockPos pos, BlockState block) {
		currentTickEntries.add(new BlockBreakStatBreak(pos, block));
	}

	public synchronized ArrayList<BlockBreakStatEntry> getEntries() {
		return entries;
	}

	public void addChangeListener(BlockBreakStatsChangeListener listener) {
		listeners.add(listener);
	}

	public synchronized BlockBreakStatsSlice getStatsSlice(int secondsInPast) {
		int start = entries.size() - secondsInPast;
		start = clampToRange(start, entries.size() - 1);
		int end = clampToRange(start + secondsInPast, entries.size());
		return new BlockBreakStatsSlice(start, end);
	}

	private int clampToRange(int start, int max) {
		if (start >= max) {
			start = max;
		} else if (start < 0) {
			start = 0;
		}
		return start;
	}
}

