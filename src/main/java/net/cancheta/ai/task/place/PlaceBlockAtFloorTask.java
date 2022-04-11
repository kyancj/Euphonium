package net.cancheta.ai.task.place;

import net.cancheta.ai.AIHelper;
import net.cancheta.ai.ItemFilter;
import net.cancheta.ai.path.world.BlockBounds;
import net.cancheta.ai.path.world.BlockSets;
import net.cancheta.ai.task.AITask;
import net.cancheta.ai.task.BlockHalf;
import net.cancheta.ai.task.TaskOperations;
import net.cancheta.ai.task.error.SelectTaskError;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class PlaceBlockAtFloorTask extends AITask {
	private final ItemFilter filter;
	private int faceTimer;
	protected final BlockPos pos;
	private Vec3d positionToFace;
	protected boolean hasPlacedBlock;

	/**
	 * 
	 * @param pos
	 *            The position we are standing on when placing the block. See {@link #getRelativePlaceAtY()}
	 * @param filter
	 *            What to place.
	 */
	public PlaceBlockAtFloorTask(BlockPos pos, ItemFilter filter) {
		this.pos = pos;
		this.filter = filter;
	}

	protected int getRelativePlaceAtY() {
		return 0;
	}

	/**
	 * Check if we face the adjacent block in that direction.
	 * 
	 * @param aiHelper
	 * @param dir
	 * @return
	 */
	protected boolean isFacing(AIHelper aiHelper, Direction dir) {
		BlockPos facingBlock = getPlaceAtPos().offset(dir);
		return aiHelper.isFacingBlock(facingBlock, dir.getOpposite(), getSide(dir));
	}

	protected BlockHalf getSide(Direction dir) {
		return BlockHalf.ANY;
	}

	/**
	 * Where to place the block
	 * @return The position at which the new block will be placed
	 */
	protected final BlockPos getPlaceAtPos() {
		return pos.add(0, getRelativePlaceAtY(), 0);
	}

	@Override
	public boolean isFinished(AIHelper aiHelper) {
		return !BlockSets.AIR.isAt(aiHelper.getWorld(), getPlaceAtPos());
	}

	@Override
	public void runTick(AIHelper aiHelper, TaskOperations taskOperations) {
		if (faceTimer > 0) {
			// Allows place while walking
			reFace(aiHelper);
			faceTimer--;
		}
		if (BlockSets.AIR.isAt(aiHelper.getWorld(), getPlaceAtPos())) {
			if (!aiHelper.selectCurrentItem(filter)) {
				taskOperations.desync(new SelectTaskError(filter));
			} else {
				if (faceTimer == 0) {
					faceBlock(aiHelper, taskOperations);
					faceTimer = 2;
				} else {
					reFace(aiHelper);
					tryPlaceBlock(aiHelper);
				}
			}
		}
	}

	protected void faceBlock(AIHelper aiHelper, TaskOperations o) {
		positionToFace = getPositionToFace(aiHelper);
		reFace(aiHelper);
	}

	private Vec3d getPositionToFace(AIHelper aiHelper) {
		BlockBounds bounds = aiHelper.getWorld().getRaytraceBounds(pos.add(0, -1, 0));
		return bounds.onlySide(Direction.UP).random(pos.add(0, -1, 0), 0.8);
	}

	protected void reFace(AIHelper aiHelper) {
		if (positionToFace != null) {
			aiHelper.face(positionToFace);
		}
	}

	protected void tryPlaceBlock(AIHelper aiHelper) {
		if (isAtDesiredHeight(aiHelper) && isFacingRightBlock(aiHelper)) {
			aiHelper.overrideUseItem();
			hasPlacedBlock = true;
		}
	}

	protected boolean isAtDesiredHeight(AIHelper aiHelper) {
		return aiHelper.getClient().player.getBoundingBox().minY >= getPlaceAtPos()
				.getY();
	}

	protected boolean isFacingRightBlock(AIHelper aiHelper) {
		return isFacing(aiHelper, Direction.DOWN);
	}

	@Override
	public String toString() {
		return "PlaceBlockAtFloorTask [filter=" + filter + ", pos=" + pos + "]";
	}

}
