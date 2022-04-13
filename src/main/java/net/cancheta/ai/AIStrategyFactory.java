package net.cancheta.ai;

import net.cancheta.ai.strategy.AIStrategy;

public interface AIStrategyFactory {
	public AIStrategy produceStrategy(AIHelper helper);
}
