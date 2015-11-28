package org.mahu.proto.proxytest;

import java.util.logging.Logger;

public class MyTestInterface2 implements TestInterface2 {

	private static final Logger log = Logger.getLogger(MyTestInterface1.class
			.getName());

	public ResponseData process(TestInterface2.RequestData requestData) {
		log.fine("process: " + requestData);
		TestInterface2.ResponseData responseData = new TestInterface2.ResponseData();
		responseData.setName(requestData.getName());
		return responseData;
	}

}
