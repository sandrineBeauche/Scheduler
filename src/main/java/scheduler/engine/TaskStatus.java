package scheduler.engine;

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