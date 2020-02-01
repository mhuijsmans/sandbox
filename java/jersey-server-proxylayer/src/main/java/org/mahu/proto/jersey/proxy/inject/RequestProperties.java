/**
 * Copyright Koninklijke Philips N.V. 2018
 *
 * All rights are reserved. Reproduction or transmission in whole or in part, in
 * any form or by any means, electronic, mechanical or otherwise, is prohibited
 * without the prior written consent of the copyright owner.
 *
 * Filename: RequestProperties.java
 */

package org.mahu.proto.jersey.proxy.inject;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.google.inject.Module;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface RequestProperties {

	Class<? extends Module> moduleClass() default EmptyModule.class;
	Class<? extends IRequestScopeMapProvider> requestScopedMapProvider() default EmptyRequestScopeMap.class;

}
