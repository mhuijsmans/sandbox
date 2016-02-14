package org.mahu.proto.lifecycle.impl;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class LifeCycleTaskExceptionTest {
    
    static class TestTask extends LifeCycleTask
    {
        public TestTask(LifeCycleTaskContext context) {
            super(context);
        }
        
    }

    @Test
    public void test() {
        Throwable t = new Throwable("Something awfull happene");
        LifeCycleTaskException exception = new LifeCycleTaskException(t, TestTask.class); 
        
        assertEquals(t, exception.getCause());
        assertEquals(TestTask.class, exception.getTaskClass());
    }

}
