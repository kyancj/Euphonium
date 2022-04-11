package net.cancheta.ai.task.error;

public class TaskError {
	private final String message;
	
	protected TaskError(String message) { this.message = message; }
	
	public String getMesssage() { return message; }
	
	public boolean shouldBeDisplayed() { return true; }
}
