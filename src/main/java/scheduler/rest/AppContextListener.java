package scheduler.rest;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import scheduler.engine.ScriptScheduler;


/**
 * Listener for starting and shutdown of the application.
 * @author Sandrine Ben Mabrouk.
 *
 */
public class AppContextListener implements ServletContextListener {


	/**
	 * Callback for the application start.
	 * @param ServletContextEvent the dispatched event.
	 */
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		ScriptScheduler scheduler = ScriptScheduler.getInstance();
		scheduler.setMaxNbThread(5);
		scheduler.start();
	}

	
	/**
	 * Callback for the application shutdown.
	 * @param ServletContextEvent the dispatched event.
	 */
	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		ScriptScheduler.getInstance().shutdown();
	}

}
