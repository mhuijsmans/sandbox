package org.mahu.proto.webguice.restconfig;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * This class is used for initialization of global data.
 */
public class ApplicationInitializeDestroy implements ServletContextListener {

    @Override
    public void contextInitialized(final ServletContextEvent sce) {
        sce.getServletContext().setAttribute(Injector.class.getName(), createApplicationInjector());
    }

    @Override
    public void contextDestroyed(final ServletContextEvent sce) {
    }

    public static Injector createApplicationInjector() {
        return Guice.createInjector(new ApplicationModule());
    }

}
