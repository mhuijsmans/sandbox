package org.mahu.proto.restappextra.config;

public class SystemConfigurationImpl implements SystemConfiguration {
	
	private final int vPort;
	private final int sPort;
	private static String VBASEURL = "http://localhost:";
	
	public SystemConfigurationImpl(final int vPort, final int sPort) {
		this.vPort = vPort;
		this.sPort = sPort;
	}
	
	public String GetVBaseUrl() {
		return VBASEURL+vPort;
	}
	
	public String GetSBaseUrl() {
		return VBASEURL+sPort;
	}
	

}
