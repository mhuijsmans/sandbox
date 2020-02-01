package org.mahu.proto.jersey.proxy.request;

import java.util.HashMap;
import java.util.Map;

import org.mahu.proto.jersey.proxy.inject.IRequestScopeMapProvider;

import com.google.inject.Key;

public class RequestScopeMapProviderThrowsException implements IRequestScopeMapProvider {
	
	RequestScopeMapProviderThrowsException() {
		throw new IllegalStateException();
	}

	@Override
	public Map<Key<?>, Object> getMap() {
        return new HashMap<>();
	}

}
