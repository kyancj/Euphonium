package net.cancheta.ai.task.move;

import net.cancheta.ai.AIHelper;
import net.cancheta.ai.path.world.BlockSets;
import net.cancheta.ai.path.world.RecordingWorld;
import net.cancheta.ai.path.world.WorldWithDelta;
import net.cancheta.ai.task.AITask;
import net.cancheta.ai.task.TaskOperations;
import net.cancheta.ai.task.error.PositionTaskError;
import net.minecraft.util.math.BlockPos;

public class WalkTowardsTask extends AITask {

	protected final int x;
	protected final int z;
	private BlockPos ensureOnPos;
	private BlockPos startPosition;

	public WalkTowardsTask(int x, int z, BlockPos fromPos) {
		this.x = x;
		this.z = z;
		this.ensureOnPos = this.startPosition = fromPos;
	}

	@Override
	public boolean isFinished(AIHelper aiHelper) {
		return aiHelper.arrivedAt(x + 0.5, z + 0.5);
	}

	@Override
	public void runTick(AIHelper aiHelper, TaskOperations taskOperations) {
		if (ensureOnPos != null) {
			if (!aiHelper.isStandingOn(ensureOnPos)) {
				taskOperations.desync(new PositionTaskError(ensureOnPos));
			}
			ensureOnPos = null;
		}
		if (startPosition == null) {
			startPosition = aiHelper.getPlayerPosition();
		}
		final boolean nextIsFacing = taskOperations.faceAndDestroyForNextTask();
		aiHelper.walkTowards(x + 0.5, z + 0.5, false, !nextIsFacing);
	}
	
	@Override
	public int getGameTickTimeout(AIHelper helper) {
		if (startPosition == null) {
			return super.getGameTickTimeout(helper);
		} else {
			return RecordingWorld.timeToWalk(startPosition, new BlockPos(x, startPosition.getY(), z));
		}
	}

	@Override
	public String toString() {
		return "WalkTowardsTask [x=" + x + ", z=" + z + ", fromPos=" + ensureOnPos
				+ "]";
	}

	@Override
	public boolean applyToDelta(WorldWithDelta world) {
		int y = world.getPlayerPosition().getY();
		while (BlockSets.FEET_CAN_WALK_THROUGH.isAt(world, x, y - 1, z)) {
			y--;
		}
		world.setPlayerPosition(new BlockPos(x, y, z));
		return true;
	}
}
