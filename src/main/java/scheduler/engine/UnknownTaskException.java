/**
 * 
 */
package scheduler.engine;

/**
 * Occurs when trying to access to a task with an unknown id.
 * @author Sandrine Ben Mabrouk
 *
 */
@SuppressWarnings("serial")
public class UnknownTaskException extends Exception {


	/**
	 * @param id the unknown id
	 */
	public UnknownTaskException(Long id) {
		super("Unknown task with id " + id);
	}

	
}
