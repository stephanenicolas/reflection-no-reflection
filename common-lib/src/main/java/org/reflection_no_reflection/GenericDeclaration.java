package org.reflection_no_reflection;

import java.util.Map;

/**
 * @author SNI.
 */
public class GenericDeclaration {
    private Map<String, ? extends Annotation> annotations;
    private TypeVariable<?>[] typeParameters;

    public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
        return (T) annotations.get(annotationClass.getName());
    }

    public Annotation[] getAnnotations() {
        return new Annotation[0];
    }

    public Annotation[] getDeclaredAnnotations() {
        return new Annotation[0];
    }

    public TypeVariable<?>[] getTypeParameters() {
        return typeParameters;
    }

    public void setTypeParameters(TypeVariable<?>[] typeParameters) {
        this.typeParameters = typeParameters;
    }
}
