package org.mahu.proto.restappext.service;

import org.mahu.proto.restappext.event.StartSessionCompletedEvent;
import org.mahu.proto.restappext.event.StartSessionEvent;

/**
 * ResourceManagement, that returns true for every ALlocate request. 
 */
public class ResourceManagementNoLimit implements ResourceManagementAlgorithm {

	public boolean Allocate(final StartSessionEvent event) {
		return true;
	}

	public void Release(final StartSessionCompletedEvent event) {
		// empty
	}
}
