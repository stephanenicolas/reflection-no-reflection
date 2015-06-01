package org.reflection_no_reflection.generator.module;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.WildcardTypeName;
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
public class ModuleDumperClassPoolVisitor implements ClassPoolVisitor {

    private JavaFile javaFile;
    private List<Class<?>> classList = new ArrayList<>();
    private Map<Class<? extends Annotation>, Set<Class<?>>> mapAnnotationTypeToClassContainingAnnotation = new HashMap<>();
    private final TypeSpec.Builder moduleType;
    public static final ClassName MODULE_TYPE_NAME = ClassName.get("org.reflection_no_reflection.runtime", "Module");
    public static final ClassName CLASS_TYPE_NAME = ClassName.get("org.reflection_no_reflection", "Class");
    public static final ClassName FIELD_TYPE_NAME = ClassName.get("org.reflection_no_reflection", "Field");
    public static final ClassName ANNOTATION_TYPE_NAME = ClassName.get("org.reflection_no_reflection", "Annotation");

    public ModuleDumperClassPoolVisitor() {

        //build class list
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


        //build map of annotation type names to class set
        ClassName mapTypeName = ClassName.get("java.util", "Map");
        ClassName hashMapTypeName = ClassName.get("java.util", "HashMap");
        ClassName setTypeName = ClassName.get("java.util", "Set");
        TypeName setOfClassesTypeName = ParameterizedTypeName.get(setTypeName, CLASS_TYPE_NAME);
        WildcardTypeName subClassOfAnnotationTypeName = WildcardTypeName.subtypeOf(ANNOTATION_TYPE_NAME);
        TypeName classesOfAnnotationTypeName = ParameterizedTypeName.get(CLASS_TYPE_NAME, subClassOfAnnotationTypeName);
        TypeName mapOfAnnotationTypeToClassesContainingAnnotationTypeName = ParameterizedTypeName.get(mapTypeName, classesOfAnnotationTypeName, setOfClassesTypeName);

        FieldSpec mapOfAnnotationTypeToClassesContainingAnnotationField = FieldSpec.builder(mapOfAnnotationTypeToClassesContainingAnnotationTypeName, "mapOfAnnotationTypeToClassesContainingAnnotation", Modifier.PRIVATE)
            .initializer("new $T<>()", hashMapTypeName)
            .build();

        MethodSpec getMapOfAnnotationTypeToClassesContainingAnnotationMethod = MethodSpec.methodBuilder("getMapOfAnnotationTypeToClassesContainingAnnotation")
            .addModifiers(Modifier.PUBLIC)
            .returns(mapOfAnnotationTypeToClassesContainingAnnotationTypeName)
            .addStatement("return $L", "mapOfAnnotationTypeToClassesContainingAnnotation")
            .build();

        moduleType = TypeSpec.classBuilder("ModuleImpl")
            .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
            .addSuperinterface(MODULE_TYPE_NAME)
            .addField(classListField)
            .addField(mapOfAnnotationTypeToClassesContainingAnnotationField)
            .addMethod(getClassListMethod)
            .addMethod(getMapOfAnnotationTypeToClassesContainingAnnotationMethod);
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

    public Map<Class<? extends Annotation>, Set<Class<?>>> getMapAnnotationTypeToClassContainingAnnotation() {
        return mapAnnotationTypeToClassContainingAnnotation;
    }


    public JavaFile getJavaFile() {
        MethodSpec.Builder constructorSpecBuilder = MethodSpec.constructorBuilder();
        constructorSpecBuilder.addModifiers(Modifier.PUBLIC);
        int classCounter = 0;
        //fill class list
        for (Class clazz : classList) {
            final String clazzName = clazz.getName();
            constructorSpecBuilder.addStatement("$T c$L = Class.forNameSafe($S)", CLASS_TYPE_NAME, classCounter, clazzName);
            constructorSpecBuilder.addStatement("classList.add(c$L)", classCounter);
            int fieldCounter = 0;
            for (Field field : clazz.getFields()) {
                constructorSpecBuilder.addStatement("$T f$L = new $T($S,Class.forNameSafe($S),c$L,$L,null)", FIELD_TYPE_NAME, fieldCounter, FIELD_TYPE_NAME, field.getName(), field.getType().getName(), classCounter, field.getModifiers());
                constructorSpecBuilder.addStatement("c$L.addField(f$L)", classCounter, fieldCounter);
                fieldCounter++;
            }
            TypeName reflectorTypeName = ClassName.get(clazzName.substring(0, clazzName.lastIndexOf('.')), clazz.getSimpleName()+"$$Reflector");
            constructorSpecBuilder.addStatement("c$L.setReflector( new $T())", classCounter, reflectorTypeName);

            classCounter++;
            constructorSpecBuilder.addCode("\n");
        }

        //fill class list
        ClassName setTypeName = ClassName.get("java.util", "Set");
        TypeName setOfClassesTypeName = ParameterizedTypeName.get(setTypeName, CLASS_TYPE_NAME);
        ClassName hashSetTypeName = ClassName.get("java.util", "HashSet");
        WildcardTypeName subClassOfAnnotationTypeName = WildcardTypeName.subtypeOf(ANNOTATION_TYPE_NAME);
        TypeName classesOfAnnotationTypeName = ParameterizedTypeName.get(CLASS_TYPE_NAME, subClassOfAnnotationTypeName);

        classCounter = 0;
        for (Map.Entry<Class<? extends Annotation>, Set<Class<?>>> entry : mapAnnotationTypeToClassContainingAnnotation.entrySet()) {
            constructorSpecBuilder.addStatement("$T s$L = new $T()", setOfClassesTypeName, classCounter, hashSetTypeName);
            for (Class<?> clazz : entry.getValue()) {
                constructorSpecBuilder.addStatement("s$L.add(Class.forNameSafe($S))", classCounter, clazz.getName());
            }
            String annotationTypeName = entry.getKey().getName();
            constructorSpecBuilder.addStatement("mapOfAnnotationTypeToClassesContainingAnnotation.put(($T) Class.forNameSafe($S),s$L)", classesOfAnnotationTypeName, annotationTypeName, classCounter);
            constructorSpecBuilder.addCode("\n");
            classCounter++;
        }

        MethodSpec constructorSpec = constructorSpecBuilder.build();
        moduleType.addMethod(constructorSpec);

        javaFile = JavaFile.builder("org.reflection_no_reflection.generator.sample.gen", moduleType.build()).build();

        return javaFile;
    }
}
