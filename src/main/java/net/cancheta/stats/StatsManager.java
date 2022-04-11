package net.cancheta.stats;

import net.cancheta.ai.path.world.BlockSets;
import net.cancheta.ai.path.world.WorldData;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

public class StatsManager {

	private static final long INTENTIONAL_BLOCK_BREAK_TICKS = 20 * 3;

	private static class IntentionalBreak {

		private long gameTickTimer;
		private BlockState block;
		private BlockPos pos;

		public IntentionalBreak(long gameTickTimer, BlockState block,
				BlockPos pos) {
			this.gameTickTimer = gameTickTimer;
			this.block = block;
			this.pos = pos;
		}

	}

	private long gameTickTimer = 0;

	private BlockBreakStats blockStats = new BlockBreakStats();

	private HashMap<BlockPos, IntentionalBreak> intentionalBreaks = new HashMap<BlockPos, IntentionalBreak>();

	private WorldData world;

	public StatsManager() {
	}

	public void setGameTickTimer(WorldData minecraftWorld) {
		world = minecraftWorld;
		long newTickTimer = minecraftWorld.getWorldTime();
		if (this.gameTickTimer != newTickTimer) {
			long clearIntentionalBreaksBefore = newTickTimer
					- INTENTIONAL_BLOCK_BREAK_TICKS;
			if (this.gameTickTimer + 1 != newTickTimer) {
				// probably a teleport somewhere.
				markWorldChange();
				clearIntentionalBreaksBefore = 0;
			}
			this.gameTickTimer = newTickTimer;

			for (Iterator<Entry<BlockPos, IntentionalBreak>> iterator = intentionalBreaks
					.entrySet().iterator(); iterator.hasNext();) {
				Entry<BlockPos, IntentionalBreak> e = iterator.next();
				if (e.getValue().gameTickTimer < clearIntentionalBreaksBefore) {
					iterator.remove();
				} else if (BlockSets.AIR.isAt(world, e.getKey())) {
					blockDisappearedInClient(e.getValue());
					iterator.remove();
				}
			}
			blockStats.nextGameTick();
		}
	}

	/**
	 * Called whenever the world was (probably) changed or whenever a teleport
	 * occurred.
	 */
	public synchronized void markWorldChange() {
	}

	/**
	 * Marks that the bot is intentionally attempting breaking a block at the
	 * position.
	 * 
	 * @param pos
	 */
	public synchronized void markIntentionalBlockBreak(BlockPos pos) {
		BlockState block = world.getBlockState(pos);
		if (!BlockSets.AIR.isAt(world, pos)) {
			intentionalBreaks.put(pos, new IntentionalBreak(gameTickTimer,
					block, pos));
		}
	}

	private synchronized void blockDisappearedInClient(IntentionalBreak intentionalBreak) {
		blockStats.addBlockBreak(intentionalBreak.pos, intentionalBreak.block);
	}

	public synchronized BlockBreakStats getBlockStats() {
		return blockStats;
	}
}
