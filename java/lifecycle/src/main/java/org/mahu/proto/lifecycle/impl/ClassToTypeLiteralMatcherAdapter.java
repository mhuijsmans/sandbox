package org.mahu.proto.lifecycle.impl;

import com.google.inject.TypeLiteral;
import com.google.inject.matcher.AbstractMatcher;
import com.google.inject.matcher.Matcher;

public class ClassToTypeLiteralMatcherAdapter extends AbstractMatcher<TypeLiteral<?>> {
    @SuppressWarnings("rawtypes")
    private final Matcher<Class> classMatcher;

    public ClassToTypeLiteralMatcherAdapter(@SuppressWarnings("rawtypes") Matcher<Class> matcher) {
        this.classMatcher = matcher;
    }

    public boolean matches(@SuppressWarnings("rawtypes") TypeLiteral typeLiteral) {
        return classMatcher.matches(typeLiteral.getRawType());
    }
}
