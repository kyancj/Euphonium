package net.cancheta.ai.task;

import net.cancheta.ai.task.error.TaskError;

public interface TaskOperations {
	public boolean faceAndDestroyForNextTask();

	public void desync(TaskError selectTaskError);
}
