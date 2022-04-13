package net.cancheta.ai.strategy;

import net.cancheta.ai.AIHelper;
import net.cancheta.ai.path.MovePathFinder;
import net.cancheta.ai.path.world.WorldData;
import net.cancheta.ai.path.world.WorldWithDelta;
import net.cancheta.ai.task.AITask;
import net.cancheta.ai.task.WaitTask;
import net.cancheta.ai.task.error.TaskError;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;

public class PathFinderStrategy extends TaskStrategy {
	private static final boolean DEBUG = false;
	private final MovePathFinder pathFinder;
	private final String description;
	private boolean inShouldTakeOver;
	private boolean noPathFound;
	protected WorldData pathFindingWorld;;

	// private final HealthWatcher watcher = new HealthWatcher();

	public PathFinderStrategy(MovePathFinder pathFinder, String description) {
		this.pathFinder = pathFinder;
		this.description = description;
	}

	@Override
	public void searchTasks(AIHelper helper) {
		if (pathFindingWorld != null) {
			pathFinder.abort();
		}

		pathFindingWorld = helper.getWorld();
		if (isInAir(helper)) {
			addTask(new WaitTask(3)); // < 3 ticks should be enough for the game to keep up
			System.out.println("Player in the air!");
		} else if (!searchTasksWithPathfinder(helper)) {
			// Path finding needs more time
			System.out.println("Path finder needs time");
			if (!(noPathFound && inShouldTakeOver)) {
				System.out.println("Not no path found and not in should take over");
				addTask(new WaitTask(10));
			}
		} else {
			if (!hasMoreTasks()) {
				System.out.println("Not has more tasks");
				noPathFound = true;
			}
		}
		pathFindingWorld = null;
	}

	private boolean isInAir(AIHelper helper) {
		//return Minecraft.getInstance().player.isAirBorne; //Change to get block beneath and if not air.
		//55 for half-slabs
		MinecraftClient mc = helper.getClient();
		BlockPos under = new BlockPos(mc.player.getX(), mc.player.getY() - 0.55, mc.player.getZ());
		return mc.world.isAir(under);

		//Maybe Minecraft.getInstance() should be helper.getMinecraft().player ?

	}

	private boolean searchTasksWithPathfinder(AIHelper helper) {
		return pathFinder.searchSomethingAround(
				pathFindingWorld.getPlayerPosition(), helper, pathFindingWorld,
				this);
	}

	@Override
	protected TickResult onGameTick(AIHelper helper) {
		if (pathFindingWorld != null) {
			// do pre pathfinding
			if (searchTasksWithPathfinder(helper)) {
				pathFindingWorld = null;
			}
		}

		boolean wasInDesync = isDesync();
		TickResult tickResult = super.onGameTick(helper);
		// If we get a tick_again, we can start a new pathfinding.
		if (pathFindingWorld == null && tickResult == TickResult.TICK_AGAIN
				&& !wasInDesync && !isDesync() && tasks.size() < 9
				&& !tasks.isEmpty()) {
			WorldWithDelta world = new WorldWithDelta(helper.getWorld());
			debug("Applying DELTA");
			for (AITask t : tasks) {
				debug("Task: " + t);
				if (!t.applyToDelta(world)) {
					debug("--> Cannot pre-search tasks because " + t
							+ " does not support world deltas.");
					world = null;
					break;
				}
			}
			pathFindingWorld = world;
		} else if (tasks.size() < 9 && tickResult == TickResult.TICK_AGAIN) {
			debug("I want to presearch, but this requires "
					+ (pathFindingWorld == null) + "&&"
					+ (tickResult == TickResult.TICK_AGAIN) + "&&"
					+ (!wasInDesync) + "&&" + (!isDesync()) + "&&"
					+ (tasks.size() < 9) + "&&" + (!tasks.isEmpty()));
		}
		return tickResult;
	}

	private void debug(String string) {
		if (DEBUG) {
			System.out.println(string);
		}
	}

	@Override
	public boolean checkShouldTakeOver(AIHelper helper) {
		inShouldTakeOver = true;
		try {
			return super.checkShouldTakeOver(helper);
		} finally {
			inShouldTakeOver = false;
		}
	}

	@Override
	public String getDescription(AIHelper helper) {
		return description;
	}

	// @Override
	// public AITask getOverrideTask(AIHelper helper) {
	// final Pos pos = helper.getPlayerPosition();
	// final Block headBlock = helper.getBlock(pos.x, pos.y + 1, pos.z);
	// if (!helper.canWalkThrough(headBlock)) {
	// return new DestroyBlockTask(pos.x, pos.y + 1, pos.z);
	// }
	// final Block floorBlock = helper.getBlock(pos.x, pos.y, pos.z);
	// if (!helper.canWalkOn(floorBlock)) {
	// return new DestroyBlockTask(pos.x, pos.y, pos.z);
	// }
	// return watcher.getOverrideTask(helper.getMinecraft().player
	// .getHealth());
	// }

	@Override
	public String toString() {
		return "PathFinderStrategy [pathFinder=" + pathFinder
				+ ", description=" + description + "]";
	}

	@Override
	public boolean faceAndDestroyForNextTask() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void desync(TaskError selectTaskError) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addTask(AITask task) {
		// TODO Auto-generated method stub
		
	}

}