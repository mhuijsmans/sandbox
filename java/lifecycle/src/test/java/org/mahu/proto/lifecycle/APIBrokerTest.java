package org.mahu.proto.lifecycle;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mahu.proto.lifecycle.example1.ExampleInterface3;
import org.mahu.proto.lifecycle.example1.impl.ExampleInterface3Impl;
import org.mahu.proto.lifecycle.impl.ApiBroker;

public class APIBrokerTest {

    ApiBroker broker;

    @Before
    public void createAPIBroker() {
        broker = new ApiBroker();
    }

    @After
    public void removeAPIBroker() {
        broker = null;
    }

    @Test
    public void resolveInterface_notRegistered_notPresent() {
        Optional<ExampleInterface3> example = broker.resolve(ExampleInterface3.class);

        assertFalse(example.isPresent());
    }

    @Test
    public void resolveInterface_registered_present() {
        broker.setPublicService(createServiceList(
                new PublicServiceKey<ExampleInterface3>(ExampleInterface3.class, new ExampleInterface3Impl())));
        Optional<ExampleInterface3> example = broker.resolve(ExampleInterface3.class);

        assertTrue(example.isPresent());
    }

    private List<PublicServiceKey<?>> createServiceList(final PublicServiceKey<?>... keys) {
        final List<PublicServiceKey<?>> services = new LinkedList<>();
        for (PublicServiceKey<?> key : keys) {
            services.add(key);
        }
        return services;
    }

}
