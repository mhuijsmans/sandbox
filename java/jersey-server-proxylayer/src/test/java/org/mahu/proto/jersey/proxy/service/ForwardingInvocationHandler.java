package org.mahu.proto.jersey.proxy.service;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class ForwardingInvocationHandler implements InvocationHandler {
     
    private final Map<String, Method> methods = new HashMap<>();
 
    private Object target;
 
    public ForwardingInvocationHandler(Object target) {
        this.target = target;
 
        for(Method method: target.getClass().getDeclaredMethods()) {
            this.methods.put(method.getName(), method);
        }
    }
 
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object result = methods.get(method.getName()).invoke(target, args);
        return result;
    }
}
