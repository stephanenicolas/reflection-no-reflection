package org.reflection_no_reflection.generator;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import java.util.ArrayList;
import java.util.List;
import javax.lang.model.element.Modifier;
import org.reflection_no_reflection.Annotation;
import org.reflection_no_reflection.Class;
import org.reflection_no_reflection.Field;
import org.reflection_no_reflection.Method;
import org.reflection_no_reflection.visit.ClassPoolVisitor;

/**
 * @author SNI.
 */
public class JavaRuntimeDumperClassPoolVisitor implements ClassPoolVisitor {

    private JavaFile javaFile;
    private List<Class<?>> classList = new ArrayList<>();
    private final TypeSpec.Builder moduleType;
    public static final ClassName CLASS_TYPE_NAME = ClassName.get("org.reflection_no_reflection", "Class");
    public static final ClassName FIELD_TYPE_NAME = ClassName.get("org.reflection_no_reflection", "Field");

    public JavaRuntimeDumperClassPoolVisitor() {

        ClassName listTypeName = ClassName.get("java.util", "List");
        ClassName arrayListTypeName = ClassName.get("java.util", "ArrayList");
        TypeName listOfClassesTypeName = ParameterizedTypeName.get(listTypeName, CLASS_TYPE_NAME);

        FieldSpec classListField = FieldSpec.builder(listOfClassesTypeName, "classList", Modifier.PRIVATE)
            .initializer("new $T<>()", arrayListTypeName)
            .build();

        MethodSpec getClassListMethod = MethodSpec.methodBuilder("getClassList")
            .addModifiers(Modifier.PUBLIC)
            .returns(listOfClassesTypeName)
            .addStatement("return $L", "classList")
            .build();

        moduleType = TypeSpec.classBuilder("Module")
            .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
            .addField(classListField)
            .addMethod(getClassListMethod);
    }

    @Override public <T> void visit(Class<T> clazz) {
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

    @Override public void endVisit(org.reflection_no_reflection.Class aClass) {
        
    }

    @Override public void endVisit(Annotation annotation) {

    }

    public JavaFile getJavaFile() {
        MethodSpec.Builder constructorSpecBuilder = MethodSpec.constructorBuilder();
        int classCounter = 0;
        for (Class clazz : classList) {
            constructorSpecBuilder.addStatement("$T c$L = Class.forName($S)", CLASS_TYPE_NAME, classCounter, clazz.getName());
            constructorSpecBuilder.addStatement("classList.add(c$L)", classCounter);
            int fieldCounter = 0;
            for (Field field : clazz.getFields()) {
                constructorSpecBuilder.addStatement("$T f$L = new $T($S,Class.forName($S),c$L,$L,null)", FIELD_TYPE_NAME, fieldCounter, FIELD_TYPE_NAME, field.getName(), field.getType().getName(), classCounter, field.getModifiers());
                constructorSpecBuilder.addStatement("c$L.addField(f$L)", classCounter, fieldCounter);
            }

            classCounter++;
            constructorSpecBuilder.addCode("\n");
        }
        MethodSpec constructorSpec = constructorSpecBuilder.build();
        moduleType.addMethod(constructorSpec);

        javaFile = JavaFile.builder("org.reflection_no_reflection.generator.example", moduleType.build()).build();

        return javaFile;
    }
}
