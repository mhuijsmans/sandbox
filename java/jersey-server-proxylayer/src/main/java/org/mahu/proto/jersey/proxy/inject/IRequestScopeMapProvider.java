package org.mahu.proto.jersey.proxy.inject;

import java.util.Map;

import com.google.inject.Key;

public interface IRequestScopeMapProvider {
	
	Map<Key<?>, Object> getMap();

}
