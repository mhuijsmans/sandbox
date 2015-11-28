package org.mahu.proto.html.rest;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Info")
public class ActionInfo {
	private String state;
	public final static String STATE_IDLE = "idle";
	public final static String STATE_BUSY = "busy";
	
	@XmlElement(name="state")	
	public String getState() {
		return state;
	}

	public void setState(final String state) {
		this.state = state;
	}	

}
