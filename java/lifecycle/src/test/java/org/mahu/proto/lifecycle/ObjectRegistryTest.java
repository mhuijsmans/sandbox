package org.mahu.proto.lifecycle;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;

import org.junit.Test;
import org.mahu.proto.lifecycle.impl.ObjectRegistry;

public class ObjectRegistryTest {

    private static class Class1 implements ILifeCycleService {

        @Override
        public void start() {
        }

        @Override
        public boolean stop() {
            return false;
        }

        @Override
        public void abort() {
        }

    }

    private static class Class2 implements IPublicService<String> {

        @Override
        public PublicServiceKey<String> getPublicServiceKey() {
            return null;
        }

    }

    private static class Class3 extends Class2 implements IPublicService<String> {

        @Override
        public PublicServiceKey<String> getPublicServiceKey() {
            return null;
        }

    }

    @Test
    public void objectCreated_differentTypeOfObject_correctState() {
        ObjectRegistry registry = new ObjectRegistry();

        registry.objectCreated(new Object());
        registry.objectCreated(new Class1());
        registry.objectCreated(new Class2());
        final Class3 class3 = new Class3();
        registry.objectCreated(class3);
        registry.objectCreated(new Class1());

        assertEquals(5, registry.getObjectCount());
        assertEquals(2, registry.getLifeCycleServiceCount());
        assertEquals(2, registry.getPublicServiceKeys().size());
        assertTrue(registry.getInstance(Class1.class).isPresent());
        assertFalse(registry.getInstance(String.class).isPresent());
        assertSame(class3, registry.getObject(3));
        Iterator<ILifeCycleService> it = registry.getLifeCycleServiceIterator();
        assertNotNull(it.next());
        assertNotNull(it.next());
        assertFalse(it.hasNext());
    }

    private void assertFalse(boolean hasNext) {
        // TODO Auto-generated method stub
        
    }

}
