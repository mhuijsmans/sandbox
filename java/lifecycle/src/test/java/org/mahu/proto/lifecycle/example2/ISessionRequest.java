package org.mahu.proto.lifecycle.example2;

import java.util.concurrent.CountDownLatch;

public interface ISessionRequest {

    public static final String RESPONSE = "response";

    public static class SessionRequestException extends RuntimeException {
        private static final long serialVersionUID = 1L;
        public SessionRequestException(String msg) {
            super(msg);
        }
    }
    
    public static class MeetUpLock {
        private final CountDownLatch signal1 = new CountDownLatch(1);
        private final CountDownLatch signal2 = new CountDownLatch(1);

        public void waitOnLockIsCalledWaitForContinue() {
            signal1.countDown();
            try {
                signal2.await();
            } catch (InterruptedException e) {
            }
        }
        
        public void waitForWaitOnLockCall() {
            try {
                signal1.await();
            } catch (InterruptedException e) {
            }
        } 
        
        public void signalWaitOnLockToContinue() {
            signal2.countDown();
        }        
    }

    String process(String data);

    void throwResourceWithMessage(String exceptionMessage);
    
    void waitOnLock(MeetUpLock lock);

}
