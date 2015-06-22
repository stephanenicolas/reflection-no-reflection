package org.reflection_no_reflection.visit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
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
        List<Class> classList = sortClassesByLevel(classCollection);
        for (Class aClass : classList) {
            classPoolVisitor.visit(aClass);

            for (Field field : aClass.getFields()) {
                for (java.lang.annotation.Annotation annotation : field.getAnnotations()) {
                    visitAnnotation((Annotation)annotation, classPoolVisitor);
                }

                classPoolVisitor.visit(field);
            }

            for (Object method : aClass.getMethods()) {
                for (java.lang.annotation.Annotation annotation : ((Method) method).getAnnotations()) {
                    visitAnnotation(annotation, classPoolVisitor);
                }
                classPoolVisitor.visit((Method) method);
            }
            classPoolVisitor.endVisit(aClass);
        }
    }

    private List<Class> sortClassesByLevel(Collection<Class> classCollection) {
        List<Class> classList = new ArrayList<>(classCollection);
        Collections.sort(classList, new Comparator<Class>() {
            @Override public int compare(Class o1, Class o2) {
                return o1.getLevel() - o2.getLevel();
            }
        });
        return classList;
    }

    private void visitAnnotation(java.lang.annotation.Annotation annotation, ClassPoolVisitor classPoolVisitor) {
        Annotation rnrAnnotation = (Annotation) annotation;
        List<Method> methods = rnrAnnotation.getMethods();
        classPoolVisitor.visit(rnrAnnotation);
        for (int i = 0; i < methods.size(); i++) {
            Method method = methods.get(i);
            classPoolVisitor.visitAnnotationMethod(rnrAnnotation, method);
        }
        classPoolVisitor.endVisit(rnrAnnotation);
    }
}
