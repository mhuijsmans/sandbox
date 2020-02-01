package org.mahu.proto.jersey.lifecycle;

import javax.inject.Singleton;

import com.google.inject.AbstractModule;

public class MyModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(ILifeCycleInterface.class).to(LifeCycleController.class).in(Singleton.class);
		bind(IRequestPreProcessor.class).to(RequestPreProcessor.class).in(Singleton.class);
		bind(ISettingsReader.class).to(SettingsReader.class).in(Singleton.class);
	}

}
