package org.mahu.proto.proxytest;

import java.io.Serializable;

public interface TestInterface2 {
	
	class RequestData implements Serializable {
		private String name;
		private byte[] bytes;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public byte[] getBytes() {
			return bytes;
		}

		public void setBytes(byte[] bytes) {
			this.bytes = bytes;
		} 
	}
	
	class ResponseData implements Serializable {
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
