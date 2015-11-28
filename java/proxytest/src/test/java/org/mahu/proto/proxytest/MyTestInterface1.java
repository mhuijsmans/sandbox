package org.mahu.proto.proxytest;

import java.util.logging.Logger;

public class MyTestInterface1 implements TestInterface1 {
	
	private static final Logger log = Logger
			.getLogger(MyTestInterface1.class.getName()); 

	public ResponseData process(TestInterface1.RequestData requestData) {
		log.info("process: "+ requestData);
		TestInterface1.ResponseData responseData = new TestInterface1.ResponseData();
		responseData.setName(requestData.getName());
		return responseData;
	}
	
}