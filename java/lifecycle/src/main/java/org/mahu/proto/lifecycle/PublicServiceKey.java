package org.mahu.proto.lifecycle;

public class PublicServiceKey<T> {
    
    public final Class<T> interfaceClass;
    public final T object;
    
    public PublicServiceKey(final Class<T> interfaceClass, final T object) {
        this.interfaceClass = interfaceClass;
        this.object = object;
    }

}
