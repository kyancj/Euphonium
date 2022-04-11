package net.cancheta.ai.task.move;

import net.cancheta.ai.AIHelper;
import net.cancheta.ai.path.world.BlockSet;
import net.cancheta.ai.path.world.BlockSets;
import net.cancheta.ai.path.world.WorldData;
import net.cancheta.ai.path.world.WorldWithDelta;
import net.cancheta.ai.task.AITask;
import net.cancheta.ai.task.TaskOperations;
import net.cancheta.ai.task.error.PositionTaskError;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;

public class DownwardsMoveTask extends AITask {

	private static final BlockSet hardBlocks = BlockSet.builder().add(Blocks.OBSIDIAN).build();

	private boolean obsidianMining;
	private BlockPos pos;

	public DownwardsMoveTask(BlockPos pos) {
		this.pos = pos;
	}

	@Override
	public boolean isFinished(AIHelper aiHelper) {
		return aiHelper.isStandingOn(pos);
	}

	@Override
	public void runTick(AIHelper aiHelper, TaskOperations taskOperations) {
		WorldData world = aiHelper.getWorld();
		if (needsToClearFootBlock(world)) {
			// tallgrass, ...
			aiHelper.faceAndDestroy(pos.add(0, 1, 0));
		} else if (!BlockSets.AIR.isAt(world, pos)) {
			if (!aiHelper.isStandingOn(pos.add(0, 1, 0))) {
				taskOperations.desync(new PositionTaskError(pos.add(0, 1, 0)));
			}
			if (hardBlocks.isAt(world, pos)) {
				obsidianMining = true;
			}

			aiHelper.faceAndDestroy(pos);
		}
	}

	private boolean needsToClearFootBlock(WorldData world) {
		return !BlockSets.AIR.isAt(world, pos.add(0, 1, 0))
				&& !world.isSideTorch(pos.add(0, 1, 0));
	}

	@Override
	public int getGameTickTimeout(AIHelper helper) {
		return super.getGameTickTimeout(helper)
				+ (obsidianMining ? HorizontalMoveTask.OBSIDIAN_TIME : 0);
	}

	@Override
	public String toString() {
		return "DownwardsMoveTask [obsidianMining=" + obsidianMining + ", pos="
				+ pos + "]";
	}

	@Override
	public boolean applyToDelta(WorldWithDelta world) {
		if (needsToClearFootBlock(world)) {
			world.setBlock(pos.add(0, 1, 0), Blocks.AIR);
		}
		world.setBlock(pos, Blocks.AIR);
		world.setPlayerPosition(pos);
		return true;
	}
}
