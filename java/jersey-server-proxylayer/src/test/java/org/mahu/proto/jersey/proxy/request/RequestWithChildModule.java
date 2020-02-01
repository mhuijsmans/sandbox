package org.mahu.proto.jersey.proxy.request;

import javax.inject.Inject;

public final class RequestWithChildModule implements IRequestWithChildModule {

	private final IChildModuleInfo moduleInfo;

	@Inject
	RequestWithChildModule(IChildModuleInfo moduleInfo) {
		this.moduleInfo = moduleInfo;
	}

	@Override
	public String getResponse() {
		return moduleInfo.getInfo();
	}

}
