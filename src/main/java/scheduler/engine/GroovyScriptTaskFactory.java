package scheduler.engine;


/**
 * A factory that creates Groovy script tasks.
 * @author Sandrine Ben Mabrouk.
 *
 */
public class GroovyScriptTaskFactory extends ScriptTaskFactory{

	/**
	 * Creates a Groovy script task.
	 * @see ScriptTaskFactory#create(String) create
	 */
	@Override
	public AbstractScriptTask create(String bodyString) {
		currentId++;
		return new GroovyScriptTask(bodyString, currentId);
	}

}
