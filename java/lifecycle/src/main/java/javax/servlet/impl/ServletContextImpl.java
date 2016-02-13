package javax.servlet.impl;

import java.util.HashMap;

import javax.servlet.ServletContext;

public class ServletContextImpl implements ServletContext {

    private HashMap<String, Object> attributes = new HashMap<>();

    @Override
    public void setAttribute(String name, Object object) {
        attributes.put(name, object);
        }

    @Override
    public Object getAttribute(String name) {
        return attributes.get(name);
    }

}
