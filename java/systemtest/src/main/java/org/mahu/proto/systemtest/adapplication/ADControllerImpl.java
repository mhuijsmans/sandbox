package org.mahu.proto.systemtest.adapplication;

import java.util.logging.Logger;

public class ADControllerImpl implements ADController {

	protected static Logger log = Logger.getLogger(ADControllerImpl.class
			.getName());

	public ADControllerImpl() {
		log.info("ctor");
	}

	@Override
	public void start() {
		log.info("start ENTER");	
		log.info("start LEAVE");
	}

}
