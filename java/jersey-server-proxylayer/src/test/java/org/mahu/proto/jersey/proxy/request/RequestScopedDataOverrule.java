package org.mahu.proto.jersey.proxy.request;

import org.mahu.proto.jersey.proxy.service.Const;

public class RequestScopedDataOverrule implements IRequestScopedData {

	@Override
	public String getInfo() {
		return Const.REQUEST_SCOPED_DATA_OVERRULE;
	}

}
