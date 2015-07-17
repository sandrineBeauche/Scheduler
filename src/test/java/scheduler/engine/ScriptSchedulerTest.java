package scheduler.engine;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.emptyIterable;
import static org.hamcrest.Matchers.sameInstance;
import static org.hamcrest.Matchers.samePropertyValuesAs;

import java.io.IOException;
import java.util.ArrayList;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import scheduler.rest.test.TestUtils;

/**
 * 
 * @author Sandrine Ben Mabrouk.
 *
 */
public class ScriptSchedulerTest {
	
	protected ScriptScheduler scheduler = null;
	
	@BeforeClass
	public static void setUpClass() {
	  	ScriptScheduler.getInstance().setMaxNbThread(5);
	}
	
	
	@Before
	public void setUp() {
		this.scheduler = ScriptScheduler.getInstance();
		this.scheduler.start();
	}
	
	
	@After
	public void tearDown() {
		this.scheduler.shutdown();
	}
	
	
	/**
	 * Test submitting a script to the scheduler.
	 * @throws IOException if an error occurs during the script reading.
	 */
	@Test
	public void testSubmitScript() throws IOException {
		String script = TestUtils.readScriptFile("DummyScript.groovy");
		
		AbstractScriptTask task = scheduler.submitScript(script);
		
		task.join();
		
		ScriptSnapshot snapshot = task.getSnapshot();
		
		ScriptSnapshot expected = new ScriptSnapshot(TaskStatus.SUCCESSFULLY_DONE, 
													"Inside DummyScript");
		
		Assert.assertThat(snapshot, samePropertyValuesAs(expected));
	}
	
	
	/**
	 * Test submitting several long script to scheduler at the same time.
	 * @throws IOException if an error occurs during the script reading.
	 */
	@Test
	public void testSubmitSeveralScript() throws IOException {
		String script = TestUtils.readScriptFile("LongScript.groovy");
	
		AbstractScriptTask task1 = scheduler.submitScript(script);
		AbstractScriptTask task2 = scheduler.submitScript(script);
		
		task1.join();
		task2.join();
		
		ScriptSnapshot expected = new ScriptSnapshot(TaskStatus.SUCCESSFULLY_DONE, 
													7929);
		
		Assert.assertThat(task1.getSnapshot(), samePropertyValuesAs(expected));
		Assert.assertThat(task2.getSnapshot(), samePropertyValuesAs(expected));
	}

	
	/**
	 * Test getting the list of running tasks.
	 * @throws IOException if an error occurs during the script reading.
	 */
	@Test
	public void testGetRunningTasks() throws IOException {
		String script1 = TestUtils.readScriptFile("LongScript.groovy");
		String script2 = TestUtils.readScriptFile("DummyScript.groovy");
		
		Task task1 = scheduler.submitScript(script1);
		Task task2 = scheduler.submitScript(script1);
		Task task3 = scheduler.submitScript(script2);
		
		((AbstractScriptTask) task3).join();
		
		ArrayList<Task> running = scheduler.getRunningTasks();
		
		Assert.assertThat(running, containsInAnyOrder(task1, task2));
		
		((AbstractScriptTask) task1).getFuture().cancel(true);
		((AbstractScriptTask) task2).getFuture().cancel(true);
	}
	
	
	/**
	 * Test getting the list of running tasks when the scheduler has no tasks. 
	 * @throws IOException if an error occurs during the script reading.
	 */
	@Test
	public void testGetRunningTasksEmpty() throws IOException {
		
		ArrayList<Task> running = scheduler.getRunningTasks();
		
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
		
		ArrayList<Task> finished = scheduler.getFinishedTasks();
		
		Assert.assertThat(finished, containsInAnyOrder(task3));
		
		((AbstractScriptTask) task1).getFuture().cancel(true);
		((AbstractScriptTask) task2).getFuture().cancel(true);
	}
	
	
	/**
	 * Test getting the list of finished tasks when the scheduler has no tasks. 
	 * @throws IOException if an error occurs during the script reading.
	 */
	@Test
	public void testGetFinishedTasksEmpty() throws IOException {
		ArrayList<Task> finished = scheduler.getRunningTasks();
		
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
		
		scheduler.removeTask(task.getId());
		
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
		
		scheduler.removeTask(task.getId());
		
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
		try{
			
			scheduler.removeTask(id);
			Assert.fail("task " + id + " should not exists");
		}
		catch(UnknownTaskException ex){}
		
		task.join();
	}

	
	/**
	 * Test getting a task from the scheduler by its id.
	 * @throws IOException if an error occurs during the script reading.
	 * @throws UnknownTaskException if the task to be retrieved does not exists.
	 */
	@Test
	public void testGetTask() throws IOException, UnknownTaskException {
		String script = TestUtils.readScriptFile("DummyScript.groovy");
	
		AbstractScriptTask task = scheduler.submitScript(script);
		
		AbstractScriptTask result = scheduler.getTask(task.getId());
		
		Assert.assertThat(result, sameInstance(task));
	}
	
	/**
	 * Test getting a task that does not exists.
	 * @throws IOException if an error occurs during the script reading.
	 */
	@Test
	public void testGetUnknownTask() throws IOException {
		String script = TestUtils.readScriptFile("DummyScript.groovy");
		
		AbstractScriptTask task = scheduler.submitScript(script);
		Long id = task.getId() + 1;
		
		try{
			scheduler.getTask(id);
			Assert.fail("task " + id + " should not exists");
		}
		catch(UnknownTaskException ex){}
	}

}
