package org.mahu.proto.jersey.proxy.inject;

import java.util.HashMap;
import java.util.Map;

import com.google.inject.Key;

public class EmptyRequestScopeMap implements IRequestScopeMapProvider {

	@Override
	public Map<Key<?>, Object> getMap() {
		return new HashMap<>();
	}

}
