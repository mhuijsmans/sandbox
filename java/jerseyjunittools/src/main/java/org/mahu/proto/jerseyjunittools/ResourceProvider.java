package org.mahu.proto.jerseyjunittools;

import static com.google.common.base.Preconditions.checkArgument;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.logging.Logger;

import org.glassfish.hk2.api.Factory;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

/**
 * Class to make creation of ResourceProvider easy.
 */
public class ResourceProvider<S, T> extends AbstractBinder implements
		Factory<T> {

	protected final static Logger LOGGER = Logger
			.getLogger(ResourceProvider.class.getName());

	private final Class<S> interfaceClass;
	private final Class<T> implClass;

	public ResourceProvider() {
		this.interfaceClass = getClassOfS();
		this.implClass = getClassOfT();
		LOGGER.info(ResourceProvider.class.getSimpleName()
				+ " provider created for interface " + interfaceClass.getName()
				+ "with impl " + implClass.getName());
	}

	protected void checkExtendsT(final Class<S> cls) {
		checkArgument(getClassOfT().isAssignableFrom(cls));
	}

	// Configure is called at registration
	@Override
	protected void configure() {
		LOGGER.info(ResourceProvider.class.getSimpleName()
				+ " binding factory(this) to interface "
				+ interfaceClass.getName());
		bindFactory(this).to(interfaceClass);
	}

	/**
	 * Called for every REST method to resource that needs instance of class.
	 */
	@Override
	public T provide() {
		LOGGER.finer(ResourceProvider.class.getSimpleName()
				+ " provide instanceof interface " + interfaceClass.getName());
		try {
			return (T) implClass.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException("Failed to craete instance of class "
					+ implClass.getName(), e);
		}
	}

	@Override
	public void dispose(final T instance) {
		// empty
	}

	/**
	 * method will only work for classes that directly inherit from ClassA.
	 */
	@SuppressWarnings("unchecked")
	public Class<T> getClassOfT() {
		return (Class<T>) getClassOfTypeArgument(1);
	}

	@SuppressWarnings("unchecked")
	public Class<S> getClassOfS() {
		return (Class<S>) getClassOfTypeArgument(0);
	}

	public Type getClassOfTypeArgument(final int i) {
		ParameterizedType superclass = (ParameterizedType) getClass()
				.getGenericSuperclass();
		return superclass.getActualTypeArguments()[i];
	}
}