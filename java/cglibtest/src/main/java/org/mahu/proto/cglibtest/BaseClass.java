package org.mahu.proto.cglibtest;

import java.util.logging.Logger;

public class BaseClass<T> implements Factory<T> {
	
	private final static Logger LOGGER = Logger.getLogger(FuncCgLib.class.getName());	
	
	private final Class<T> cls;
	
	public BaseClass(final Class<T> cls) {
		LOGGER.finer("ctor() with cls " + cls.getName()+" this="+this);
		this.cls = cls;
	}

	@Override
	public T get() {
		LOGGER.finer("get() with cls "+cls + " this="+this);
		try {
			return cls.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException("Failed to create instance of class "
					+ cls.getName(), e);
		}
	}
}
