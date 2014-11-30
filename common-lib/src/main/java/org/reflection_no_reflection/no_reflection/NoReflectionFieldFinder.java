package org.reflection_no_reflection.no_reflection;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.reflection_no_reflection.Field;
import org.reflection_no_reflection.FieldFinder;

/**
 * A wrapper based on the {@link AnnotationDatabaseFinder}.
 * Provides a high level API to access RNR fields.
 * @author SNI
 */
public class NoReflectionFieldFinder implements FieldFinder {
    protected static FieldGetterAndSetter fieldGetterAndSetter;
    protected AnnotationDatabaseFinder annotationDatabaseFinder;

    public NoReflectionFieldFinder(AnnotationDatabaseFinder annotationDatabaseFinder) {
        this.annotationDatabaseFinder = annotationDatabaseFinder;
    }

    public NoReflectionFieldFinder(String[] annotationDatabasePackages) {
        this.annotationDatabaseFinder = new AnnotationDatabaseFinder(annotationDatabasePackages);
    }

    @Override
    public Collection<Field> getAllFields(Class<? extends Annotation> annotationClass, Class clazz) {
        HashMap<String, Map<String, Set<Field>>> mapAnnotationToMapClassContainingInjectionToInjectedFieldSet = annotationDatabaseFinder.getMapAnnotationToMapClassContainingInjectionToInjectedFieldSet();
        Map<String, Set<Field>> stringSetMap = mapAnnotationToMapClassContainingInjectionToInjectedFieldSet.get(annotationClass.getName());
        return stringSetMap.get(clazz.getName());
    }


}


