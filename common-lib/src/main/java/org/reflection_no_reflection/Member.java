package org.reflection_no_reflection;

import java.util.List;
import java.util.Map;

public abstract class Member {

    protected Member() {}

    public abstract Class<?> getDeclaringClass();
    public abstract String getName();

    private List<Annotation> rnRAnnotationList;
    private List<java.lang.annotation.Annotation> annotationImplList;

    @SuppressWarnings({"unused", "called by generated code"})
    public void setAnnotationImplList(List<java.lang.annotation.Annotation> annotationImplList) {
        this.annotationImplList = annotationImplList;
    }

    public void setRnRAnnotationList(List<Annotation> rnRAnnotationList) {
        this.rnRAnnotationList = rnRAnnotationList;
    }


    public <A extends java.lang.annotation.Annotation> A getAnnotation(Class<A> annotationType) {
        return getAnnotation(annotationType.getName());
    }

    public <A extends java.lang.annotation.Annotation> A getAnnotation(java.lang.Class<A> annotationType) {
        return getAnnotation(annotationType.getName());
    }

    public <A extends java.lang.annotation.Annotation> A getAnnotation(String annotationClassNane) {
        //processor annotations
        if (rnRAnnotationList != null) {
            for (Annotation annotation : rnRAnnotationList) {
                if (annotation.rnrAnnotationType().getName().equals(annotationClassNane)) {
                    return (A) annotation;
                }
            }
        }

        //generator annotations
        if( annotationImplList!= null) {
            for (java.lang.annotation.Annotation annotation : annotationImplList) {
                if (annotation.annotationType().getName().equals(annotationClassNane)) {
                    return (A) annotation;
                }
            }
        }

        return null;
    }

    public java.lang.annotation.Annotation[] getAnnotations() {
        if (rnRAnnotationList == null && annotationImplList == null) {
            return new java.lang.annotation.Annotation[0];
        } else if (rnRAnnotationList !=null ) {
            return rnRAnnotationList.toArray(new Annotation[rnRAnnotationList.size()]);
        } else {
            return annotationImplList.toArray(new java.lang.annotation.Annotation[annotationImplList.size()]); //not implemented
        }
    }

    public boolean isAnnotationPresent(Class<? extends Annotation> annotationType) {
        return false; //not implemented
    }

    private static final Annotation[] EMPTY_ANNOTATION_ARRAY = new Annotation[0];

    /**
     * TODO what should this really return ?
     * @since 1.5
     */
    public java.lang.annotation.Annotation[] getDeclaredAnnotations() {
        //TODO catch what the difference should be
        return getAnnotations();
    }
}
