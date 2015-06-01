package org.reflection_no_reflection.generator;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
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
    public static final TypeName INT_TYPE_NAME = TypeName.get(int.class);
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
        IntrospectorFieldAccessMethodCreator creator = new IntrospectorFieldAccessMethodCreator();
        final MethodSpec setObjectFieldMethod = creator.createSetObjectFieldMethod(aClass);
        final MethodSpec setIntFieldMethod = creator.createSetIntFieldMethod(aClass);

        TypeSpec.Builder reflectorType = TypeSpec.classBuilder(aClass.getSimpleName() + "$$Reflector")
            .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
            .superclass(BASE_REFLECTOR_TYPE_NAME);

        if (setObjectFieldMethod!= null) {
            reflectorType.addMethod(setObjectFieldMethod);
        }
        if (setIntFieldMethod!= null) {
            reflectorType.addMethod(setIntFieldMethod);
        }
        final String aClassName = aClass.getName();
        return JavaFile.builder(aClassName.substring(0, aClassName.lastIndexOf('.')), reflectorType.build()).build();
    }
}
