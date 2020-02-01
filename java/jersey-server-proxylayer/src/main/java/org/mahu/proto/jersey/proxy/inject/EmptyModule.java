package org.mahu.proto.jersey.proxy.inject;

import com.google.inject.Binder;
import com.google.inject.Module;

final class EmptyModule implements Module {

	@Override
	public void configure(Binder binder) {
		// empty
	}

}
