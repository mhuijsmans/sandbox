package org.mahu.proto.restappext.service;

import org.mahu.proto.restappext.event.StartSessionCompletedEvent;
import org.mahu.proto.restappext.event.StartSessionEvent;

/**
 * Interface allows different implementations for ResourceManagement
 */
public interface ResourceManagementAlgorithm {

	/**
	 * Request resource
	 * 
	 * @param event
	 * @return true is release allocated, false otherwise.
	 */
	public boolean Allocate(final StartSessionEvent event);

	/**
	 * Releaseresource
	 * 
	 * @param event
	 */
	public void Release(final StartSessionCompletedEvent event);
}
