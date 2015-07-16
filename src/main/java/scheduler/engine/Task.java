package scheduler.engine;



public class Task {

	protected Long id;
	
	protected String scriptContent;

	/**
	 * Gets the id of the task. This id is used to identify the task when interacting with the scheduler.
	 * @return the task id.
	 */
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getScriptContent() {
		return scriptContent;
	}

	public void setScriptContent(String scriptContent) {
		this.scriptContent = scriptContent;
	}
	
	

}
