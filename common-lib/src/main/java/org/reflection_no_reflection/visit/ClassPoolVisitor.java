package org.reflection_no_reflection.visit;

import org.reflection_no_reflection.Annotation;
import org.reflection_no_reflection.Class;
import org.reflection_no_reflection.Field;
import org.reflection_no_reflection.Method;

/**
 * @author SNI.
 */
public interface ClassPoolVisitor {
    <T> void visit(org.reflection_no_reflection.Class<T> clazz);

    void visit(Field field);

    void visit(Method method);

    void visit(Annotation annotation);

    void visitAnnotationMethod(Annotation annotation, Method method);

    void endVisit(Class aClass);

    void endVisit(Annotation annotation);
}
