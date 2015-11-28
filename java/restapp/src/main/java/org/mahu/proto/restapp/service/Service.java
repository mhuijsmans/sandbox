package org.mahu.proto.restapp.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mahu.proto.forkprocesstest.ProcessManager;

public class Service {

	final static Logger logger = LogManager.getLogger(Service.class.getName());

	final ProcessManager processManager;

	public Service(ProcessManager processManager) {
		this.processManager = processManager;
	}

	public void Start() throws InterruptedException {
		logger.info("Service.Start() ENTER");
		processManager.StartProcess(Service.class);
		logger.info("Service.Start() LEAVE");
	}

	public void Close() {
		logger.info("Service.Close()");
	}

	//
	public Service() {
		processManager = null;
	}

	public static void main(String[] args) {
		System.out.println("Service, new process started");
		System.exit(0);
	}

}
