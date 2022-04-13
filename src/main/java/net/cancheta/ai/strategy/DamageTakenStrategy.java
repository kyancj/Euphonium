package net.cancheta.ai.strategy;

import net.cancheta.ai.AIHelper;
import net.minecraft.client.MinecraftClient;

public class DamageTakenStrategy extends ValueActionStrategy{
	
	@Override
	protected double getValue(AIHelper helper) { 
		MinecraftClient mc = helper.getClient();
		return mc.player.getHealth();
	}

	@Override
	protected TickResult onGameTick(AIHelper helper) {
		// TODO Auto-generated method stub
		return null;
	}
}
