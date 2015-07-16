package scheduler.engine;


/**
 * A snapshot representing the current status of a task. If the task is finished, the snapshot gives also its result. 
 * @author Sandrine Ben Mabrouk
 *
 */
public class ScriptSnapshot {

	
	/**
	 * The task status
	 */
	protected TaskStatus status;
	
	/**
	 * The task result.
	 */
	protected Object result;
	
	
	public ScriptSnapshot(){}
	
	/**
	 * Creates a snapshot representing the status of a task.
	 * @param status the status of the task.
	 * @param result the result of the task.
	 */
	public ScriptSnapshot(TaskStatus status, Object result) {
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

	public void setStatus(TaskStatus status) {
		this.status = status;
	}

	public void setResult(Object result) {
		this.result = result;
	}
	
	

}
