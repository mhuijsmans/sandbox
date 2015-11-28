package org.mahu.proto.systemtest.adapplication;

import java.util.logging.Logger;

import javax.ws.rs.core.MediaType;

import org.mahu.proto.jerseyjunittools.RestResource;
import org.mahu.proto.jerseyjunittools.RestResourceUri;
import org.mahu.proto.systemtest.CommonConst;
import org.mahu.proto.systemtest.baseapplication.BaseApplication;

public class ADApplication extends BaseApplication {
	static {
		BaseApplication.log = Logger.getLogger(ADApplication.class.getName());
	}

	public static void main(String[] args) throws InterruptedException {
		log.info("ENTER");
		try {
			//
			ADApplication app = new ADApplication();
			app.startNotifierService();
			app.startRestService(CommonConst.AD_PORT,
					new Class<?>[] { StartTaskResource.class },
					new ADControllerProvider());
			app.readSystemInUntilClosure();
			//
		} finally {
			log.info("LEAVE");
		}
		System.exit(0);
	}

	protected void rstCall() {
		RestResource<String> resource = new RestResource<String>(
				new RestResourceUri(CommonConst.getSdAppBaseURI(),
						CommonConst.AD_CONTEXT), String.class);
		resource.setMediaType(MediaType.TEXT_PLAIN);
		//
		resource.doGet();
		log.info("Response=" + resource.getResponseCode());
	}

	private ADApplication() {
		super(ADApplication.class.getSimpleName());
	}

}
