package org.mahu.proto.systemtest.sdapplication;

import java.util.logging.Logger;

public class SDControllerImpl implements SDController {

	protected static Logger log = Logger.getLogger(SDControllerImpl.class
			.getName());

	public SDControllerImpl() {
		log.info("ctor");
	}

	@Override
	public void start() {
		log.info("start ENTER");	
		log.info("start LEAVE");
	}

}
