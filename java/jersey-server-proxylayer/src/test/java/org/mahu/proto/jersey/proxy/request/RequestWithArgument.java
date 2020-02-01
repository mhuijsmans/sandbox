package org.mahu.proto.jersey.proxy.request;

import org.mahu.proto.jersey.proxy.service.Const;

public class RequestWithArgument implements IRequestWithArgument {

	@Override
	public String getResponse(String input) {
		return input+Const.REQUEST_WITH_ARGUMENT;
	}

}
