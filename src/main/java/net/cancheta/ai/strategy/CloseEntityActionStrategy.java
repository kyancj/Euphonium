package net.cancheta.ai.strategy;

import net.cancheta.ai.AIHelper;
import net.minecraft.entity.Entity;

public abstract class CloseEntityActionStrategy extends ValueActionStrategy{
	@Override
	protected double getValue(final AIHelper helper) {
		final Entity closest = helper.getClosestEntity(50, player -> matches(helper, player));
		return closest == null ? Double.MAX_VALUE : closest.distanceTo(helper.getClient().player);
	}
	
	protected abstract boolean matches(AIHelper helper, Entity player);
}
