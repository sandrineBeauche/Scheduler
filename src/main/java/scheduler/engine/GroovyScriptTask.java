package scheduler.engine;

import javax.script.ScriptException;
import javax.script.ScriptEngine;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * A task that launches a Groovy script.
 * @author Sandrine Ben Mabrouk
 *
 */
public class GroovyScriptTask extends AbstractScriptTask {

	/**
	 * Creates a new task to launch a given Groovy script
	 * @param bodyScript the body of the script to be launched.
	 * @param id the task id.
	 */
	public GroovyScriptTask(String bodyScript, Long id) {
		super(bodyScript, id);
	}
	
	
	/**
	 * @see AbstractScriptTask#doCallExecution() Object
	 */
	@Override
	public Object doCallExecution() throws ScriptException{
		ScriptEngine engine  = manager.getEngineByName("groovy");
	    Object result = engine.eval(this.scriptContent);
	    return result;
	}
	
}
