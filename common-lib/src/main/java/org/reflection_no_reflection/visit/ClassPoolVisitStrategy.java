package org.reflection_no_reflection.visit;

import java.util.Collection;
import java.util.List;
import org.reflection_no_reflection.Annotation;
import org.reflection_no_reflection.Class;
import org.reflection_no_reflection.Field;
import org.reflection_no_reflection.Method;

/**
 * @author SNI.
 */
public class ClassPoolVisitStrategy {

    public void visit(Collection<Class> classCollection, ClassPoolVisitor classPoolVisitor) {
        for (Class aClass : classCollection) {
            classPoolVisitor.visit(aClass);

            for (Field field : aClass.getFields()) {
                for (Annotation annotation : field.getDeclaredAnnotations()) {
                    visitAnnotation(annotation, classPoolVisitor);
                }

                classPoolVisitor.visit(field);
            }

            for (Object method : aClass.getMethods()) {
                for (Annotation annotation : ((Method) method).getDeclaredAnnotations()) {
                    visitAnnotation(annotation, classPoolVisitor);
                }
                classPoolVisitor.visit((Method) method);
            }
            classPoolVisitor.endVisit(aClass);
        }
    }

    private void visitAnnotation(Annotation annotation, ClassPoolVisitor classPoolVisitor) {
        List<Method> methods = annotation.getMethods();
        classPoolVisitor.visit(annotation);
        for (int i = 0; i < methods.size(); i++) {
            Method method = methods.get(i);
            classPoolVisitor.visitAnnotationMethod(annotation, method);
        }
        classPoolVisitor.endVisit(annotation);
    }
}
