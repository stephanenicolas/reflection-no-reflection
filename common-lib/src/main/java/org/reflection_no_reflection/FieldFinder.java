package org.reflection_no_reflection;

import java.lang.annotation.Annotation;
import java.util.List;

/**
 * Created by administrateur on 14-11-23.
 */
public interface FieldFinder {

    public List<Field> getAllFields(Class<? extends Annotation> annotationClass, Class clazz);
}
