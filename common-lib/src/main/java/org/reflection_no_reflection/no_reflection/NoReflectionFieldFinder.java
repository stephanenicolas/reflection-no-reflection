package org.reflection_no_reflection.no_reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.util.Map;
import org.reflection_no_reflection.FieldFinder;

/**
 * Created by administrateur on 14-11-23.
 */
public abstract class NoReflectionFieldFinder implements FieldFinder {
    protected static FieldGetterAndSetter fieldGetterAndSetter;

    protected class LocalNoReflectionField extends NoReflectionField {

        private int modifiers = Modifier.PUBLIC;
        private final Map<Class<? extends Annotation>, Annotation> mapAnnotation;

        public LocalNoReflectionField(int index, String fieldName, String declaringClassName, String fieldTypeName, int modifiers, Map<Class<? extends Annotation>, Annotation> mapAnnotation) {
            super(index, fieldName, declaringClassName, fieldTypeName);
            this.modifiers = modifiers;
            this.mapAnnotation = mapAnnotation;
        }

        @Override
        public <A extends Annotation> A getAnnotation (Class<A> annotationType) {
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


