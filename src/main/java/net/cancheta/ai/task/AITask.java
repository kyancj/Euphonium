package net.cancheta.ai.task;

import net.cancheta.ai.AIHelper;
import net.cancheta.ai.path.world.RecordingWorld;
import net.cancheta.ai.path.world.WorldWithDelta;
import net.minecraft.client.MinecraftClient;

public abstract class AITask {
	
	private int gameTickTimeoutByBlockDestruction = -1;
	
	public abstract boolean isFinished(AIHelper aiHelper);

	public abstract void runTick(AIHelper aiHelper, TaskOperations taskOperations);

	public int getGameTickTimeout(AIHelper helper) {
		if (gameTickTimeoutByBlockDestruction < 0) {
			gameTickTimeoutByBlockDestruction = Math.max(
					computeGameTickTimeout(helper), 5);
		}

		return gameTickTimeoutByBlockDestruction;
	}
	
	protected int computeGameTickTimeout(AIHelper helper) {
		MinecraftClient mc = helper.getClient();
		RecordingWorld world = new RecordingWorld(helper.getWorld(),
				mc.player);
		if (applyToDelta(world)) {
			return (int) (world.getTimeInTicks() * 1.3f);
		} else {
			return 5 * 20;
		}
	}
	
	public boolean applyToDelta(WorldWithDelta world) {
		// default is unsupported.
		return false;
	}
	
	/**
	 * Called whenever this task was canceled.
	 */
	public void onCanceled() {
	}
}
