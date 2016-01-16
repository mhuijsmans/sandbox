package org.mahu.proto.lifecycle;

public interface IPublicServiceKeyFactory {

    public <T> PublicServiceKey<T> createKey(Class<T> cls, T object);

}