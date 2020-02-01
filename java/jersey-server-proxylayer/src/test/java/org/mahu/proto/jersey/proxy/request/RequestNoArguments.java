package org.mahu.proto.jersey.proxy.request;

import org.mahu.proto.jersey.proxy.service.Const;

public class RequestNoArguments implements IRequestNoArguments {

	@Override
	public String getResponse() {
		return Const.REQUEST_NO_ARGUMENTS;
	}

}
