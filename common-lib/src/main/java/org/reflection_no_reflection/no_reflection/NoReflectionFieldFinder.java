package org.reflection_no_reflection.no_reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.reflection_no_reflection.Field;
import org.reflection_no_reflection.FieldFinder;

/**
 * Created by administrateur on 14-11-23.
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
        Set<Field> allFields;

        HashMap<String, Map<String, Set<Field>>> mapAnnotationToMapClassContainingInjectionToInjectedFieldSet = annotationDatabaseFinder.getMapAnnotationToMapClassContainingInjectionToInjectedFieldSet();
        Map<String, Set<Field>> stringSetMap = mapAnnotationToMapClassContainingInjectionToInjectedFieldSet.get(annotationClass.getName());
        allFields = stringSetMap.get(clazz.getName());
        return allFields;
    }

    protected class LocalNoReflectionField extends NoReflectionField {

        private int modifiers = Modifier.PUBLIC;
        private final Map<String, Annotation> mapAnnotation;

        public LocalNoReflectionField(int index, String fieldName, String declaringClassName, String fieldTypeName, int modifiers, List<? extends Annotation> annotationList) {
            super(index, fieldName, declaringClassName, fieldTypeName, modifiers, annotationList);
            mapAnnotation = new HashMap<>();
            for(Annotation annotation : annotationList) {
                mapAnnotation.put(((org.reflection_no_reflection.Annotation) annotation).getAnnotationTypeName(), annotation);
            }
        }

        @Override
        public <A extends Annotation> A getAnnotation(Class<A> annotationType) {
            return (A) mapAnnotation.get(annotationType);
        }

        @Override
        public Annotation[] getDeclaredAnnotations() {
            return mapAnnotation.values().toArray(new Annotation[mapAnnotation.values().size()]);
        }

        @Override
        public int getModifiers() {
            return modifiers;
        }

        @Override
        public void set(Object object, Object value) throws IllegalAccessException {
            fieldGetterAndSetter.set(getIndex(), object, value);
        }

        @Override
        public Object get(Object object) throws IllegalAccessException {
            return fieldGetterAndSetter.get(getIndex(), object);
        }

        @Override
        public boolean isAnnotationPresent(Class<? extends Annotation> annotationType) {
            return mapAnnotation.get(annotationType) != null;
        }

        @Override
        public int getInt(Object object) throws IllegalAccessException {
            return fieldGetterAndSetter.getInt(getIndex(), object);
        }

        @Override
        public void setInt(Object object, int value) throws IllegalAccessException {
            fieldGetterAndSetter.setInt(getIndex(), object, value);
        }
    }
}


