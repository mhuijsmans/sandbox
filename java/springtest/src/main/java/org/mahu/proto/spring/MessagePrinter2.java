package org.mahu.proto.spring;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.stereotype.Component;

@Component
public class MessagePrinter2 {

	@Inject
	@Named("instance1")
    private MessageService1 service1;
    
	@Inject
	@Named("instance1")
    private MessageService1 service2; 
	
	@Inject
	@Named("instance2")
    private MessageService1 service3;   
	
	/**
	 * Included to test if this class is also found during test
	 */
	private Dummy dummy = new Dummy();

    public void printMessage() {
        System.out.println(service1.getName()+" "+service2.getName()+" "+service3.getName());
    }

	public MessageService1 getService1() {
		return service1;
	}

	public MessageService1 getService2() {
		return service2;
	}

	public MessageService1 getService3() {
		return service3;
	}

	public Dummy getDummy() {
		return dummy;
	}
     
}