package com.groupon;

import java.lang.annotation.Annotation;
import com.google.inject.blender.Field;
import java.util.List;

/**
 * Created by administrateur on 14-11-23.
 */
public interface FieldFinder {

    public List<Field> getAllFields(Class<? extends Annotation> annotationClass, Class clazz);
}
