package net.cancheta.ai.strategy;

import net.cancheta.ai.AIHelper;

public class AbortOnDeathStrategy extends AIStrategy{
	@Override
	public boolean checkShouldTakeOver(AIHelper helper) {return !helper.isAlive();}
	
	@Override
	protected TickResult onGameTick(AIHelper helper) {
		// TODO Auto-generated method stub
		if (helper.isAlive()) {
			return TickResult.NO_MORE_WORK;
		} else {
			return TickResult.ABORT;
		}
	}
}
