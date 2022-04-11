package net.cancheta.ai.task;

import net.cancheta.ai.AIHelper;


/**
 * The task can do some work while other the bot is still working to the desired positon
 */
public interface CanWorkWhileApproaching {
	
	/**
	 * Do the approach work.
	 * @return <code>true</code> if the request was handled
	 */
	public boolean doApproachWork(AIHelper helper);
}
