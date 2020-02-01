package org.mahu.proto.jersey.proxy.request;

import org.mahu.proto.jersey.proxy.service.Const;

public class ChildModuleInfo implements IChildModuleInfo {
	
	public String getInfo() {
		return Const.REQUEST_WITH_CHILD_MODULE;
	}

}
