package scheduler.rest.test;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;

import scheduler.engine.ScriptSnapshot;
import scheduler.engine.TaskStatus;

public class ScriptSnapshotMatcher extends BaseMatcher<ScriptSnapshot> {

	protected TaskStatus expectedStatus;
	
	protected Class expectedResultClass;
	
	public ScriptSnapshotMatcher(TaskStatus status, Class result) {
		this.expectedStatus = status;
		this.expectedResultClass = result;
	}

	
	protected boolean matchesSafely(ScriptSnapshot item) {
		return (item.getStatus() == this.expectedStatus && 
				item.getResult().getClass() == this.expectedResultClass);
	}


	@Override
	public boolean matches(Object item) {
		return this.matchesSafely((ScriptSnapshot) item);
	}


	@Override
	public void describeTo(Description description) {
		description.appendText("not the equivalent task snapshot");
	}
	
	@Factory
	public static <T> Matcher<ScriptSnapshot> equivalentSnapshot(TaskStatus status, Class resultClass) {
	   return new ScriptSnapshotMatcher(status, resultClass);
	 }
	
	

}
