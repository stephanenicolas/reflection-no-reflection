package org.reflection_no_reflection.generator.annotation;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import java.util.ArrayList;
import java.util.List;
import javax.lang.model.element.Modifier;
import org.reflection_no_reflection.Annotation;
import org.reflection_no_reflection.Class;
import org.reflection_no_reflection.Field;
import org.reflection_no_reflection.Method;
import org.reflection_no_reflection.generator.introspector.IntrospectorUtil;
import org.reflection_no_reflection.visit.ClassPoolVisitor;

/**
 * @author SNI.
 */
public class AnnotationCreatorClassPoolVisitor implements ClassPoolVisitor {
    IntrospectorUtil util = new IntrospectorUtil();

    private List<Class<? extends Annotation>> annotationClassList = new ArrayList<>();
    private List<JavaFile> javaFiles = new ArrayList<>();
    private String targetPackageName;

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
        String aClassName = aClass.getName();
        MethodSpec annotationTypeMethod = MethodSpec.methodBuilder("annotationType")
            .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
            .returns(ClassName.get(java.lang.Class.class))
            .addStatement("return $L.class",aClass.getName())
            .build();

        //TODO add other methods define by the annotations
        TypeSpec annotationImplType = TypeSpec.classBuilder(aClass.getSimpleName() + "$$Impl")
            .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
            .addSuperinterface(util.getClassName(aClass))
            .addMethod(annotationTypeMethod)
            .build();
        return JavaFile.builder(targetPackageName, annotationImplType).build();
    }

    public void setTargetPackageName(String targetPackageName) {
        this.targetPackageName = targetPackageName;
    }
}
