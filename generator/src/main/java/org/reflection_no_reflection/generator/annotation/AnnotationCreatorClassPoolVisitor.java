package org.reflection_no_reflection.generator.annotation;

import com.squareup.javapoet.JavaFile;
import java.util.ArrayList;
import java.util.List;
import org.reflection_no_reflection.*;
import org.reflection_no_reflection.Class;
import org.reflection_no_reflection.visit.ClassPoolVisitor;

/**
 * @author SNI.
 */
public class AnnotationCreatorClassPoolVisitor implements ClassPoolVisitor {
    private List<Class<? extends Annotation>> annotationClassList = new ArrayList<>();
    private List<JavaFile> javaFiles;

    @Override public <T> void visit(org.reflection_no_reflection.Class<T> clazz) {
        if (clazz.isAnnotation()) {
            annotationClassList.add((Class<? extends Annotation>) clazz);
        }
    }

    @Override public void visit(Field field) {

    }

    @Override public void visit(Method method) {

    }

    @Override public void visit(Annotation annotation) {

    }

    @Override public void visitAnnotationMethod(Annotation annotation, Method method) {

    }

    @Override public void endVisit(Class aClass) {

    }

    @Override public void endVisit(Annotation annotation) {

    }

    public List<JavaFile> getJavaFiles() {
        javaFiles.clear();
        for (Class<? extends Annotation> aClass : annotationClassList) {
            JavaFile javaFile = buildAnnotationImpl(aClass);
            javaFiles.add(javaFile);
        }

        return javaFiles;
    }

    private JavaFile buildAnnotationImpl(Class<? extends Annotation> aClass) {
        return null;
    }
}
