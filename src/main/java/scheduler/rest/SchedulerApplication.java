package scheduler.rest;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;

public class SchedulerApplication extends ResourceConfig {

	public SchedulerApplication() {
		super(
                TaskResource.class,
                // register Jackson ObjectMapper resolver
                SchedulerObjectMapperProvider.class,
                JacksonFeature.class
        );
	}
}
