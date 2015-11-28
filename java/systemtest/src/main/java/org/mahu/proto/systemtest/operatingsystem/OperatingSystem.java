package org.mahu.proto.systemtest.operatingsystem;

import java.util.concurrent.FutureTask;
import java.util.logging.Logger;

import org.mahu.proto.systemtest.adapplication.ADApplication;
import org.mahu.proto.systemtest.baseapplication.BaseApplication;
import org.mahu.proto.systemtest.sdapplication.SDApplication;

public class OperatingSystem extends BaseApplication {
	static {
		log = Logger.getLogger(OperatingSystem.class.getName());
	}

	public static void main(String[] args) {
		try {
			log.info("ENTER");
			OperatingSystem os = new OperatingSystem();
			os.startNotifierService();
			os.startApplications();
			os.readSystemInUntilClosure();
		} finally {
			log.info("LEAVE");
		}
		System.exit(0);
	}

	OperatingSystem() {
		super(OperatingSystem.class.getSimpleName());
	}

	private void startApplications() {
		startApplication(SDApplication.class);
		startApplication(ADApplication.class);
		executor.execute(new FutureTask<Void>(new MonitorChildrenTerminate(
				children)));
	}

}
