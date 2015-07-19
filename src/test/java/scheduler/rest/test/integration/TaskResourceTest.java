package scheduler.rest.test.integration;

import static org.hamcrest.Matchers.emptyIterable;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static scheduler.rest.test.ScriptSnapshotMatcher.equivalentSnapshot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.Collections;
import java.util.List;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.grizzly2.servlet.GrizzlyWebContainerFactory;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.DeploymentContext;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.glassfish.jersey.test.spi.TestContainer;
import org.glassfish.jersey.test.spi.TestContainerException;
import org.glassfish.jersey.test.spi.TestContainerFactory;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import scheduler.engine.AbstractScriptTask;
import scheduler.engine.ScriptScheduler;
import scheduler.engine.ScriptSnapshot;
import scheduler.engine.Task;
import scheduler.engine.TaskStatus;
import scheduler.engine.UnknownTaskException;
import scheduler.rest.SchedulerObjectMapperProvider;
import scheduler.rest.TaskResource;
import scheduler.rest.test.TestUtils;

public class TaskResourceTest extends JerseyTest {

	protected ScriptScheduler scheduler = null;
	
	@Override
    protected Application configure() {
		enable(TestProperties.LOG_TRAFFIC);
        ResourceConfig result = new ResourceConfig(TaskResource.class,  
        							SchedulerObjectMapperProvider.class);
        return result;
    }
	
	@Override
	protected void configureClient(ClientConfig config) {
	    config.register(JacksonFeature.class);
	}
	
	
	@Override
	protected TestContainerFactory getTestContainerFactory() throws TestContainerException {
		//return new GrizzlyTestContainerFactory();
	    return new TestContainerFactory() {

			@Override
			public TestContainer create(final URI baseUri, DeploymentContext deploymentContext) {
				return new TestContainer() {
	                private HttpServer server;

	                @Override
	                public ClientConfig getClientConfig() {
	                    return null;
	                }

	                @Override
	                public URI getBaseUri() {
	                    return baseUri;
	                }

	                @Override
	                public void start() {
	                    try {
	                        this.server = GrizzlyWebContainerFactory.create(
	                                baseUri, Collections.singletonMap("jersey.config.server.provider.packages", "scheduler.rest")
	                        );
	                    } catch (ProcessingException e) {
	                        throw new TestContainerException(e);
	                    } catch (IOException e) {
	                        throw new TestContainerException(e);
	                    }
	                }

	                @Override
	                public void stop() {
	                    this.server.stop();
	                }
	            };
			}
	    };
	}
	

	
	
	@Before
	public void setUp() throws Exception {
		ScriptScheduler.getInstance().setMaxNbThread(5);
		this.scheduler = ScriptScheduler.getInstance();
		this.scheduler.start();
		super.setUp();
	}
	
	
	@After
	public void tearDown() throws Exception {
		this.scheduler.shutdown();
		super.tearDown();
	}
	
	
	/**
	 * Test submitting a script to the scheduler.
	 * @throws IOException if an error occurs during the script reading.
	 */
	@Test
    public void testPostDummyScript() throws IOException {
		String script = TestUtils.readScriptFile("DummyScript.groovy");
		Entity<String> scriptEntity = Entity.entity(script, MediaType.TEXT_PLAIN);
		Response response = target("scheduler/task").request(MediaType.TEXT_PLAIN).post(scriptEntity);
		
		BufferedReader buf = new BufferedReader(new InputStreamReader(response.readEntity(InputStream.class)));
		String result = buf.readLine();
		buf.close();
		
		Long id = new Long(result);
		
		Assert.assertEquals(201, response.getStatus());
    }
	
