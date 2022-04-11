package net.cancheta.ai.task.place;

import net.cancheta.ai.AIHelper;
import net.cancheta.ai.ItemFilter;
import net.minecraft.client.input.Input;
import net.minecraft.util.math.BlockPos;

public class JumpingPlaceBlockAtFloorTask extends PlaceBlockAtFloorTask {
	public JumpingPlaceBlockAtFloorTask(BlockPos pos, ItemFilter filter) {
		super(pos, filter);
	}

	@Override
	protected int getRelativePlaceAtY() {
		return -1;
	}

	@Override
	public boolean isFinished(AIHelper aiHelper) {
		return hasPlacedBlock && isAtDesiredHeight(aiHelper) && !aiHelper.isJumping() && super.isFinished(aiHelper);
	}

	@Override
	protected void tryPlaceBlock(AIHelper aiHelper) {
		super.tryPlaceBlock(aiHelper);
		final Input movement = new Input();
		movement.jumping = true;
		aiHelper.overrideMovement(movement);
	}

	@Override
	public String toString() {
		return "JumpingPlaceBlockAtFloorTask [pos=" + pos + "]";
	}

}
