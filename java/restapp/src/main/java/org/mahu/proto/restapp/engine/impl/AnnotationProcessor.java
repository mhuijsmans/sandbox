package org.mahu.proto.restapp.engine.impl;

import java.lang.reflect.Field;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mahu.proto.restapp.engine.ExecutionErrorException;
import org.mahu.proto.restapp.model.Node;

public class AnnotationProcessor {

	@SuppressWarnings("serial")
	private static class AnnotationProcessorException extends RuntimeException {
		AnnotationProcessorException(final String msg) {
			super(msg);
		}
	}

	final static Logger logger =LogManager.getLogger(AnnotationProcessor.class.getName());

	public static void AnnotateInstance(final SessionImpl session,
			final Node node, final Object objectWithAnnotations,
			final Map<String, Object> annotationData) {
		String nameObjectWithAnnotations = objectWithAnnotations.getClass()
				.getName();
		logger.debug("AnnotateInstance() object=" + nameObjectWithAnnotations
				+ (node != null ? (", node=" + node.getName()) : ""));
		for (Field field : objectWithAnnotations.getClass().getDeclaredFields()) {
			Class<?> declaringClass = field.getType();
			String nameAnnotatedClassOrInterface = declaringClass.getName();
			if (field.isAnnotationPresent(Inject.class)) {
				logger.debug(
						"AnnotateInstance() object={} Field={} has annotation @Inject of type={}",
						nameObjectWithAnnotations, field.getName(),
						nameAnnotatedClassOrInterface);
				Named named = field.getAnnotation(Named.class);
				if (named != null) {
					nameAnnotatedClassOrInterface = named.value();
					logger.debug(
							"AnnotateInstance(), Field={} has annotation @Named=",
							field.getName(), nameAnnotatedClassOrInterface);
				}
				Object objectToBeInjected = null;
				if (annotationData != null) {
					objectToBeInjected = annotationData
							.get(nameAnnotatedClassOrInterface);
				}
				if (objectToBeInjected == null && session != null) {
					objectToBeInjected = session
							.get(nameAnnotatedClassOrInterface);
				}
				if (objectToBeInjected == null) {
					if (node != null) {
						session.setStateReady(new ExecutionErrorException(
								" Node="
										+ node.getName()
										+ " task="
										+ nameObjectWithAnnotations
										+ ", field="
										+ field.getName()
										+ " annotated with @Inject can not be resolved, nameAnnotatedClassOrInterface="
										+ nameAnnotatedClassOrInterface));
						logger.debug("AnnotateInstance(), exception={}",
								session.getException().getMessage());
						break;
					} else {
						AnnotationProcessorException exception = new AnnotationProcessorException(
								"Object="
										+ nameObjectWithAnnotations
										+ ", field="
										+ field.getName()
										+ " annotated with @Inject can not be resolved, nameAnnotatedClassOrInterface="
										+ nameAnnotatedClassOrInterface);
						logger.debug("AnnotateInstance(), exception={}",
								exception.getMessage());
						throw exception;
					}
				} else {
					boolean isAccessible = field.isAccessible();
					try {
						field.setAccessible(true);
						// If obj is of the wrong type, JVM will throw an
						// IllegalArgumentException exception
						field.set(objectWithAnnotations, objectToBeInjected);
						//
						// Annotate object referenced by this object
						if (node != null) {
							try {
								AnnotateInstance(session, null,
										objectToBeInjected, annotationData);
							} catch (AnnotationProcessorException e) {
								session.setStateReady(new ExecutionErrorException(
										e.getMessage()));
								logger.debug(
										"AnnotateInstance(), exception={}",
										session.getException().getMessage());
								break;
							}
						}
					} catch (IllegalArgumentException | IllegalAccessException e) {
						session.setStateReady(new ExecutionErrorException(
								"Failed to set field="
										+ field.getName()
										+ " annotated with @Inject. Java-error="
										+ e.getMessage()));
						logger.debug("AnnotateInstance() exception={}", session
								.getException().getMessage());
						break;
					} finally {
						field.setAccessible(isAccessible);
					}
				}
			}
		}
	}
}
