package org.mahu.proto.service;

import org.mahu.proto.util.Utils;
import org.springframework.stereotype.Component;

@Component
public final class SimpleService implements ISimpleService {
	
	// Non-static to get new instance
	private final String SIMPLE_CONTROLLER_TEXT = "simple-controller-text."+Utils.nextId();
	private final String SIMPLE_CONTROLLER_XML = "simple-controller-xml."+Utils.nextId();

	public String getValueXml() {
		return SIMPLE_CONTROLLER_XML;
	}

	public String getValueText() {
		return SIMPLE_CONTROLLER_TEXT;
	}	

}
