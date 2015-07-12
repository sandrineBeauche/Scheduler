package scheduler.engine;

import javax.script.ScriptException;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import javax.script.ScriptEngineManager;

/**
 * A task that launches a script.
 * @author Sandrine Ben Mabrouk
 *
 */
public abstract class AbstractScriptTask implements Callable<Object> {

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
		ERROR
	}
	
	
	protected long id;
	
	/**
	 * The body of the script to be executed
	 */
	protected String bodyScript = null;
	
	/**
	 * A Future representing the task and allows to cancel it, get the current status of get the result.
	 */
	protected Future future = null;
	
	/**
	 * The status of the task to indicate if the task is finished successfully or with an exception
	 */
	protected TaskStatus status = null;
	
	
	/**
	 * A manager that allows to get a ScriptEngine.
	 */
	protected static ScriptEngineManager manager = new ScriptEngineManager();
	
	
	/**
	 * Creates a new task to launch a given script
	 * @param bodyScript the body of the script to be launched.
	 */
	public AbstractScriptTask(String bodyScript, long id){
		this.bodyScript = bodyScript;
		this.id = id;
	}
	
	/**
	 * Starts the task with the given executor service.
	 * @param executor the executor that starts the task.
	 */
	public void start(ExecutorService executor){
		this.future = executor.submit(this);
	}
	
	
	/**
	 * Get the Future representing this task.
	 * @return the Future representing this task.
	 */
	public Future getFuture(){
		return this.future;
	}
	
	
	/**
	 * Gets the status of a finished task to indicate if this task was successfully done or threw an exception.
	 * @return the status of the task. Returns null if the task is not finished yet.
	 */
	public TaskStatus getStatus(){
		return this.status;
	}
	
	
	/**
	 * Gets the id of the task. This id is used to identify the task when interacting with the scheduler.
	 * @return the task id.
	 */
	public long getId(){
		return this.id;
	}
	
	
	/**
	 * Performs the script execution with a ScriptEngine
	 * @return the result of the execution
	 * @throws ScriptException occurs if the script throws an exception.
	 */
	protected abstract Object doCallExecution() throws ScriptException;
	
	
	/**
	 * Encapsulates the script execution in order to set the status of the task and capture exceptions.
	 */
	@Override
	public Object call() throws Exception {
		try{
			Object result = doCallExecution();
			this.status = TaskStatus.SUCCESSFULLY_DONE;
			return result;
		}
		catch(ScriptException ex){
			this.status = TaskStatus.ERROR;
			Throwable err = ex.getCause();
			if(err instanceof ScriptException){
				err = err.getCause();
			}
			return err;
		}
	}
}
