package scheduler.rest.test.integration;

import static org.junit.Assert.*;

import javax.ws.rs.core.Application;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;

import scheduler.rest.TaskResource;

public class MyResourceTest extends JerseyTest {

	@Override
    protected Application configure() {
        return new ResourceConfig(TaskResource.class);
    }
	
	@Test
    public void test1() {
        final String hello = target("myresource").request().get(String.class);
        assertEquals("Got it!", hello);
    }
	
}
