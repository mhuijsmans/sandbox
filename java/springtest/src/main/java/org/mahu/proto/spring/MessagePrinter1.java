package org.mahu.proto.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MessagePrinter1 {

    @Autowired
    private MessageService2 service1;
    
    @Autowired
    private MessageService2 service2;    

    public void printMessage() {
        System.out.println(service1.getName()+" "+service2.getName());
    }
}