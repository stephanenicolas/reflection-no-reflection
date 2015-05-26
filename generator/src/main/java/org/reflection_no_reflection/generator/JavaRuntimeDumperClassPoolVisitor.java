package org.reflection_no_reflection.generator;

import org.reflection_no_reflection.Annotation;
import org.reflection_no_reflection.Class;
import org.reflection_no_reflection.Field;
import org.reflection_no_reflection.Method;
import org.reflection_no_reflection.visit.ClassPoolVisitor;

/**
 * @author SNI.
 */
public class JavaRuntimeDumperClassPoolVisitor implements ClassPoolVisitor {
    @Override public <T> void visit(Class<T> clazz) {

    }

    @Override public void visit(Field field) {

    }

    @Override public void visit(Method method) {

    }

    @Override public void visit(Annotation annotation) {

    }

    @Override public void visitAnnotationMethod(Annotation annotation, Method method) {

    }

    @Override public void endVisit(org.reflection_no_reflection.Class aClass) {
        
    }

    @Override public void endVisit(Annotation annotation) {

    }
}
