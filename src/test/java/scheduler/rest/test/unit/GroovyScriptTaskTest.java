package scheduler.rest.test.unit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import groovy.lang.MissingMethodException;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.codehaus.groovy.control.MultipleCompilationErrorsException;
import org.junit.Assert;
import org.junit.Test;

import scheduler.engine.AbstractScriptTask.TaskStatus;
import scheduler.engine.GroovyScriptTask;
import scheduler.rest.test.TestUtils;

public class GroovyScriptTaskTest {

	/**
	 * Test a successfully script execution
	 * @throws Exception
	 */
	@Test
	public void testCallDummy() throws Exception {
		String script = TestUtils.readScriptFile("DummyScript.groovy");
		GroovyScriptTask task = new GroovyScriptTask(script, 1);
		Object result = task.call();
		
		String expected = "Inside DummyScript";
		
		Assert.assertEquals(expected, result);
		Assert.assertEquals(TaskStatus.SUCCESSFULLY_DONE, task.getStatus());
	}
	
	
	/**
	 * Test the execution of an empty script
	 * @throws Exception
	 */
	@Test
	public void testCallEmpty() throws Exception {
		GroovyScriptTask task = new GroovyScriptTask("", 1);
		Object result = task.call();
		
		Assert.assertNull(result);
		Assert.assertEquals(TaskStatus.SUCCESSFULLY_DONE, task.getStatus());
	}
	
	
	/**
	 * Test a script execution that throws an exception
	 * @throws Exception
	 */
	@Test
	public void testCallException() throws Exception {
		String script = TestUtils.readScriptFile("ExceptionScript.groovy");
		GroovyScriptTask task = new GroovyScriptTask(script, 1);
		
		Object result = task.call();
		assertThat(result, instanceOf(ArithmeticException.class));
		Assert.assertEquals(TaskStatus.ERROR, task.getStatus());
	}
	
	
	/**
	 * Test the execution of a script with compilation errors
	 * @throws Exception
	 */
	@Test
	public void testCallCompilationError() throws Exception {
		GroovyScriptTask task = new GroovyScriptTask("retur 5", 1);
		Object result = task.call();
		
		assertThat(result, instanceOf(MissingMethodException.class));
		Assert.assertEquals(TaskStatus.ERROR, task.getStatus());
	}
	
	
	/**
	 * Test the execution of a script with compilation errors
	 * @throws Exception
	 */
	@Test
	public void testCallCompilationError2() throws Exception {
		GroovyScriptTask task = new GroovyScriptTask("5++4", 1);
		Object result = task.call();
		
		assertThat(result, instanceOf(MultipleCompilationErrorsException.class));
		Assert.assertEquals(TaskStatus.ERROR, task.getStatus());
	}
	
	
	/**
	 * Test groovy script launching and get the result by the future
	 * @throws Exception
	 */
	@Test
	public void testFutureDummy() throws Exception {
		String script = TestUtils.readScriptFile("DummyScript.groovy");
		GroovyScriptTask task = new GroovyScriptTask(script, 1);
		
		ExecutorService executor = Executors.newSingleThreadExecutor();
		task.start(executor);
		
		String result = (String) task.getFuture().get();
		
		String expected = "Inside DummyScript";
		
		Assert.assertEquals(expected, result);
	}
	
	
	/**
	 * Test groovy script launching that throws an error and get the result by the future
	 * @throws Exception
	 */
	@Test
	public void testFutureException() throws Exception {
		String script = TestUtils.readScriptFile("ExceptionScript.groovy");
		GroovyScriptTask task = new GroovyScriptTask(script, 1);
		
		ExecutorService executor = Executors.newSingleThreadExecutor();
		task.start(executor);
		
		Object result = task.getFuture().get();
		
		assertThat(result, instanceOf(ArithmeticException.class));
	}
	
	
	/**
	 * Test an empty groovy script launching and get the result by the future
	 * @throws Exception
	 */
	@Test
	public void testFutureEmpty() throws Exception {
		GroovyScriptTask task = new GroovyScriptTask("", 1);
		
		ExecutorService executor = Executors.newSingleThreadExecutor();
		task.start(executor);
		
		Object result = task.getFuture().get();
		
		Assert.assertNull(result);
	}
	
	
}
