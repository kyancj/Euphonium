package net.cancheta.ai.strategy;

import net.cancheta.ai.AIHelper;

public abstract class AIStrategy {
	public enum TickResult{
		TICK_AGAIN,
		TICK_HANDLED,
		NO_MORE_WORK,
		ABORT
	};
	private boolean active;
	
	protected void onActivate(AIHelper helper) {
		
	}
	
	protected void onDeactivated(AIHelper helper) {
		
	}
	
	public final void setActive(boolean active, AIHelper helper){
		this.active = active;
		if (active) {
			
		}
	}
}
