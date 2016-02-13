package org.mahu.proto.lifecycle;

import javax.servlet.impl.ServletContextEventImpl;
import javax.servlet.impl.ServletContextImpl;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.mahu.proto.lifecycle.ILifeCycleManager.LifeCycleState;
import org.mahu.proto.lifecycle.example1.impl.ApplicationInitializeDestroy;

public class InitDestroyTest {
    
    private final static int TESTCASE_TIMEOUT_IN_MS = 30 * 1000;    
    
    @Rule
    public Timeout globalTimeout = Timeout.seconds(30); 

	@Test
	public void contextInitializationAndDestroy() {
		ServletContextImpl servletContext = new ServletContextImpl();
		ServletContextEventImpl event = new ServletContextEventImpl(servletContext);
		ApplicationInitializeDestroy appInitDestroy = new ApplicationInitializeDestroy();
		appInitDestroy.contextInitialized(event);

		ILifeCycleManager lifeCycleManager = (ILifeCycleManager)servletContext.getAttribute(ILifeCycleManager.class.getName());
        TestUtils.pollingWait(() -> lifeCycleManager.getStatus().getState() == LifeCycleState.running, TESTCASE_TIMEOUT_IN_MS);		
		
		appInitDestroy.contextDestroyed(event);
        TestUtils.pollingWait(() -> lifeCycleManager.getStatus().getState() == LifeCycleState.shutdown, TESTCASE_TIMEOUT_IN_MS); 		
	}	

}
