package net.cancheta.ai.strategy;

public abstract class AIStrategy {
	public enum TickResult{
		TICK_AGAIN,
		TICK_HANDLED,
		NO_MORE_WORK,
		ABORT;
	};
}
