package org.mahu.proto.lifecycle;

import java.util.concurrent.ThreadFactory;

public interface IThreadFactoryFactory {
    
    public ThreadFactory createNamedEventBusThreadFactory(final String name);

}
