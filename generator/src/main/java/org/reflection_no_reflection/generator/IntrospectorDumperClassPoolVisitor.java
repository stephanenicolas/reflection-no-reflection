package org.reflection_no_reflection.generator;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeSpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.lang.model.element.Modifier;
import org.reflection_no_reflection.Annotation;
import org.reflection_no_reflection.Class;
import org.reflection_no_reflection.Field;
import org.reflection_no_reflection.Method;
import org.reflection_no_reflection.visit.ClassPoolVisitor;

/**
 * @author SNI.
 */
public class IntrospectorDumperClassPoolVisitor implements ClassPoolVisitor {

    private List<JavaFile> javaFiles = new ArrayList<>();
    private List<Class<?>> classList = new ArrayList<>();

    private Map<Class<? extends Annotation>, Set<Class<?>>> mapAnnotationTypeToClassContainingAnnotation = new HashMap<>();
    public static final ClassName BASE_REFLECTOR_TYPE_NAME = ClassName.get("org.reflection_no_reflection.runtime", "BaseReflector");
    public static final ClassName OBJECT_TYPE_NAME = ClassName.get("java.lang", "Object");
    public static final ClassName STRING_TYPE_NAME = ClassName.get("java.lang", "String");
    public static final ClassName CLASS_TYPE_NAME = ClassName.get("org.reflection_no_reflection", "Class");
    public static final ClassName FIELD_TYPE_NAME = ClassName.get("org.reflection_no_reflection", "Field");
    public static final ClassName ANNOTATION_TYPE_NAME = ClassName.get("org.reflection_no_reflection", "Annotation");

    @Override
    public <T> void visit(Class<T> clazz) {
        classList.add(clazz);
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

    public Map<Class<? extends Annotation>, Set<Class<?>>> getMapAnnotationTypeToClassContainingAnnotation() {
        return mapAnnotationTypeToClassContainingAnnotation;
    }

    public List<JavaFile> getJavaFiles() {
        javaFiles.clear();
        for (Class<?> aClass : classList) {
            JavaFile javaFile = buildReflector(aClass);
            javaFiles.add(javaFile);
        }

        return javaFiles;
    }

    private JavaFile buildReflector(Class<?> aClass) {
        ParameterSpec parameterSpec1 = ParameterSpec.builder(OBJECT_TYPE_NAME, "instance").build();
        ParameterSpec parameterSpec2 = ParameterSpec.builder(STRING_TYPE_NAME, "name").build();
        ParameterSpec parameterSpec3 = ParameterSpec.builder(OBJECT_TYPE_NAME, "value").build();
        MethodSpec.Builder setObjectFieldMethodBuilder = MethodSpec.methodBuilder("setObjectField")
            .addModifiers(Modifier.PUBLIC)
            .addParameter(parameterSpec1)
            .addParameter(parameterSpec2)
            .addParameter(parameterSpec3)
            .addCode("switch(name) {\n");

        for (Field field : aClass.getFields()) {
            final ClassName enclosingClassName = getClassName(aClass);
            final ClassName fieldTypeName = getClassName(field.getType());
            setObjectFieldMethodBuilder
            .addCode("case($S) :", field.getName())
                .addStatement("(($T) instance).$L = ($T) value", enclosingClassName, field.getName(), fieldTypeName)
                .addStatement("break");
        }


        setObjectFieldMethodBuilder.addStatement("}");

        TypeSpec.Builder reflectorType = TypeSpec.classBuilder(aClass.getSimpleName() + "$$Reflector")
            .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
            .superclass(BASE_REFLECTOR_TYPE_NAME)
            .addMethod(setObjectFieldMethodBuilder.build());
        final String aClassName = aClass.getName();
        return JavaFile.builder(aClassName.substring(0, aClassName.lastIndexOf('.')), reflectorType.build()).build();
    }

    private ClassName getClassName(Class<?> clazz) {
        final String className = clazz.getName();
        final String packageName = className.substring(0, className.lastIndexOf('.'));
        return ClassName.get(packageName, clazz.getSimpleName());
    }
}
