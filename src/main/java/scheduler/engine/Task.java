package scheduler.engine;


/**
 * An task represented as a java bean in order to be serialized and deserialized.
 * @author Sandrine Ben Mabrouk.
 *
 */
public class Task {

	/**
	 * The task id.
	 */
	protected Long id;
	
	/**
	 * The content of the script to be executed.
	 */
	protected String scriptContent;

	/**
	 * Gets the id of the task. This id is used to identify the task when interacting with the scheduler.
	 * @return the task id.
	 */
	public Long getId() {
		return id;
	}

	/**
	 * Sets the id of the task.
	 * @param id the new id of the task.
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * Get the content of the script to be executed.
	 * @return the content of the script to be executed.
	 */
	public String getScriptContent() {
		return scriptContent;
	}

	/**
	 * Sets the content of the script to be executed.
	 * @param scriptContent the script content.
	 */
	public void setScriptContent(String scriptContent) {
		this.scriptContent = scriptContent;
	}
	
	

}
