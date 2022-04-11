package net.cancheta.ai.path;

import java.util.LinkedList;

import net.cancheta.ai.AIHelper;
import net.cancheta.ai.BlockItemFilter;
import net.cancheta.ai.path.world.BlockSet;
import net.cancheta.ai.path.world.BlockSets;
import net.cancheta.ai.path.world.Pos;
import net.cancheta.ai.path.world.WorldData;
import net.cancheta.ai.task.AITask;
import net.cancheta.ai.task.move.AlignToGridTask;
import net.cancheta.ai.task.move.DownwardsMoveTask;
import net.cancheta.ai.task.move.HorizontalMoveTask;
import net.cancheta.ai.task.move.JumpMoveTask;
import net.cancheta.ai.task.move.UpwardsMoveTask;
import net.cancheta.ai.task.move.WalkTowardsTask;
import net.cancheta.settings.EuphoniumSettings;
import net.cancheta.settings.EuphoniumSettingsRoot;
import net.cancheta.settings.PathfindingSetting;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MovePathFinder extends PathFinderField {
	private static final Logger LOGGER = LogManager.getLogger(MovePathFinder.class);
	
	/**
	 * Blocks that are destructable faster.
	 */
	protected final static BlockSet fastDestructableBlocks = BlockSet.builder().add(
			Blocks.DIRT,
			Blocks.GRAVEL, 
			Blocks.SAND, 
			Blocks.SANDSTONE)
			.add(BlockSets.LEAVES)
			.build();

	/**
	 * Blocks we should not dig through, e.g. because we cannot handle them
	 * correctly.
	 */
	protected static final BlockSet defaultForbiddenBlocks = BlockSet.builder().add(
			Blocks.BEDROCK, 
			Blocks.CACTUS, 
			Blocks.OBSIDIAN,
			Blocks.MOVING_PISTON,
			Blocks.PISTON_HEAD).build();

	/**
	 * The AI helper
	 */
	protected AIHelper helper;
	/**
	 * The local world we do path finding on. This may already represent the
	 * expected world state in the future.
	 */
	protected WorldData world;

	/**
	 * The minebot settings used.
	 */
	protected PathfindingSetting pathSettings;
	protected EuphoniumSettingsRoot settings;

	/**
	 * The blocks we use when building upwards.
	 */
	protected BlockSet upwardsBuildBlocks;

	/**
	 * This list can be changed. Only blocks of this type are walked to.
	 */
	protected BlockSet allowedGroundBlocks;
	/**
	 * Those ground blocks are the blocks we allow to 'stand' on after a upwards
	 * move.
	 */
	protected BlockSet allowedGroundForUpwardsBlocks;

	/**
	 * Blocks we allow the feet to walk through.
	 */
	protected BlockSet footAllowedBlocks;
	/**
	 * Blocks we allow the head to walk through.
	 */
	protected BlockSet headAllowedBlocks;

	protected BlockSet shortFootBlocks = BlockSets.FEET_CAN_WALK_THROUGH;
	protected BlockSet shortHeadBlocks = BlockSets.HEAD_CAN_WALK_THROUGH;

	protected PathfindingSetting setting;

	// /**
	// * Current forbidden block settings. Just FYI, never used by this
	// * pathfinder.
	// */
	// protected final BlockSet forbiddenBlocks;
	private TaskReceiver receiver;

	private volatile BlockPos currentTarget;

	public MovePathFinder() {
		super();
		settings = EuphoniumSettings.getSettings();
		pathSettings = loadSettings(settings);

		upwardsBuildBlocks = pathSettings.getUpwardsBuildBlocks();

		allowedGroundBlocks = pathSettings.getAllowedGround();
		allowedGroundForUpwardsBlocks = pathSettings
				.getAllowedGroundWhenUpwards();
		footAllowedBlocks = pathSettings.getFootWalkThrough();
		headAllowedBlocks = pathSettings.getHeadWalkThrough();

		// getBlocks("upwards_place_block",
		// defaultUpwardsBlocks);
		// forbiddenBlocks = settings.getBlocks("blacklisted_blocks",
		// defaultForbiddenBlocks);
		//
		// footAllowedBlocks = footAllowedBlocks.intersectWith(forbiddenBlocks
		// .invert());
		// headAllowedBlocks = headAllowedBlocks.intersectWith(forbiddenBlocks
		// .invert());
	}

	protected PathfindingSetting loadSettings(EuphoniumSettingsRoot settingsRoot) {
		return settingsRoot.getPathfinding().getDestructivePathfinder();
	}

	@Override
	protected final boolean searchSomethingAround(int cx, int cy, int cz) {
		throw new UnsupportedOperationException("Direct call not supported.");
	}

	protected void addTask(AITask task) {
		receiver.addTask(task);
	}

	public final boolean searchSomethingAround(BlockPos playerPosition,
			AIHelper helper, WorldData world, TaskReceiver receiver) {
		currentTarget = null;
		this.helper = helper;
		this.world = world;
		this.receiver = receiver;
		return runSearch(playerPosition);
	}

	/**
	 * 
	 * @param playerPosition
	 * @return <code>false</code> When pathfinding should be given more time.
	 */
	protected boolean runSearch(BlockPos playerPosition) {
		return super.searchSomethingAround(playerPosition.getX(),
				playerPosition.getY(), playerPosition.getZ());
	}

	@Override
	protected int getNeighbour(int currentNode, int cx, int cy, int cz) {
		final int res = super.getNeighbour(currentNode, cx, cy, cz);
		if (res > 0 && (
				// We cannot go there (world constraints)
				!isSafeToTravel(currentNode, cx, cy, cz)
						// Do not go up when standing on a slab
						// TODO: There may be more such blocks, like snow
						|| (getY(currentNode) < cy && BlockSets.LOWER_SLABS.isAt(world, cx, getY(currentNode) - 1, cz)))) {
			return -1;
		}
		return res;
	}

	/**
	 * Check if a position is safe to travel to
	 * @param currentNode The node we are comming from
	 * @param cx Destination x
	 * @param cy Destination y
	 * @param cz Destination z
	 * @return true if we can go there
	 */
	protected boolean isSafeToTravel(int currentNode, int cx, int cy, int cz) {
		return BlockSets.safeSideAround(world, cx, cy + 1, cz)
				&& isAllowedPosition(cx, cy, cz)
				&& BlockSets.safeSideAround(world, cx, cy, cz)
				&& checkHeadBlock(currentNode, cx, cy, cz)
				&& checkGroundBlock(currentNode, cx, cy, cz);
	}

	/**
	 * Are we allowed to travel there, only looking at the blocks that we need.
	 * 
	 * @param cx
	 * @param cy
	 * @param cz
	 * @return
	 */
	private boolean isAllowedPosition(int cx, int cy, int cz) {
		return footAllowedBlocks.isAt(world, cx, cy, cz)
				&& headAllowedBlocks.isAt(world, cx, cy + 1, cz);
	}

	protected boolean checkGroundBlock(int currentNode, int cx, int cy, int cz) {
		if (getY(currentNode) < cy && cx == getX(currentNode) && cz == getZ(currentNode)) {
			// Moving straight up - this needs to place a block
			return allowedGroundForUpwardsBlocks.isAt(world, cx, cy - 1, cz);
		} else {
			return allowedGroundBlocks.isAt(world, cx, cy - 1, cz);
		}
	}

	private boolean checkHeadBlock(int currentNode, int cx, int cy, int cz) {
		if (getY(currentNode) > cy) {
			// Moving down
			if (getX(currentNode) != cx || getZ(currentNode) != cz) {
				// Moving sideways down
				return BlockSets.SAFE_CEILING.isAt(world, cx, cy + 3, cz)
						&& BlockSets.HEAD_CAN_WALK_THROUGH.isAt(world, cx,
								cy + 2, cz);
			} else if (BlockSets.FALLING.isAt(world, cx, cy + 2, cz)) {
				// moving straight down, so ignoring sand, gravel.
				return true;
			}
		}
		return BlockSets.SAFE_CEILING.isAt(world, cx, cy + 2, cz);
	}

	@Override
	protected void foundPath(LinkedList<BlockPos> path) {
		super.foundPath(path);
		BlockPos currentPos = path.removeFirst();
		addTask(new AlignToGridTask(currentPos.getX(), currentPos.getY(),
				currentPos.getZ()));
		while (!path.isEmpty()) {
			BlockPos nextPos = path.removeFirst();
			Direction moveDirection = direction(currentPos, nextPos);
			int fastMoveStepsAdded = 0;
			while (path.peekFirst() != null
					&& isAreaClear(currentPos, path.peekFirst())
					&& fastMoveStepsAdded < 40) {
				nextPos = path.removeFirst();
				fastMoveStepsAdded++;
			}
			final BlockPos peeked = path.peekFirst();
			if (fastMoveStepsAdded > 0) {
				addHorizontalFastMove(currentPos, nextPos);
			} else if (moveDirection == Direction.UP && peeked != null
					&& nextPos.subtract(peeked).getY() == 0) {
				addJumpMoveTask(currentPos, peeked);
				nextPos = peeked;
				path.removeFirst();
			} else if (nextPos.getY() > currentPos.getY()
					&& nextPos.getX() == currentPos.getX()
					&& nextPos.getZ() == currentPos.getZ()) {
				addTask(new UpwardsMoveTask(nextPos, new BlockItemFilter(
						upwardsBuildBlocks)));
			} else if (nextPos.getY() > currentPos.getY()) {
				addJumpMoveTask(currentPos, nextPos);
			} else if (nextPos.getY() < currentPos.getY()
					&& nextPos.getX() == currentPos.getX()
					&& nextPos.getZ() == currentPos.getZ()) {
				addTask(new DownwardsMoveTask(nextPos));
			} else {
				addTask(new HorizontalMoveTask(nextPos));
			}
			currentPos = nextPos;
		}
		currentTarget = currentPos;
		addTasksForTarget(currentPos);
	}

	private void addJumpMoveTask(BlockPos nextPos, final BlockPos peeked) {
		addTask(new JumpMoveTask(peeked, nextPos.getX(), nextPos.getZ()));
	}

	private void addHorizontalFastMove(BlockPos currentPos, BlockPos nextPos) {
		LOGGER.debug("Using fast move from " + currentPos + " to " + nextPos);
		addTask(new WalkTowardsTask(nextPos.getX(), nextPos.getZ(),
				currentPos));
	}

	/**
	 * Could we take a shortcut without any objects in the way.
	 * 
	 * @param pos1
	 * @param pos2
	 * @return
	 */
	private boolean isAreaClear(BlockPos pos1, BlockPos pos2) {
		if (pos1.getY() != pos2.getY()) {
			return false;
		}
		BlockPos min = Pos.minPos(pos1, pos2);
		BlockPos max = Pos.maxPos(pos1, pos2);
		int y = pos1.getY();
		for (int x = min.getX(); x <= max.getX(); x++) {
			for (int z = min.getZ(); z <= max.getZ(); z++) {
				if (!BlockSets.SAFE_GROUND.isAt(world, x, y - 1, z)
						|| !BlockSets.SAFE_CEILING.isAt(world, x, y + 2, z)
						|| !BlockSets.FEET_CAN_WALK_THROUGH
								.isAt(world, x, y, z)
						|| !BlockSets.HEAD_CAN_WALK_THROUGH.isAt(world, x,
								y + 1, z)) {
					return false;
				}
			}
		}
		return true;
	}

	private Direction direction(BlockPos currentPos, final BlockPos nextPos) {
		BlockPos delta = nextPos.subtract(currentPos);
		if (delta.getY() != 0 && (delta.getX() != 0 || delta.getZ() != 0)) {
			delta = new BlockPos(delta.getX(), 0, delta.getZ());
		}
		return AIHelper.getDirectionFor(delta);
	}

	protected void addTasksForTarget(BlockPos currentPos) {
	}

	@Override
	protected int distanceFor(int from, int to) {
		int distance = 1;
		int toX = getX(to);
		int toY = getY(to);
		int toZ = getZ(to);
		int fromY = getY(from);
		if (fromY > toY && (toX != getX(from) || toY != getY(from))) {
			// sideward down
			distance += materialDistance(toX, toY + 2, toZ, false);
			distance += materialDistance(toX, toY + 1, toZ, false);
			distance += materialDistance(toX, toY, toZ, true);
			distance += 1;
		} else if (fromY > toY && (toX != getX(from) || toY != getY(from))) {
			// sideward up
			distance += materialDistance(getX(from), toY + 1, getZ(from), false);
			distance += materialDistance(toX, toY + 1, toZ, false);
			distance += materialDistance(toX, toY, toZ, true);
			distance += 1;
		} else {
			if (fromY >= toY) {
				distance += materialDistance(toX, toY, toZ, true);
			} else {
				distance += 2;
			}
			if (fromY <= toY) {
				distance += materialDistance(toX, toY + 1, toZ, false);
			} else {
				distance += 2;
			}
		}
		return distance;
	}

	protected int materialDistance(int x, int y, int z, boolean asFloor) {
		if (asFloor && shortFootBlocks.isAt(world, x, y, z) || !asFloor
				&& shortHeadBlocks.isAt(world, x, y, z)) {
			return 0;
		} else if (fastDestructableBlocks.isAt(world, x, y, z)) {
			// fast breaking gives bonus.
			return 1;
		} else {
			return 2;
		}
	}

	public BlockPos getCurrentTarget() {
		return currentTarget;
	}

}
