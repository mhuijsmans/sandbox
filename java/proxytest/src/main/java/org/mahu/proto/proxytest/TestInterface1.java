package org.mahu.proto.proxytest;

import java.io.Serializable;

public interface TestInterface1 {
	
	class RequestData implements Serializable {
		private static final long serialVersionUID = 9100812153039271528L;
		private String name;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		} 
	}
	
	class ResponseData implements Serializable {
		private static final long serialVersionUID = 4589921086023905855L;
		private String name;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		} 
	}
	
	public ResponseData process(RequestData requestData);

}
