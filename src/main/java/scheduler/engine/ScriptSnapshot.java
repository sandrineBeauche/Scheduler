package scheduler.engine;

/**
 * A snapshot representing the current status of a task. If the task is finished, the snapshot gives also its result. 
 * @author Sandrine Ben Mabrouk
 *
 */
public class ScriptSnapshot {

	/**
	 * Status of the task that indicates of the task is finished successfully or with an exception
	 * @author Sandrine Ben Mabrouk
	 *
	 */
	public enum TaskStatus {
		/**
		 * The task is finished successfully
		 */
		SUCCESSFULLY_DONE,
		/**
		 * The task is finished with an exception
		 */
		ERROR,
		/**
		 * The task is running
		 */
		RUNNING,
		/**
		 * The task is cancelled.
		 */
		CANCELLED
	}
	
	/**
	 * The task status
	 */
	protected TaskStatus status;
	
	/**
	 * The task result.
	 */
	protected Object result;
	
	
	/**
	 * Creates a snapshot representing the status of a task.
	 * @param status the status of the task.
	 * @param result the result of the task.
	 */
	protected ScriptSnapshot(TaskStatus status, Object result) {
		this.status = status;
		this.result = result;
	}

	/**
	 * Gets the status from this snapshot.
	 * @return the status.
	 */
	public TaskStatus getStatus() {
		return status;
	}

	/**
	 * Gets the result from this snapshot.
	 * @return the snapshot.
	 */
	public Object getResult() {
		return result;
	}

}
