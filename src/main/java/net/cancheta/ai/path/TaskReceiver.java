package net.cancheta.ai.path;

import net.cancheta.ai.task.AITask;

public interface TaskReceiver {
	public abstract void addTask(AITask task);
}
