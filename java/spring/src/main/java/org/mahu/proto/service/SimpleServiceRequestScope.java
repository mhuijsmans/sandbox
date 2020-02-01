package org.mahu.proto.service;

import org.mahu.proto.util.Utils;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

@Component
@Scope(value = "request", proxyMode = ScopedProxyMode.INTERFACES)
public final class SimpleServiceRequestScope implements ISimpleServiceRequestScope {
	
	// Non-static to get new instance
	private final String SIMPLE_CONTROLLER_XML = "simple-service-request-scope-xml."+Utils.nextId();

	public String getValueXml() {
		return SIMPLE_CONTROLLER_XML;
	}
	
}
