package org.mahu.proto.jerseyjunittools;

import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.net.URI;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.core.UriBuilder;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.mahu.proto.jerseyjunittools.annotation.RestProviderInTest;
import org.mahu.proto.jerseyjunittools.annotation.RestResourceInTest;

public class RestServiceRule implements TestRule {

	protected final static Logger LOGGER = Logger
			.getLogger(RestServiceRule.class.getName());

	private final RestResourceBase restResourceBase = new RestResourceBase(
			getBaseURI());
	
	
	private final int port;
	
	public RestServiceRule() {
		port = 8080;
	}
	
	public RestServiceRule(final int port) {
		this.port = port;
	}	

	/**
	 * This method is called by JUnit for every method annotated with @Test
	 */
	public Statement apply(Statement arg0, Description arg1) {
		MyStatement statement = new MyStatement(arg0, getBaseURI());
		addRestResources(statement.getResourceConfig(), arg1);
		return statement;
	}

	public RestResourceBase getRestResourceBase() {
		return restResourceBase;
	}

	public URI getBaseURI() {
		return UriBuilder.fromUri("http://localhost/").port(port).build();
	}

	private void addRestResources(final ResourceConfig resourceConfig,
			final Description desc) {
		printAnnotations(desc);
		processRestResourceAnnotation(resourceConfig, desc);
		processRestProviderAnnotation(resourceConfig, desc);
	}

	protected void printAnnotations(final Description desc) {
		Collection<Annotation> col = desc.getAnnotations();
		for (Annotation ann : col) {
			Class<? extends Annotation> annCls = ann.annotationType();
			LOGGER.finest("Description.annotationClass: " + annCls);
		}
	}

	private void processRestProviderAnnotation(
			final ResourceConfig resourceConfig, final Description desc) {
		RestProviderInTest annotation = desc
				.getAnnotation(RestProviderInTest.class);
		if (annotation != null) {
			Class<?> cls = annotation.provider();
			if (!cls.equals(RestProviderInTest.class)) {
				LOGGER.finest("Found provider: " + cls);
				try {
					Object provider = createInstanceProvider(cls);
					registerResourceProvider(resourceConfig, provider);
				} catch (InstantiationException | IllegalAccessException e) {
					throw new IllegalArgumentException(
							"Can not create instance of class " + cls.getName());
				}
			}
		}
	}

	protected void registerResourceProvider(
			final ResourceConfig resourceConfig, Object provider) {
		resourceConfig.register(provider);
	}

	protected Object createInstanceProvider(final Class<?> cls)
			throws InstantiationException, IllegalAccessException {
		return cls.newInstance();
	}

	private void processRestResourceAnnotation(
			final ResourceConfig resourceConfig, final Description desc) {
		RestResourceInTest annotation = desc
				.getAnnotation(RestResourceInTest.class);
		if (annotation != null) {
			Class<?> cls = annotation.resource();
			if (isSetToValueOtherThanDefault(cls)) {
				processRestResourceClass(resourceConfig, cls);
			}
			Class<?>[] clss = annotation.resources();
			if (clss != null && clss.length > 0) {
				for (Class<?> tmp : clss) {
					processRestResourceClass(resourceConfig, tmp);
				}
			}
		}
	}

	protected void processRestResourceClass(
			final ResourceConfig resourceConfig, Class<?> cls) {
		LOGGER.finest("Found resource: " + cls);
		// If class is not public, it will fail in Jersey
		checkClassIsPublic(cls);
		registerRestResource(resourceConfig, cls);
	}

	private boolean isSetToValueOtherThanDefault(Class<?> cls) {
		return !cls.equals(RestResourceInTest.class);
	}

	protected void registerRestResource(final ResourceConfig resourceConfig,
			Class<?> cls) {
		resourceConfig.register(cls);
	}

	private void checkClassIsPublic(Class<?> cls) {
		if (!Modifier.isPublic(cls.getModifiers())) {
			throw new IllegalArgumentException("Class must be public: "
					+ cls.getName());
		}
	}

	/**
	 * MyStatement evaluate() is invoked for every test case.
	 */
	static class MyStatement extends Statement {
		private final Statement statement;
		private final URI baseUri;
		private HttpServer server;
		private final ResourceConfig resourceConfig = new ResourceConfig();

		MyStatement(final Statement aStatement, URI aBaseUri) {
			statement = aStatement;
			baseUri = aBaseUri;
			//
			resourceConfig
					.register(org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpContainerProvider.class);
			resourceConfig.property(
					ServerProperties.METAINF_SERVICES_LOOKUP_DISABLE, true);
			//
			if (LOGGER.isLoggable(Level.FINE)) {
				resourceConfig.register(new LoggingFilter(LOGGER, true));
				resourceConfig.property(ServerProperties.TRACING, "ALL");
			}
		}

		private ResourceConfig getResourceConfig() {
			return resourceConfig;
		}

		@Override
		public void evaluate() throws Throwable {
			try {
				LOGGER.finer("MyStatement.evaluate() enter");
				startServer();
				statement.evaluate();
			} finally {
				stopServer();
				LOGGER.finer("MyStatement.evaluate() leave");
			}
		}

		private void startServer() {
			server = GrizzlyHttpServerFactory.createHttpServer(baseUri,
					resourceConfig);
		}

		private void stopServer() {
			if (server != null) {
				server.shutdownNow();
			}
		}
	}

}
