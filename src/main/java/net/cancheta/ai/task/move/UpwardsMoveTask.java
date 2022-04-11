package net.cancheta.ai.task.move;

import net.cancheta.ai.AIHelper;
import net.cancheta.ai.ItemFilter;
import net.cancheta.ai.path.world.BlockSet;
import net.cancheta.ai.path.world.BlockSets;
import net.cancheta.ai.path.world.WorldWithDelta;
import net.cancheta.ai.task.TaskOperations;
import net.cancheta.ai.task.error.PositionTaskError;
import net.cancheta.ai.task.place.JumpingPlaceBlockAtFloorTask;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;

public class UpwardsMoveTask extends JumpingPlaceBlockAtFloorTask {
	public UpwardsMoveTask(BlockPos pos, ItemFilter filter) {
		super(pos, filter);
	}

	@Override
	public void runTick(AIHelper aiHelper, TaskOperations taskOperations) {
		if (!BlockSets.HEAD_CAN_WALK_THROUGH.isAt(aiHelper.getWorld(), pos.add(0, 1, 0))) {
			if (!aiHelper.isStandingOn(pos.add(0, -1, 0))) {
				taskOperations.desync(new PositionTaskError(pos.add(0, -1, 0)));
			}
			aiHelper.faceAndDestroy(pos.add(0, 1, 0));
		} else if (!BlockSets.AIR.isAt(aiHelper.getWorld(), pos.add(0, -1, 0))
				&& !(isAtDesiredHeight(aiHelper) && hasPlacedBlock)) {
			aiHelper.faceAndDestroy(pos.add(0, -1, 0));
		} else {
			super.runTick(aiHelper, taskOperations);
		}
	}

	@Override
	public String toString() {
		return "UpwardsMoveTask [pos=" + pos + "]";
	}
	
	@Override
	public boolean applyToDelta(WorldWithDelta world) {
		// we always set cobblestone... TODO: set other material.
		
		world.setBlock(getPlaceAtPos(), Blocks.COBBLESTONE);
		world.setBlock(pos, Blocks.AIR);
		world.setBlock(pos.add(0, 1, 0), Blocks.AIR);
		world.setPlayerPosition(pos);

		return true;
	}

}