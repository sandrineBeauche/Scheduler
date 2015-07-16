package scheduler.engine;

import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A task that launches a script.
 * @author Sandrine Ben Mabrouk
 *
 */
public abstract class AbstractScriptTask extends Task implements Callable<Object> {

	
	/**
	 * A Future representing the task and allows to cancel it, get the current status of get the result.
	 */
	protected Future future = null;
	
	
	/**
	 * A manager that allows to get a ScriptEngine.
	 */
	protected static ScriptEngineManager manager = new ScriptEngineManager();
	
	/**
	 * Logger for a task.
	 */
	static final Logger LOG = LoggerFactory.getLogger(AbstractScriptTask.class);
	
	
	/**
	 * Creates a new task to launch a given script.
	 * @param bodyScript the body of the script to be launched.
	 * @param id the task id.
	 */
	public AbstractScriptTask(String bodyScript, Long id){
		this.scriptContent = bodyScript;
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
	 * Gets the Future representing this task.
	 * @return the Future representing this task.
	 */
	public Future getFuture(){
		return this.future;
	}
	
	
	/**
	 * Performs the script execution with a ScriptEngine.
	 * @return the result of the execution.
	 * @throws ScriptException occurs if the script throws an exception.
	 */
	protected abstract Object doCallExecution() throws ScriptException;
	
	
	/**
	 * Encapsulates the script execution in order to set the status of the task and capture exceptions.
	 * @see Callable#call Object
	 */
	@Override
	public Object call() throws ScriptException {
		LOG.info("starts the script with id " + this.id);
		Object result = doCallExecution();	
		LOG.info("finished the script with id " + this.id);
		return result;
	}
	
	
	/**
	 * Gets a snapshot of the task, that indicates its current status and its result if the task is finished.
	 * @return a snapshot of the task.
	 */
	public ScriptSnapshot getSnapshot(){
		if(this.future.isDone()){
			try{
				Object result = this.future.get();
				return new ScriptSnapshot(TaskStatus.SUCCESSFULLY_DONE, result);
			}
			catch(CancellationException ex){
				return new ScriptSnapshot(TaskStatus.CANCELLED, null);
			}
			catch(ExecutionException ex){
				Throwable cause = ex.getCause();
				while(cause instanceof ScriptException){
					cause = cause.getCause();
				}
				return new ScriptSnapshot(TaskStatus.ERROR, cause);
			} 
			catch (InterruptedException ex) {
				return new ScriptSnapshot(TaskStatus.ERROR, ex);
			}
		}
		else{
			return new ScriptSnapshot(TaskStatus.RUNNING, null);
		}
	}
	
	/**
	 * Waits for the end of th task.
	 */
	public void join(){
		if(!this.future.isDone()){
			try {
				this.future.get();
			} catch (InterruptedException | ExecutionException e) {
			}
		}
	}
}
