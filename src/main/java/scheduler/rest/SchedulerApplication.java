package scheduler.rest;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;

/**
 * The Rest Application that registers resources and JSON provider.
 * @author Sandrine Ben Mabrouk.
 *
 */
public class SchedulerApplication extends ResourceConfig {

	/**
	 * Creates a scheduler rest application that registers JSON provider and custom object mapping.
	 */
	public SchedulerApplication() {
		super(
                TaskResource.class,
                // register Jackson ObjectMapper resolver
                SchedulerObjectMapperProvider.class,
                JacksonFeature.class
        );
	}
}
