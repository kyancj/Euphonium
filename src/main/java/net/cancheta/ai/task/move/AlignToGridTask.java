package net.cancheta.ai.task.move;

import net.cancheta.ai.AIHelper;
import net.cancheta.ai.path.world.WorldWithDelta;
import net.cancheta.ai.task.AITask;
import net.cancheta.ai.task.SkipWhenSearchingPrefetch;
import net.cancheta.ai.task.TaskOperations;
import net.minecraft.util.math.BlockPos;

@SkipWhenSearchingPrefetch
public class AlignToGridTask extends AITask {
	private final int x;
	private final int y;
	private final int z;

	public AlignToGridTask(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public AlignToGridTask(BlockPos p) {
		this(p.getX(), p.getY(), p.getZ());
	}

	@Override
	public boolean isFinished(AIHelper aiHelper) {
		return aiHelper.isStandingOn(x, y, z);
	}

	@Override
	public void runTick(AIHelper aiHelper, TaskOperations taskOperations) {
		aiHelper.walkTowards(x + 0.5, z + 0.5, false, !taskOperations.faceAndDestroyForNextTask());
	}

	@Override
	public String toString() {
		return "AlignToGridTask [x=" + x + ", y=" + y + ", z=" + z + "]";
	}
	
	@Override
	public boolean applyToDelta(WorldWithDelta world) {
		world.setPlayerPosition(new BlockPos(x, y, z));
		return true;
	}
}
