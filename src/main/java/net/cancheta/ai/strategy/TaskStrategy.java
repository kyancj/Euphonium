package net.cancheta.ai.strategy;

import java.util.LinkedList;

import net.cancheta.ai.AIHelper;
import net.cancheta.ai.path.TaskReceiver;
import net.cancheta.ai.task.AITask;
import net.cancheta.ai.task.TaskOperations;
import net.cancheta.ai.task.error.StringTaskError;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public abstract class TaskStrategy extends AIStrategy implements TaskOperations, TaskReceiver{
	private static final Marker MARKER_PREFACING = MarkerManager
			.getMarker("preface");
	private static final Marker MARKER_TASK = MarkerManager
			.getMarker("task");
	private static final Logger LOGGER = LogManager.getLogger(AIStrategy.class);
	
	private int desyncTimer = 0;
	private boolean searchNewTasks = true;
	private AIHelper temporaryHelper;
	
	protected final LinkedList<AITask> tasks = new LinkedList<AITask>();
	private int taskTimeout;
	private volatile AITask activeTask;
	
	@Override
	protected TickResult onGameTick(AIHelper helper) {
		if (desyncTimer > 0) {
			activeTask = null;
			// clear the tasks
			for (AITask t : tasks) {
				t.onCanceled();
			}
			tasks.clear();
			LOGGER.debug(MARKER_TASK, "Waiting because of desync... " + desyncTimer);
			desyncTimer--;
			// pause for a tick, to reset all buttons, jump, ...
			return TickResult.TICK_HANDLED;
		}

		if (searchNewTasks) {
			activeTask = null;
			searchAndPrintTasks(helper);
			if (tasks.isEmpty()) {
				LOGGER.debug(MARKER_TASK, "No more tasks found.");
				return TickResult.NO_MORE_WORK;
			}
			taskTimeout = 0;
			searchNewTasks = false;
		} else if (tasks.isEmpty()) {
			searchNewTasks = true;
			activeTask = null;
			return TickResult.TICK_AGAIN;
		}

		final AITask task = tasks.peekFirst();
		if (task.isFinished(helper)) {
			LOGGER.trace(MARKER_TASK,"Task done: " + task);
			tasks.removeFirst();
			LOGGER.debug(MARKER_TASK,"Next task will be: " + tasks.peekFirst());
			taskTimeout = 0;
			activeTask = null;
			return TickResult.TICK_AGAIN;
		} else {
			int tickTimeout = task.getGameTickTimeout(helper);
			if (taskTimeout > tickTimeout) {
				LOGGER.error(MARKER_TASK, "Task timeout after {} ticks for: {}",
						tickTimeout, task);
				desync(new StringTaskError(
						"Task timed out. It should have been completed in "
								+ (tickTimeout / 20f) + "s"));
				activeTask = null;
				return TickResult.TICK_HANDLED;
			} else {
				temporaryHelper = helper;
				task.runTick(helper, this);
				temporaryHelper = null;
				taskTimeout++;
				activeTask = task;
				return TickResult.TICK_HANDLED;
			}
		}
	}
	
	private void searchAndPrintTasks(AIHelper helper) {
		searchTasks(helper);
		if (!tasks.isEmpty()) {
			LOGGER.trace(MARKER_TASK, "Found " + tasks.size() + " tasks, first task: "
					+ tasks.peekFirst());
		}
	}
	
	protected boolean hasMoreTasks() {
		return !tasks.isEmpty();
	}

	/**
	 * Searches for tasks to do. Always called in a game tick. The tasks should
	 * be added with {@link AIHelper#(AITask)}.
	 * <p>
	 * You cannot make any assumptions on the state of the world. Tasks may even
	 * have been interrupted in between. You can be sure that there are no tasks
	 * currently added when this method is called.
	 * 
	 * @param helper
	 *            The helper that can be used.
	 */
	protected abstract void searchTasks(AIHelper helper);
	
	public boolean isDesync() {
		return desyncTimer > 0;
	}
}
