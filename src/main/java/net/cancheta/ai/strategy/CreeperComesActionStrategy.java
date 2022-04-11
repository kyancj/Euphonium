package net.cancheta.ai.strategy;

import net.cancheta.ai.AIHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.CreeperEntity;

public class CreeperComesActionStrategy extends CloseEntityActionStrategy{

	@Override
	protected boolean matches(AIHelper helper, Entity player) {
		// TODO Auto-generated method stub
		return player instanceof CreeperEntity;
	}

	@Override
	protected TickResult onGameTick(AIHelper helper) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
