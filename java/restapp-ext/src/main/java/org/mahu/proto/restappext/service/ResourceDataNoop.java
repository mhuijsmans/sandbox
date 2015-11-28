package org.mahu.proto.restappext.service;

import org.mahu.proto.restappext.event.StartWorkflowSessionEvent;

public class ResourceDataNoop implements ResourceData {

	public boolean AddResourceData(final StartWorkflowSessionEvent event) {
		// no data added
		return true;
	}

}
