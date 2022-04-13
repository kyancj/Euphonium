package net.cancheta.ai.task.move;

import net.cancheta.ai.AIHelper;
import net.cancheta.ai.path.world.BlockSet;
import net.cancheta.ai.path.world.BlockSets;
import net.cancheta.ai.path.world.WorldData;
import net.cancheta.ai.path.world.WorldWithDelta;
import net.cancheta.ai.task.AITask;
import net.cancheta.ai.task.CanPrefaceAndDestroy;
import net.cancheta.ai.task.TaskOperations;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class HorizontalMoveTask extends AITask implements CanPrefaceAndDestroy {
	private static final BlockSet hardBlocks = BlockSet.builder().add(Blocks.OBSIDIAN).build();
	static final int OBSIDIAN_TIME = 10 * 20;
	protected final BlockPos pos;
//	private boolean hasObsidianLower;
//	private boolean hasObsidianUpper;

	public HorizontalMoveTask(BlockPos pos) {
		this.pos = pos;
	}

	@Override
	public boolean isFinished(AIHelper aiHelper) {
		return aiHelper.isStandingOn(pos);
	}

	@Override
	public void runTick(AIHelper aiHelper, TaskOperations taskOperations) {
		if (needDestroyHead(aiHelper.getWorld())) {
			BlockPos upper = pos.add(0, 1, 0);
			if (hardBlocks.isAt(aiHelper.getWorld(), upper)) {
//				hasObsidianUpper = true;
			}
			aiHelper.faceAndDestroyWithHangingBlock(pos.add(0, 1, 0));
		} else {
			if (needDestroyFoot(aiHelper.getWorld())) {
				if (hardBlocks.isAt(aiHelper.getWorld(), pos)) {
//					hasObsidianLower = true;
				}
				aiHelper.faceAndDestroyWithHangingBlock(pos);
			} else {
				final boolean nextIsFacing = taskOperations.faceAndDestroyForNextTask();
				aiHelper.walkTowards(pos.getX() + 0.5, pos.getZ() + 0.5, doJump(aiHelper),
						!nextIsFacing);
			}
		}
	}

//	@Override
//	public int getGameTickTimeout(AIHelper helper) {
//		return super.getGameTickTimeout(helper)
//				+ (hasObsidianLower ? OBSIDIAN_TIME : 0)
//				+ (hasObsidianUpper ? OBSIDIAN_TIME : 0);
//	}

	protected boolean doJump(AIHelper aiHelper) {
		return false;
	}

	@Override
	public String toString() {
		return "HorizontalMoveTask [pos=" + pos + "]";
	}

	@Override
	public List<BlockPos> getPredestroyPositions(AIHelper helper) {
		final ArrayList<BlockPos> arrayList = new ArrayList<BlockPos>();
		WorldData world = helper.getWorld();
		if (needDestroyHead(world)) {
			arrayList.add(pos.add(0, 1, 0));
		}
		if (needDestroyFoot(world)) {
			arrayList.add(pos);
		}
		return arrayList;
	}

	private boolean needDestroyFoot(WorldData world) {
		return !BlockSets.FEET_CAN_WALK_THROUGH.isAt(world, pos);
	}

	private boolean needDestroyHead(WorldData world) {
		return !BlockSets.HEAD_CAN_WALK_THROUGH.isAt(world, pos.add(0, 1, 0));
	}

	@Override
	public boolean applyToDelta(WorldWithDelta world) {
		if (needDestroyHead(world)) {
			world.setBlock(pos.add(0, 1, 0), Blocks.AIR);
		}
		if (needDestroyFoot(world)) {
			world.setBlock(pos, Blocks.AIR);
		}
		world.setPlayerPosition(pos);
		return true;
	}
}
