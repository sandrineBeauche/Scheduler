package scheduler.rest;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import scheduler.engine.GroovyScriptTask;
import scheduler.engine.ScriptSnapshot;
import scheduler.engine.Task;
import scheduler.engine.TaskStatus;

/**
 * Root resource (exposed at "myresource" path)
 */
@Path("scheduler/task")
public class TaskResource {

	static final Logger LOG = LoggerFactory.getLogger(TaskResource.class);
	
    /**
     * Method handling HTTP GET requests. The returned object will be sent
     * to the client as "text/plain" media type.
     *
     * @return String that will be returned as a text/plain response.
     */
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getIt() {
    	LOG.trace("Start GetIt");
        return "Got it!";
    }
    
    
    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public Long submit(String script){
    	LOG.info("submit the script " + script);
    	return 3L;
    }
    
    
    @GET
    @Path("running")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Task> getRunning(){
    	ArrayList<Task> result = new ArrayList<Task>();
    	GroovyScriptTask task1 = new GroovyScriptTask("coucou", 1L);
    	GroovyScriptTask task2 = new GroovyScriptTask("sandrine", 2L);
    	GroovyScriptTask task3 = new GroovyScriptTask("t'es geniale", 3L);
    	
    	result.add(task1);
    	result.add(task2);
    	result.add(task3);
    	
    	return result;
    }
    
    
    @GET 
    @Path("{taskId}/result")
    @Produces(MediaType.APPLICATION_JSON)
    public ScriptSnapshot getResult(@PathParam("taskId") long id){
    	LOG.info("Get the result of task " + id);
    	ScriptSnapshot result =  new ScriptSnapshot();
    	result.setStatus(TaskStatus.SUCCESSFULLY_DONE);
    	result.setResult(new ArithmeticException("Division by zero"));
    	
    	return result;
    }
    
}
