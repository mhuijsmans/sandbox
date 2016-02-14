package javax.servlet;

public interface ServletContextListener {

	void contextInitialized(ServletContextEvent sce);

	void contextDestroyed(ServletContextEvent sce);

}
