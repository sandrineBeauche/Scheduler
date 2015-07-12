package scheduler.engine;

/**
 * An abstract factory that creates script tasks.
 * @author Sandrine Ben Mabrouk.
 *
 */
public abstract class ScriptTaskFactory {
	
	/**
	 * The current id available to create a new task.
	 */
	protected static long currentId = 0;
	
	/**
	 * Creates a new script task
	 * @param bodyString the content of the script to be executed.
	 * @return the new script task.
	 */
	public abstract AbstractScriptTask create(String bodyString);
}
