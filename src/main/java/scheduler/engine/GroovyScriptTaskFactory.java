package scheduler.engine;


/**
 * A factory that creates Groovy script tasks.
 * @author Sandrine Ben Mabrouk.
 *
 */
public class GroovyScriptTaskFactory extends ScriptTaskFactory{

	@Override
	public AbstractScriptTask create(String bodyString) {
		currentId++;
		return new GroovyScriptTask(bodyString, currentId);
	}

}
