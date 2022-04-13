package net.cancheta.ai.command;

import net.cancheta.ai.AIHelper;
import net.cancheta.ai.strategy.AIStrategy;
import net.minecraft.client.MinecraftClient;

public interface IAIControllable{
	default MinecraftClient getMinecraft() {
		return getAiHelper().getClient();
	}
	
	AIHelper getAiHelper();
	
	int requestUseStrategy(AIStrategy strategy);
	
	default int requestUseStrategy(AIStrategy strategy, SafeStrategyRule rule) {
		return requestUseStrategy(strategy);
	}
	
	StackBuilder getStackBuilder();
}
