package net.cancheta.ai.task;

import net.cancheta.ai.AIHelper;
import net.cancheta.ai.path.world.WorldWithDelta;

@SkipWhenSearchingPrefetch
public class WaitTask extends AITask {

	private final int ticks;
	private int ticked;

	public WaitTask() {
		this(1);
	}

	public WaitTask(int ticks) {
		this.ticks = ticks;
	}

	@Override
	public boolean isFinished(AIHelper aiHelper) {
		return ticks <= ticked;
	}

	@Override
	public void runTick(AIHelper aiHelper, TaskOperations taskOperations) {
		ticked++;
	}

	@Override
	public String toString() {
		return "WaitTask [ticks=" + ticks + "]";
	}
	
	@Override
	public boolean applyToDelta(WorldWithDelta world) {
		return true;
	}
	
	@Override
	public int getGameTickTimeout(AIHelper helper) {
		return ticks + 5;
	}

}
