package net.cancheta.ai.strategy;

import net.cancheta.ai.AIHelper;
import net.minecraft.client.MinecraftClient;

public abstract class AIStrategy {
	public enum TickResult{
		TICK_AGAIN,
		TICK_HANDLED,
		NO_MORE_WORK,
		ABORT;
	};
	
	private boolean active;
	
	private boolean oldAutoJump;
	
	public final void setActive(boolean active, AIHelper helper) {
		this.active = active;
		if (active) {
			//System.out.println("ACTIVATED" + helper.getResumeStrategy());
			onActivate(helper);
			System.out.println("ACTIVATED" + helper.getResumeStrategy());
		} else {
			System.out.println("DEACTIVATED" + helper.getResumeStrategy());
			onDeactivate(helper);
			//System.out.println("DEACTIVATED" + helper.getResumeStrategy());
		}
	}
	
	public final boolean isActive() { return active;}
	
	protected void onActivate(AIHelper helper) {
		MinecraftClient mc = helper.getClient();
		oldAutoJump = mc.options.autoJump;
		mc.options.autoJump = false;
	}
	
	protected void onDeactivate(AIHelper helper) {
		MinecraftClient mc = helper.getClient();
		mc.options.autoJump = oldAutoJump;
	}
	
	public boolean takesOverAnyTime() {
		return false;
	}
	
	public boolean checkShouldTakeOver(AIHelper helper) {
		if (active) {
			throw new IllegalStateException();
		}
		return true;
	}
	
	public final TickResult gameTick(AIHelper helper) {
		if (!active) {
			throw new IllegalStateException();
		}

		return onGameTick(helper);
	}
	
	protected abstract TickResult onGameTick(AIHelper helper);
	
	public String getDescription(AIHelper helper) {
		return "No description so far... " + getClass().getSimpleName();
	}
}
