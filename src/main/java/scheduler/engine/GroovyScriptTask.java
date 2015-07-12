package scheduler.engine;

import javax.script.ScriptException;

import javax.script.ScriptEngine;

/**
 * A task that launches a Groovy script.
 * @author Sandrine Ben Mabrouk
 *
 */
public class GroovyScriptTask extends AbstractScriptTask {

	/**
	 * Creates a new task to launch a given Groovy script
	 * @param bodyScript the body of the script to be launched.
	 */
	public GroovyScriptTask(String bodyScript, long id) {
		super(bodyScript, id);
	}
	
	
	
	@Override
	public Object doCallExecution() throws ScriptException{
		ScriptEngine engine  = manager.getEngineByName("groovy");
	    Object result = engine.eval(this.bodyScript);
	    return result;
	}
	
	
	

}
