package scheduler.rest;

import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import scheduler.engine.AbstractScriptTask;
import scheduler.engine.ScriptScheduler;
import scheduler.engine.ScriptSnapshot;
import scheduler.engine.Task;
import scheduler.engine.UnknownTaskException;

/**
 * Task resource that allows accessing to the scheduler and created tasks.
 * @author Sandrine Ben Mabrouk.
 */
@Path("scheduler/task")
public class TaskResource {

	/**
	 * Logger.
	 */
	static final Logger LOG = LoggerFactory.getLogger(TaskResource.class);
	
   
    /**
     * Override the response status with the given code.
     * @param response the response to be returned to the client.
     * @param status the new code status.
     */
    protected void setResponseStatus(HttpServletResponse response, Status status){
    	LOG.info("HTTP serlet response is of type " + response.getClass().getName());
    	response.setStatus(status.getStatusCode());
    	try {
            response.flushBuffer();
        }catch(Exception e){}
    }
    
    /**
     * Submit a new script.
     * @param script the script content
     * @param response the current servlet reponse, injected by Jersey.
     * @return the id of the created task.
     */
    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public Long submit(String script, @Context final HttpServletResponse response){
    	LOG.info("submit the script " + script);
    	Task newTask = ScriptScheduler.getInstance().submitScript(script);
    	
    	setResponseStatus(response, Response.Status.CREATED);
    	
    	return newTask.getId();
    }
    
    
    /**
     * Get the list of running tasks.
     * @return the list of running tasks.
     */
    @GET
    @Path("running")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Task> getRunning(){
    	return ScriptScheduler.getInstance().getRunningTasks();
    }
    
    /**
     * Get the list of finished tasks.
     * @return the list of finished tasks.
     */
    @GET
    @Path("finished")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Task> getFinished(){
    	return ScriptScheduler.getInstance().getFinishedTasks();
    }
    
    
    /**
     * Get the status and result of a task specified by its id.
     * @param id the task id.
     * @return the status and result of the task.
     * @throws NotFoundException if the task does not exist.
     */
    @GET 
    @Path("{taskId}/status")
    @Produces(MediaType.APPLICATION_JSON)
    public ScriptSnapshot getResult(@PathParam("taskId") long id) throws NotFoundException{
    	LOG.info("Get the result of task " + id);
    	
    	try {
			AbstractScriptTask task = ScriptScheduler.getInstance().getTask(id);
			return task.getSnapshot();
		} catch (UnknownTaskException e) {
			throw new NotFoundException("Task with id " + id + " not found");
		}
    }
    
    
    /**
     * Delete a task specified by its id.
     * @param id the task id.
     * @throws NotFoundException if the task does not exist.
     */
    @DELETE
    @Path("{taskId}")
    public void removeTask(@PathParam("taskId") long id) throws NotFoundException{
    	LOG.info("Remove the task " + id);
    	try {
			ScriptScheduler.getInstance().removeTask(id);
		} catch (UnknownTaskException e) {
			throw new NotFoundException("Task with id " + id + " not found");
		}
    }
    
}
