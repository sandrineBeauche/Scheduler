package scheduler.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.jackson.JacksonFeature;

import scheduler.engine.ScriptSnapshot;
import scheduler.engine.Task;
import scheduler.rest.SchedulerObjectMapperProvider;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Convenient client for the scheduler rest api.
 * @author Sandrine Ben Mabrouk.
 *
 */
public class SchedulerClient {

	/**
	 * underlying client from Jersey.
	 */
	protected WebTarget mainTarget;
	
	/**
	 * underlying JSON mapper from Jackson.
	 */
	protected ObjectMapper mapper;
	
	
	/**
	 * Creates a new client that target the scheduler rest api on a host and port.
	 * @param host the host where the scheduler rest api is available.
	 * @param port the port where the scheduler rest api is available.
	 */
	public SchedulerClient(String host, String port) {
		ClientConfig clientConfig = new ClientConfig();
		clientConfig.register(JacksonFeature.class, SchedulerObjectMapperProvider.class);
		 
		Client client = ClientBuilder.newClient(clientConfig);
		
		WebTarget webTarget = client.target("http://" + host + ":" + port + "/scheduler/rest/api");
		this.mainTarget = webTarget.path("scheduler/task");
		
		this.mapper = SchedulerObjectMapperProvider.getNewObjectMapper();
	}
	
	
	/**
	 * Submit a script the to scheduler rest api with the request POST /scheduler/task
	 * @param scriptContent the content script to be executed of the scheduler.
	 * @return the id of the new created task.
	 * @throws IOException if an error occurs during reading the scheduler response.
	 */
	public Long submitScript(String scriptContent) throws IOException{
		Entity<String> scriptEntity = Entity.entity(scriptContent, MediaType.TEXT_PLAIN);
		Response response = this.mainTarget.request(MediaType.TEXT_PLAIN).post(scriptEntity);
		if(response.getStatus() == 201){
			try(BufferedReader buf = new BufferedReader(new InputStreamReader(response.readEntity(InputStream.class)))) {
				String result = buf.readLine();
				return Long.valueOf(result);
			} 
		}
		else{
			return null;
		}
	}
	
	/**
	 * Gets the list of running tasks on the scheduler.
	 * @return the list of running tasks on the scheduler.
	 */
	public List<Task> getRunningTasks(){
		GenericType<List<Task>> gType = new GenericType<List<Task>>(){}; 
		List<Task> result = mainTarget.path("running").request().get(gType);
		return result;
	}
	
	/**
	 * Gets the list of finished tasks on the scheduler.
	 * @return the list of finished tasks on the scheduler.
	 */
	public List<Task> getFinishedTasks(){
		GenericType<List<Task>> gType = new GenericType<List<Task>>(){}; 
		List<Task> result = mainTarget.path("finished").request().get(gType);
		return result;
	}
	
	/**
	 * Get the status and the result of a given task if this task is finished.
	 * @param idTask the related task id.
	 * @return a snapshot that indicates the current status of the task and its result if finished.
	 * @throws JsonParseException occurs when non-well-formed content (content that does not conform to JSON syntax as per specification) is encountered.
	 * @throws JsonMappingException signal fatal problems with mapping of content.
	 * @throws IOException if I/O error occurs.
	 */
	public ScriptSnapshot getResultTask(Long idTask) throws JsonParseException, JsonMappingException, IOException{
		Response response = this.mainTarget.path(idTask + "/status").request(MediaType.APPLICATION_JSON).get();
		ScriptSnapshot snapshot = this.mapper.readValue(response.readEntity(InputStream.class), ScriptSnapshot.class);
		return snapshot;
	}
	
	/**
	 * Delete a given task
	 * @param idTask the related task id.
	 */
	public void deleteTask(Long idTask){
		Response response = this.mainTarget.path(idTask.toString()).request().delete();
	}
	

	public static void main(String[] args) throws IOException, InterruptedException {
		SchedulerClient client = new SchedulerClient("localhost", "8080");
		Long id = client.submitScript("return 1+2");
		System.out.println("Submitted a script with id " + id);
		
		Thread.sleep(500);
		
		List<Task> finished = client.getFinishedTasks();
		System.out.println("Finished tasks:");
		for(Task currentTask: finished){
			System.out.println(currentTask.getId());
		}
		System.out.println("\n");
		
		System.out.println("Get the result of the task");
		ScriptSnapshot snapshot = client.getResultTask(id);
		System.out.println(snapshot.getResult().toString());
		
		System.out.println("Delete the task");
		client.deleteTask(id);
	}

}
