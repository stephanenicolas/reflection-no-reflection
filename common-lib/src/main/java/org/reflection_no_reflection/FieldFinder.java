package org.reflection_no_reflection;

import java.lang.annotation.Annotation;
import java.util.Collection;

/**
 * Defines the behaviour of member finders.
 * @author SNI
 */
public interface FieldFinder {

    public Collection<Field> getAllFields(Class<? extends Annotation> annotationClass, Class clazz);
}
