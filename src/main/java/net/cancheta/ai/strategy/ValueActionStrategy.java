package net.cancheta.ai.strategy;

import net.cancheta.ai.AIHelper;

public abstract class ValueActionStrategy extends AIStrategy{
	
	protected abstract double getValue(AIHelper helper);
}
