package org.mahu.proto.webguice.restconfig;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.mahu.proto.webguice.stm.IStateMachine;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * This class is used for initialization of global data.
 */
public class ApplicationInitializeDestroy implements ServletContextListener {

    @Override
    public void contextInitialized(final ServletContextEvent sce) {
        Injector injector = createApplicationInjector();
        IStateMachine stateMachine = injector.getInstance(IStateMachine.class);
        sce.getServletContext().setAttribute(Injector.class.getName(), createApplicationInjector());

        sce.getServletContext().setAttribute(IStateMachine.class.getName(), stateMachine);
    }

    @Override
    public void contextDestroyed(final ServletContextEvent sce) {
    }

    public static Injector createApplicationInjector() {
        return Guice.createInjector(new ApplicationModule());
    }

}
