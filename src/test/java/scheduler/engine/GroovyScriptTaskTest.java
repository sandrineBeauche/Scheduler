package scheduler.engine;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static scheduler.rest.test.ScriptSnapshotMatcher.equivalentSnapshot;
import groovy.lang.MissingMethodException;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.script.ScriptException;

import org.codehaus.groovy.control.MultipleCompilationErrorsException;
import org.junit.Assert;
import org.junit.Test;

import scheduler.rest.test.TestUtils;

/**
 * 
 * @author Sandrine Ben Mabrouk.
 *
 */
public class GroovyScriptTaskTest {

	/**
	 * Test a successfully script execution.
	 * @throws IOException if an error occurs during the script reading.
	 * @throws ScriptException 
	 */
	@Test
	public void testCallDummy() throws IOException, ScriptException {
		String script = TestUtils.readScriptFile("DummyScript.groovy");
		GroovyScriptTask task = new GroovyScriptTask(script, 1L);
		Object result = task.call();
		
		String expected = "Inside DummyScript";
		
		Assert.assertEquals(expected, result);
	}
	
	
	/**
	 * Test the execution of an empty script.
	 * @throws IOException if an error occurs during the script reading.
	 * @throws ScriptException 
	 */
	@Test
	public void testCallEmpty() throws IOException, ScriptException {
		GroovyScriptTask task = new GroovyScriptTask("", 1L);
		Object result = task.call();
		
		Assert.assertNull(result);
	}
	
	
	/**
	 * Test a script execution that throws an exception.
	 * @throws IOException if an error occurs during the script reading.
	 */
	@Test
	public void testCallException() throws IOException {
		String script = TestUtils.readScriptFile("ExceptionScript.groovy");
		GroovyScriptTask task = new GroovyScriptTask(script, 1L);
		
		try{
			task.call();
			Assert.fail("should throw a ScriptException");
		}
		catch(ScriptException ex){
			Assert.assertThat(ex.getCause().getCause(), instanceOf(ArithmeticException.class));
		}
	}
	
	
	/**
	 * Test the execution of a script with compilation errors.
	 * @throws IOException if an error occurs during the script reading.
	 */
	@Test
	public void testCallCompilationError() throws IOException {
		GroovyScriptTask task = new GroovyScriptTask("retur 5", 1L);
		
		try{
			task.call();
			Assert.fail("should throw a MissingMethodExceptionException");
		}
		catch(ScriptException ex){
			Assert.assertThat(ex.getCause().getCause(), instanceOf(MissingMethodException.class));
		}
	}
	
	
	/**
	 * Test the execution of a script with compilation errors.
	 * @throws IOException if an error occurs during the script reading.
	 */
	@Test
	public void testCallCompilationError2() throws IOException {
		GroovyScriptTask task = new GroovyScriptTask("5++4", 1L);
		try{
			task.call();
			Assert.fail("should throw a MultipleCompilationErrorsException");
		}
		catch(ScriptException ex){
			Assert.assertThat(ex.getCause(), instanceOf(MultipleCompilationErrorsException.class));
		}
	}
	
	
	/**
	 * Test groovy script launching and get the result with a snapshot.
	 * @throws IOException if an error occurs during the script reading.
	 */
	@Test
	public void testFutureDummy() throws IOException {
		String script = TestUtils.readScriptFile("DummyScript.groovy");
		GroovyScriptTask task = new GroovyScriptTask(script, 1L);
		
		ExecutorService executor = Executors.newSingleThreadExecutor();
		task.start(executor);
		task.join();
		
		ScriptSnapshot snapshot = task.getSnapshot();
		
		ScriptSnapshot expected = new ScriptSnapshot(TaskStatus.SUCCESSFULLY_DONE, 
													"Inside DummyScript");
		
		Assert.assertThat(snapshot, samePropertyValuesAs(expected));
	}
	
	
	/**
	 * Test groovy script launching that throws an error and get the result with a snapshot.
	 * @throws IOException if an error occurs during the script reading.
	 */
	@Test
	public void testFutureException() throws IOException {
		String script = TestUtils.readScriptFile("ExceptionScript.groovy");
		GroovyScriptTask task = new GroovyScriptTask(script, 1L);
		
		ExecutorService executor = Executors.newSingleThreadExecutor();
		task.start(executor);
		task.join();
		
		ScriptSnapshot snapshot = task.getSnapshot();
		
		Assert.assertThat(snapshot, equivalentSnapshot(TaskStatus.ERROR, 
														ArithmeticException.class));
	}
	
	
	/**
	 * Test an empty groovy script launching and get the result with a snapshot.
	 * @throws IOException if an error occurs during the script reading.
	 */
	@Test
	public void testFutureEmpty() throws IOException {
		GroovyScriptTask task = new GroovyScriptTask("", 1L);
		
		ExecutorService executor = Executors.newSingleThreadExecutor();
		task.start(executor);
		task.join();
		
		ScriptSnapshot snapshot = task.getSnapshot();
		
		ScriptSnapshot expected = new ScriptSnapshot(TaskStatus.SUCCESSFULLY_DONE, 
													null);
		
		Assert.assertThat(snapshot, samePropertyValuesAs(expected));
	}
	
