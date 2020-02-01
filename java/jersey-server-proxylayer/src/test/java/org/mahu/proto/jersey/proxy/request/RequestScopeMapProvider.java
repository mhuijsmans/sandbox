package org.mahu.proto.jersey.proxy.request;

import java.util.HashMap;
import java.util.Map;

import org.mahu.proto.jersey.proxy.inject.IRequestScopeMapProvider;

import com.google.inject.Key;

public class RequestScopeMapProvider implements IRequestScopeMapProvider {

	@Override
	public Map<Key<?>, Object> getMap() {
		IRequestScopedData requestData = new RequestScopedDataOverrule();
        Map<Key<?>, Object> seedMap = new HashMap<>();
        seedMap.put(Key.get(IRequestScopedData.class), requestData);
        return seedMap;
	}

}
