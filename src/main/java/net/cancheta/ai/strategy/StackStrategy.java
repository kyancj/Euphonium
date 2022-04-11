package net.cancheta.ai.strategy;

import net.cancheta.ai.AIHelper;

public class StackStrategy extends AIStrategy{
	private final StrategyStack stack;
	private boolean aborted = false;
	
	public StackStrategy(StrategyStack stack) {
		super();
		this.stack = stack;
	}
	
	@Override
	protected TickResult onGameTick(AIHelper helper) {
		TickResult tickResult = stack.gameTick(helper);
		if (tickResult == TickResult.ABORT) {
			aborted = true;
			return TickResult.NO_MORE_WORK;
		}
		return tickResult;
	}
	
	@Override
	public boolean checkShouldTakeOver(AIHelper helper) {
		return !aborted && stack.couldTakeOver(helper);
	}
	
	@Override
	protected void onActivate(AIHelper helper) {
		stack.resume(helper);
		super.onDeactivate(helper);
	}
	
	@Override
	protected void onDeactivate(AIHelper helper) {
		stack.pause(helper);
		super.onDeactivate(helper);
	}
}
