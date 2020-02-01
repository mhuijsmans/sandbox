package org.mahu.proto.test;

import com.google.inject.AbstractModule;

public class AppModule1 extends AbstractModule {

	@Override
	protected void configure() {
		bind(IInterface1.class).to(Class1.class);
	}

}
