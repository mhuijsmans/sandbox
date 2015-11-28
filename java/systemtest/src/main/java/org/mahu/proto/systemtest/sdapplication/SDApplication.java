package org.mahu.proto.systemtest.sdapplication;

import java.util.logging.Logger;

import org.mahu.proto.systemtest.CommonConst;
import org.mahu.proto.systemtest.baseapplication.BaseApplication;

public class SDApplication extends BaseApplication {
	static {
		BaseApplication.log = Logger.getLogger(SDApplication.class.getName());
	}

	public static void main(String[] args) {
		log.info("ENTER");
		try {
			//
			SDApplication os = new SDApplication();
			os.startNotifierService();
			os.startRestService(CommonConst.SD_PORT,
					new Class<?>[] { StartTaskResource.class },
					new SDControllerProvider());
			os.readSystemInUntilClosure();
		} finally {
			log.info("LEAVE");
		}
		System.exit(0);
	}

	SDApplication() {
		super(SDApplication.class.getSimpleName());
	}
}
