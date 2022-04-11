package net.cancheta.ai.strategy;

import net.cancheta.ai.AIHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

public class PlayerComesActionStrategy extends CloseEntityActionStrategy{

	@Override
	protected boolean matches(AIHelper helper, Entity player) {
		// TODO Auto-generated method stub
		return player instanceof PlayerEntity && player != helper.getClient().player;
	}

	@Override
	protected TickResult onGameTick(AIHelper helper) {
		// TODO Auto-generated method stub
		return null;
	}

}
