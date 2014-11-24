package com.groupon;

import com.google.inject.blender.Field;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by administrateur on 14-11-23.
 */
public class ReflectionFieldFinderImpl implements FieldFinder {
    @Override public List<Field> getAllFields(Class<? extends Annotation> annotationClass, Class clazz) {
        List<Field> fieldList = new ArrayList<Field>();
        for(java.lang.reflect.Field field : clazz.getDeclaredFields()) {
            if( field.isAnnotationPresent(annotationClass)) {
                fieldList.add(new ReflectionField(field));
            }
        }
        Collections.sort(fieldList, new Comparator<Field>() {
            @Override public int compare(Field o1, Field o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        return fieldList;
    }
}
