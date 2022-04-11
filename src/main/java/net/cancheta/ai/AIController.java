package net.cancheta.ai;

import net.cancheta.ai.command.IAIControllable;
import net.cancheta.ai.command.SafeStrategyRule;
import net.cancheta.ai.command.StackBuilder;
import net.cancheta.ai.strategy.AIStrategy;

public class AIController extends AIHelper implements IAIControllable{
	private final StackBuilder stackBuilder = new StackBuilder();
	
	private AIStrategy deactivatedStrategy;
	
	@Override
	public AIHelper getAiHelper() {
		return this;
	}

	@Override
	public int requestUseStrategy(AIStrategy strategy) {
		return requestUseStrategy(strategy, SafeStrategyRule.NONE);
	}

	@Override
	public StackBuilder getStackBuilder() {
		return stackBuilder;
	}

	@Override
	public AIStrategy getResumeStrategy() {
		return deactivatedStrategy;
	}
}