	/**
	 * Test the execution of a script with compilation errors.
	 * @throws IOException if an error occurs during the script reading.
	 */
	@Test
	public void testFutureCompilationError() throws IOException {
		GroovyScriptTask task = new GroovyScriptTask("retur 5", 1L);
		
		ExecutorService executor = Executors.newSingleThreadExecutor();
		task.start(executor);
		task.join();
		
		ScriptSnapshot snapshot = task.getSnapshot();
		
		Assert.assertThat(snapshot, equivalentSnapshot(TaskStatus.ERROR, MissingMethodException.class));
	}
	
	
	/**
	 * Test the execution of a script with compilation errors.
	 * @throws IOException if an error occurs during the script reading.
	 */
	@Test
	public void testFutureCompilationError2() throws IOException {
		GroovyScriptTask task = new GroovyScriptTask("5++4", 1L);
		ExecutorService executor = Executors.newSingleThreadExecutor();
		task.start(executor);
		task.join();
		
		ScriptSnapshot snapshot = task.getSnapshot();
		
		Assert.assertThat(snapshot, equivalentSnapshot(TaskStatus.ERROR, 
														MultipleCompilationErrorsException.class));
	}
	
	
	/**
	 * Test getting a snapshot during the execution of long script.
	 * @throws IOException if an error occurs during the script reading.
	 */
	@Test
	public void testFutureRunning() throws IOException {
		String script = TestUtils.readScriptFile("LongScript.groovy");
		GroovyScriptTask task = new GroovyScriptTask(script, 1L);
		
		ExecutorService executor = Executors.newSingleThreadExecutor();
		task.start(executor);
		
		ScriptSnapshot snapshot = task.getSnapshot();
		
		ScriptSnapshot expected = new ScriptSnapshot(TaskStatus.RUNNING, 
													null);
		
		Assert.assertThat(snapshot, samePropertyValuesAs(expected));
		
		task.join();
	}
	
	
	/**
	 * Test getting a snapshot of a cancelled script.
	 * @throws IOException if an error occurs during the script reading.
	 */
	@Test
	public void testFutureCancel() throws IOException {
		String script = TestUtils.readScriptFile("LongScript.groovy");
		GroovyScriptTask task = new GroovyScriptTask(script, 1L);
		
		ExecutorService executor = Executors.newSingleThreadExecutor();
		task.start(executor);
		task.getFuture().cancel(true);
		
		ScriptSnapshot snapshot = task.getSnapshot();
		
		ScriptSnapshot expected = new ScriptSnapshot(TaskStatus.CANCELLED, 
													null);
		
		Assert.assertThat(snapshot, samePropertyValuesAs(expected));
		
		task.join();
	}
}
