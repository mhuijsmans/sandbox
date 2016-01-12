package org.mahu.proto.lifecycle.impl;

import org.mahu.proto.lifecycle.PublicServiceKey;

public interface IPublicServiceKeyFactory {

    public <T> PublicServiceKey<T> createKey(Class<T> cls, T object);

}