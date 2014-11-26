package org.reflection_no_reflection;

import java.lang.annotation.Annotation;
import java.util.Collection;

/**
 * Created by administrateur on 14-11-23.
 */
public interface FieldFinder {

    public Collection<Field> getAllFields(Class<? extends Annotation> annotationClass, Class clazz);
}
