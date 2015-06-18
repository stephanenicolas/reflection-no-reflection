package org.reflection_no_reflection.generator.annotation;

import com.squareup.javapoet.ArrayTypeName;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
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

    private JavaFile buildAnnotationImpl(Class<? extends Annotation> annotationClass) {
        String aClassName = annotationClass.getName();
        MethodSpec annotationTypeMethod = MethodSpec.methodBuilder("annotationType")
            .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
            .returns(ClassName.get(java.lang.Class.class))
            .addStatement("return $L.class", aClassName)
            .build();

        TypeSpec.Builder annotationImplType = TypeSpec.classBuilder(annotationClass.getSimpleName() + "$$Impl")
            .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
            .addSuperinterface(util.getClassName(annotationClass))
            .addMethod(annotationTypeMethod);

        System.out.println("annotation methods " + annotationClass.getMethods().size());
        System.out.println("annotation fields " + annotationClass.getFields().length);
        System.out.println("annotation " + annotationClass.toString());

        for (Method method : annotationClass.getMethods()) {
            TypeName type;
            if (method.getReturnType().isArray()) {
                type = ArrayTypeName.get(method.getReturnType().getComponentType());
            } else {
                type = TypeName.get(method.getReturnType());
            }
            FieldSpec field = FieldSpec.builder(type, method.getName(), Modifier.PRIVATE).build();
            annotationImplType.addField(field);

            MethodSpec setterMethod = createSetterMethod(type, method.getName());
            annotationImplType.addMethod(setterMethod);
            MethodSpec getterMethod = createGetterMethod(type, method.getName());
            annotationImplType.addMethod(getterMethod);
        }

        return JavaFile.builder(targetPackageName, annotationImplType.build()).build();
    }

    private MethodSpec createSetterMethod(TypeName type, String fieldName) {
        return createPrefixedMethod("set", fieldName)
            .addParameter(type, fieldName)
            .build();
    }

    private MethodSpec createGetterMethod(TypeName type, String fieldName) {
        return createPrefixedMethod("get", fieldName)
            .returns(type)
            .build();
    }

    private MethodSpec.Builder createPrefixedMethod(String prefix, String fieldName) {
        final String capitalizedFieldName = createCapitalizedName(fieldName);
        String methodName = prefix + capitalizedFieldName;
        return MethodSpec.methodBuilder(methodName);
    }

    private String createCapitalizedName(String fieldName) {
        return Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
    }

    public void setTargetPackageName(String targetPackageName) {
        this.targetPackageName = targetPackageName;
    }
}
