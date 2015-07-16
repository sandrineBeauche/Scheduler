package scheduler.engine;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * The script scheduler that manages the tasks.
 * @author Sandrine Ben Mabrouk
 *
 */
public class ScriptScheduler {
	
	/**
	 * The scripts managed by the scheduler.
	 */
	protected ConcurrentHashMap<Long, AbstractScriptTask> scripts;
	
	/**
	 * A thread pool to execute the scripts.
	 */
	protected ExecutorService threadPool = null;
	
	/**
	 * A factory that creates new script tasks.
	 */
	protected ScriptTaskFactory taskFactory = null;
	
	
	private static ScriptScheduler INSTANCE = null;

	public static final ScriptScheduler getInstance() {
	   if(INSTANCE == null){
		   INSTANCE = new ScriptScheduler(5);
	   }
	   return INSTANCE;
	}
	
	/**
	 * Creates a new script scheduler.
	 * @param maxThreads the number max of threads that executes the scripts.
	 */
	public ScriptScheduler(int maxThreads){
		this.scripts = new ConcurrentHashMap<Long, AbstractScriptTask>();
		this.threadPool = Executors.newFixedThreadPool(maxThreads);
		
		this.taskFactory = new GroovyScriptTaskFactory();
	}
	
	/**
	 * Submit a new script to the scheduler in order to execute it.
	 * @param bodyScript the content of the script to be executed.
	 * @return the new created script task.
	 */
	public AbstractScriptTask submitScript(String bodyScript){
		AbstractScriptTask task = this.taskFactory.create(bodyScript);
		task.start(this.threadPool);
		this.scripts.put(task.getId(), task);
		return task;
	}
	
	/**
	 * Gets the tasks that are running.
	 * @return a list of running tasks.
	 */
	public ArrayList<Task> getRunningTasks(){
		ArrayList<Task> result = new ArrayList<Task>(this.scripts.size());
		for(Map.Entry<Long, AbstractScriptTask> current: this.scripts.entrySet()){
			AbstractScriptTask currentTask = current.getValue();
			if(!currentTask.getFuture().isDone()){
				result.add(current.getValue());
			}
		}
		return result;
	}
	
	
	/**
	 * Gets the tasks that are finished.
	 * @return a list of id corresponding to the finished tasks.
	 */
	public ArrayList<Task> getFinishedTasks(){
		ArrayList<Task> result = new ArrayList<Task>(this.scripts.size());
		for(Map.Entry<Long, AbstractScriptTask> current: this.scripts.entrySet()){
			AbstractScriptTask currentTask = current.getValue();
			if(currentTask.getFuture().isDone()){
				result.add(current.getValue());
			}
		}
		return result;
	}
	
	/**
	 * Removes a task from the scheduler
	 * @param id the id of the task to be removed.
	 * @throws UnknownTaskException occurs if the id does not exists in the scheduler.
	 */
	public void removeTask(Long id) throws UnknownTaskException{
		AbstractScriptTask task = this.getTask(id);
		if(!task.getFuture().isDone()){
			task.getFuture().cancel(true);
		}
		this.scripts.remove(id);
	}
	
	/**
	 * Get the task corresponding to the given id.
	 * @param id the id of the task to be retrieved.
	 * @return the corresponding task.
	 * @throws UnknownTaskException occurs if the id does not exists in the scheduler.
	 */
	public AbstractScriptTask getTask(Long id) throws UnknownTaskException{
		AbstractScriptTask result = this.scripts.get(id);
		if(result == null){
			throw new UnknownTaskException(id);
		}
		return result;
	}
	
	
	/**
	 * Shutdown the scheduler engine.
	 */
	public void shutdown(){
		this.threadPool.shutdown();
		this.scripts.clear();
	}
	
	
	
}