	@Test
	public void testGetRunning() throws IOException{
		String script1 = TestUtils.readScriptFile("LongScript.groovy");
		String script2 = TestUtils.readScriptFile("DummyScript.groovy");
		
		Task task1 = scheduler.submitScript(script1);
		Task task2 = scheduler.submitScript(script1);
		Task task3 = scheduler.submitScript(script2);
		
		((AbstractScriptTask) task3).join();
		
		GenericType<List<Task>> gType = new GenericType<List<Task>>(){}; 
		List<Task> result = target("scheduler/task/running").request().get(gType);
		
		Assert.assertThat(result, hasSize(2));
		Assert.assertTrue(result.get(0).getScriptContent().equals(script1));
		Assert.assertTrue(result.get(1).getScriptContent().equals(script1));
		
		((AbstractScriptTask) task1).getFuture().cancel(true);
		((AbstractScriptTask) task2).getFuture().cancel(true);
	}
	
	
	/**
	 * Test getting the list of running tasks when the scheduler has no tasks. 
	 * @throws IOException if an error occurs during the script reading.
	 */
	@Test
	public void testGetRunningTasksEmpty() throws IOException {
		GenericType<List<Task>> gType = new GenericType<List<Task>>(){}; 
		List<Task> running = target("scheduler/task/running").request().get(gType);
		
		Assert.assertThat(running, emptyIterable());
	}
	
	
	/**
	 * Test getting the list of finished tasks.
	 * @throws IOException if an error occurs during the script reading.
	 */
	@Test
	public void testGetFinishedTasks() throws IOException {
		String script1 = TestUtils.readScriptFile("LongScript.groovy");
		String script2 = TestUtils.readScriptFile("DummyScript.groovy");
		
		Task task1 = scheduler.submitScript(script1);
		Task task2 = scheduler.submitScript(script1);
		Task task3 = scheduler.submitScript(script2);
		
		((AbstractScriptTask) task3).join();
		
		GenericType<List<Task>> gType = new GenericType<List<Task>>(){}; 
		List<Task> finished = target("scheduler/task/finished").request().get(gType);
		
		Assert.assertThat(finished, hasSize(1));
		Assert.assertTrue(finished.get(0).getScriptContent().equals(script2));
		
		((AbstractScriptTask) task1).getFuture().cancel(true);
		((AbstractScriptTask) task2).getFuture().cancel(true);
	}
	
	
	/**
	 * Test getting the list of finished tasks when the scheduler has no tasks. 
	 * @throws IOException if an error occurs during the script reading.
	 */
	@Test
	public void testGetFinishedTasksEmpty() throws IOException {
		GenericType<List<Task>> gType = new GenericType<List<Task>>(){}; 
		List<Task> finished = target("scheduler/task/finished").request().get(gType);
		
		Assert.assertThat(finished, emptyIterable());
	}
	
	
	
	/**
	 * Test removing a finished task.
	 * @throws IOException if an error occurs during the script reading.
	 * @throws UnknownTaskException if the task to remove does not exists.
	 */
	@Test
	public void testRemoveDummyTask() throws IOException, UnknownTaskException {
		String script = TestUtils.readScriptFile("DummyScript.groovy");
		AbstractScriptTask task = scheduler.submitScript(script);
		task.join();
		
		Response response = target("scheduler/task/" + task.getId()).request().delete();
		
		Assert.assertThat(response.getStatus(), equalTo(204));
		Assert.assertThat(scheduler.getFinishedTasks(), emptyIterable());
	}
	
	
	/**
	 * Test removing a running task.
	 * @throws IOException if an error occurs during the script reading.
	 * @throws UnknownTaskException if the task to remove does not exists.
	 */
	@Test
	public void testRemoveLongTask() throws IOException, UnknownTaskException {
		String script = TestUtils.readScriptFile("LongScript.groovy");
		
		AbstractScriptTask task = scheduler.submitScript(script);
		
		Response response = target("scheduler/task/" + task.getId()).request().delete();
		
		Assert.assertThat(response.getStatus(), equalTo(204));
		
		Assert.assertThat(scheduler.getRunningTasks(), emptyIterable());
		Assert.assertThat(scheduler.getFinishedTasks(), emptyIterable());
		
		task.join();
	}
	
	
	/**
	 * Test removing an unknown task.
	 * @throws IOException if an error occurs during the script reading.
	 */
	@Test
	public void testRemoveUnknownTask() throws IOException{
		String script = TestUtils.readScriptFile("DummyScript.groovy");
	
		AbstractScriptTask task = scheduler.submitScript(script);
		
		Long id = task.getId() + 1;
		
			
		Response response = target("scheduler/task/" + id).request().delete();
			
		Assert.assertThat(response.getStatus(), equalTo(404));
		
		task.join();
	}
	
	
	
