package net.cancheta.ai.strategy;

import net.cancheta.ai.AIHelper;

public class DamageTakenStrategy extends ValueActionStrategy{
	
	@Override
	protected double getValue(AIHelper helper) { return helper.getClient().player.getHealth();}

	@Override
	protected TickResult onGameTick(AIHelper helper) {
		// TODO Auto-generated method stub
		return null;
	}
}
