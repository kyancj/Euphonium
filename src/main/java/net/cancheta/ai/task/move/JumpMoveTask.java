package net.cancheta.ai.task.move;

import net.cancheta.ai.AIHelper;
import net.cancheta.ai.path.world.BlockSets;
import net.cancheta.ai.path.world.WorldWithDelta;
import net.cancheta.ai.task.TaskOperations;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;

import java.util.List;

// Digs one block up and one to the side then jumps there.

public class JumpMoveTask extends HorizontalMoveTask {

	private final int oldX;
	private final int oldZ;

	public JumpMoveTask(BlockPos pos, int oldX, int oldZ) {
		super(pos);
		this.oldX = oldX;
		this.oldZ = oldZ;
	}

	@Override
	protected boolean doJump(AIHelper aiHelper) {
		BlockPos player = aiHelper.getPlayerPosition();
		return player.getX() != pos.getX() || player.getZ() != pos.getZ();
	}

	@Override
	public void runTick(AIHelper aiHelper, TaskOperations taskOperations) {
		if (!BlockSets.HEAD_CAN_WALK_THROUGH.isAt(aiHelper.getWorld(), oldX, pos.getY() + 1, oldZ)) {
			aiHelper.faceAndDestroy(toDestroyForJump());
		} else {
			super.runTick(aiHelper, taskOperations);
		}
	}

	@Override
	public String toString() {
		return "JumpMoveTask [oldX=" + oldX + ", oldZ=" + oldZ + ", pos=" + pos
				+ "]";
	}

	@Override
	public List<BlockPos> getPredestroyPositions(AIHelper helper) {
		final List<BlockPos> list = super.getPredestroyPositions(helper);
		list.add(0, toDestroyForJump());
		return list;
	}

	private BlockPos toDestroyForJump() {
		return new BlockPos(oldX, pos.getY() + 1, oldZ);
	}
	@Override
	public boolean applyToDelta(WorldWithDelta world) {
		world.setBlock(toDestroyForJump(), Blocks.AIR);
		return super.applyToDelta(world);
	}
}