	/**
	 * Test groovy script launching and get the result with a snapshot.
	 * @throws IOException if an error occurs during the script reading.
	 */
	@Test
	public void testGetResultLongScript() throws IOException {
		String script = TestUtils.readScriptFile("LongScript.groovy");
		AbstractScriptTask task = scheduler.submitScript(script);
		
		ScriptSnapshot snapshot = target("scheduler/task/" + task.getId() + "/status").request().get(ScriptSnapshot.class);
		
		ScriptSnapshot expected = new ScriptSnapshot(TaskStatus.RUNNING, 
													null);
		
		Assert.assertThat(snapshot, samePropertyValuesAs(expected));
		
		((AbstractScriptTask) task).getFuture().cancel(true);
	}
	
	
	/**
	 * Test groovy script launching and get the result with a snapshot.
	 * @throws IOException if an error occurs during the script reading.
	 */
	@Test
	public void testGetResultCancelledScript() throws IOException {
		String script = TestUtils.readScriptFile("LongScript.groovy");
		AbstractScriptTask task = scheduler.submitScript(script);
		((AbstractScriptTask) task).getFuture().cancel(true);
		
		ScriptSnapshot snapshot = target("scheduler/task/" + task.getId() + "/status").request().get(ScriptSnapshot.class);
		
		ScriptSnapshot expected = new ScriptSnapshot(TaskStatus.CANCELLED, 
													null);
		
		Assert.assertThat(snapshot, samePropertyValuesAs(expected));
	}
	
	
	/**
	 * Test groovy script launching and get the result with a snapshot.
	 * @throws IOException if an error occurs during the script reading.
	 */
	@Test
	public void testGetResultDummyScript() throws IOException {
		String script = TestUtils.readScriptFile("DummyScript.groovy");
		AbstractScriptTask task = scheduler.submitScript(script);
		
		Response response = target("scheduler/task/" + task.getId() + "/status").request().get();
		ScriptSnapshot snapshot = SchedulerObjectMapperProvider.getNewObjectMapper()
			.readValue(response.readEntity(InputStream.class), ScriptSnapshot.class);
		
		ScriptSnapshot expected = new ScriptSnapshot(TaskStatus.SUCCESSFULLY_DONE, 
													"Inside DummyScript");
		
		Assert.assertThat(snapshot, samePropertyValuesAs(expected));
		
		((AbstractScriptTask) task).getFuture().cancel(true);
	}
	
	
	/**
	 * Test groovy script launching and get the result with a snapshot.
	 * @throws IOException if an error occurs during the script reading.
	 */
	@Test
	public void testGetResultExceptionScript() throws IOException {
		String script = TestUtils.readScriptFile("ExceptionScript.groovy");
		AbstractScriptTask task = scheduler.submitScript(script);
		
		Response response = target("scheduler/task/" + task.getId() + "/status").request().get();
		ScriptSnapshot snapshot = SchedulerObjectMapperProvider.getNewObjectMapper()
				.readValue(response.readEntity(InputStream.class), ScriptSnapshot.class);
		
		Assert.assertThat(snapshot, equivalentSnapshot(TaskStatus.ERROR, 
				ArithmeticException.class));
		
		((AbstractScriptTask) task).getFuture().cancel(true);
	}
	
	
	/**
	 * Test groovy script launching and get the result with a snapshot.
	 * @throws IOException if an error occurs during the script reading.
	 */
	@Test
	public void testGetResultBeanScript() throws IOException {
		String script = TestUtils.readScriptFile("ComplexBeanScript.groovy");
		AbstractScriptTask task = scheduler.submitScript(script);
		
		Response response = target("scheduler/task/" + task.getId() + "/status").request().get();
		ScriptSnapshot snapshot = SchedulerObjectMapperProvider.getNewObjectMapper()
				.readValue(response.readEntity(InputStream.class), ScriptSnapshot.class);
		
		Assert.assertEquals(TaskStatus.SUCCESSFULLY_DONE, snapshot.getStatus());
		Assert.assertEquals("Person", snapshot.getResult().getClass().getName());
		
		((AbstractScriptTask) task).getFuture().cancel(true);
	}
}
