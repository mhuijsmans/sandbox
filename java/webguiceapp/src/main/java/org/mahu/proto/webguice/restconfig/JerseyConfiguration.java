package org.mahu.proto.webguice.restconfig;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

import org.mahu.proto.webguice.rest.RestService;

public class JerseyConfiguration extends Application {
    @Override
    public Set<Class<?>> getClasses() {
        final Set<Class<?>> s = new HashSet<Class<?>>();
        s.add(RestService.class);
        return s;
    }

}