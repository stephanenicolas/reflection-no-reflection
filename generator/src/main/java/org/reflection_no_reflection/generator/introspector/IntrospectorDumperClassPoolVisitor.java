package org.reflection_no_reflection.generator.introspector;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
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
    public static final ClassName CLASS_TYPE_NAME = ClassName.get("org.reflection_no_reflection", "Class");
    public static final ClassName OBJECT_TYPE_NAME = ClassName.get("java.lang", "Object");
    public static final TypeName BYTE_TYPE_NAME = TypeName.get(byte.class);
    public static final TypeName SHORT_TYPE_NAME = TypeName.get(short.class);
    public static final TypeName INT_TYPE_NAME = TypeName.get(int.class);
    public static final TypeName LONG_TYPE_NAME = TypeName.get(long.class);
    public static final TypeName FLOAT_TYPE_NAME = TypeName.get(float.class);
    public static final TypeName DOUBLE_TYPE_NAME = TypeName.get(double.class);
    public static final TypeName CHAR_TYPE_NAME = TypeName.get(char.class);
    public static final TypeName BOOLEAN_TYPE_NAME = TypeName.get(boolean.class);
    public static final ClassName STRING_TYPE_NAME = ClassName.get("java.lang", "String");
    private List<Pattern> includes;
    private List<Pattern> excludes;

    @Override
    public <T> void visit(Class<T> clazz) {
        //TODO add all protected java & android packages
        String className = clazz.getName();
        boolean shouldIncludeClass = filter(className);
        System.out.println("Introspector for class:" + className + "=" + shouldIncludeClass);
        if (shouldIncludeClass) {
            classList.add(clazz);
        }
    }

    public boolean filter(String className) {
        if (className.indexOf('.') == -1) {
            return false;
        }

        boolean inIncludes = false;
        for (Pattern include : includes) {
            if (include.matcher(className).find()) {
                inIncludes = true;
                break;
            }
        }

        if (!inIncludes) {
            return false;
        }

        for (Pattern exclude : excludes) {
            if (exclude.matcher(className).find()) {
                return false;
            }
        }
        return true;
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
        IntrospectorFieldSetterMethodCreator setterCreator = new IntrospectorFieldSetterMethodCreator();
        IntrospectorMethodInvokerCreator methodInvokerCreator = new IntrospectorMethodInvokerCreator();
        IntrospectorConstructorInvokerCreator constructorInvokerCreator = new IntrospectorConstructorInvokerCreator();
        final MethodSpec setObjectFieldMethod = setterCreator.createSetObjectFieldMethod(aClass);
        final MethodSpec setByteFieldMethod = setterCreator.createSetByteFieldMethod(aClass);
        final MethodSpec setShortFieldMethod = setterCreator.createSetShortFieldMethod(aClass);
        final MethodSpec setIntFieldMethod = setterCreator.createSetIntFieldMethod(aClass);
        final MethodSpec setLongFieldMethod = setterCreator.createSetLongFieldMethod(aClass);
        final MethodSpec setFloatFieldMethod = setterCreator.createSetFloatFieldMethod(aClass);
        final MethodSpec setDoubleFieldMethod = setterCreator.createSetDoubleFieldMethod(aClass);
        final MethodSpec setCharFieldMethod = setterCreator.createSetCharFieldMethod(aClass);
        final MethodSpec setBooleanFieldMethod = setterCreator.createSetBooleanFieldMethod(aClass);

        final MethodSpec methodInvokerMethod = methodInvokerCreator.createMethodInvoker(aClass);
        final MethodSpec constructorInvokerMethod = constructorInvokerCreator.createConstructorInvoker(aClass);

        TypeSpec.Builder reflectorType = TypeSpec.classBuilder(aClass.getSimpleName() + "$$Reflector")
            .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
            .superclass(BASE_REFLECTOR_TYPE_NAME);

        doAddMethod(reflectorType, setObjectFieldMethod);
        doAddMethod(reflectorType, setByteFieldMethod);
        doAddMethod(reflectorType, setShortFieldMethod);
        doAddMethod(reflectorType, setIntFieldMethod);
        doAddMethod(reflectorType, setLongFieldMethod);
        doAddMethod(reflectorType, setFloatFieldMethod);
        doAddMethod(reflectorType, setDoubleFieldMethod);
        doAddMethod(reflectorType, setCharFieldMethod);
        doAddMethod(reflectorType, setBooleanFieldMethod);
        doAddMethod(reflectorType, methodInvokerMethod);
        doAddMethod(reflectorType, constructorInvokerMethod);
        final String aClassName = aClass.getName();
        return JavaFile.builder(aClassName.substring(0, aClassName.lastIndexOf('.')), reflectorType.build()).build();
    }

    private void doAddMethod(TypeSpec.Builder reflectorType, MethodSpec methodSpec) {
        if (methodSpec != null) {
            reflectorType.addMethod(methodSpec);
        }
    }

    public void setIncludes(List<Pattern> includes) {
        this.includes = includes;
    }

    public void setExcludes(List<Pattern> excludes) {
        this.excludes = excludes;
    }
}
