package scheduler.rest;

import java.util.List;

import javax.ws.rs.NotFoundException;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
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
 * Root resource (exposed at "myresource" path)
 */
@Path("scheduler/task")
public class TaskResource {

	static final Logger LOG = LoggerFactory.getLogger(TaskResource.class);
	
   
    
    protected void setResponseStatus(HttpServletResponse response, Status status){
    	response.setStatus(status.getStatusCode());
    	try {
            response.flushBuffer();
        }catch(Exception e){}
    }
    
    
    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public Long submit(String script, @Context final HttpServletResponse response){
    	LOG.info("submit the script " + script);
    	Task newTask = ScriptScheduler.getInstance().submitScript(script);
    	
    	setResponseStatus(response, Response.Status.CREATED);
    	
    	return newTask.getId();
    }
    
    
    @GET
    @Path("running")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Task> getRunning(){
    	return ScriptScheduler.getInstance().getRunningTasks();
    }
    
    
    @GET
    @Path("finished")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Task> getFinished(){
    	return ScriptScheduler.getInstance().getFinishedTasks();
    }
    
    
    @GET 
    @Path("{taskId}/result")
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
