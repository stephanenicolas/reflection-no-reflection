package org.reflection_no_reflection.generator.introspector;

import com.squareup.javapoet.ArrayTypeName;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import java.lang.reflect.InvocationTargetException;
import javax.lang.model.element.Modifier;
import org.reflection_no_reflection.Class;
import org.reflection_no_reflection.Constructor;
import org.reflection_no_reflection.Method;

import static org.reflection_no_reflection.generator.introspector.IntrospectorDumperClassPoolVisitor.OBJECT_TYPE_NAME;
import static org.reflection_no_reflection.generator.introspector.IntrospectorDumperClassPoolVisitor.STRING_TYPE_NAME;

/**
 * @author SNI.
 */
public class IntrospectorConstructorInvokerCreator {
    IntrospectorUtil util = new IntrospectorUtil();

    public MethodSpec createConstructorInvoker(Class<?> aClass) {
        ParameterSpec parameterSpec1 = ParameterSpec.builder(STRING_TYPE_NAME, "signature").build();
        TypeName objectVarArgsType = ArrayTypeName.get(Object[].class);
        ParameterSpec parameterSpec2 = ParameterSpec.builder(objectVarArgsType, "params").build();
        String packageName = aClass.getName().substring(0, aClass.getName().lastIndexOf('.'));
        String className = aClass.getSimpleName();
        MethodSpec.Builder newInstanceMethodBuilder = MethodSpec.methodBuilder("newInstance")
            .addModifiers(Modifier.PUBLIC)
            .addAnnotation(Override.class)
            .addParameter(parameterSpec1)
            .addParameter(parameterSpec2)
            .returns(ClassName.get(packageName, className))
            .addException(InvocationTargetException.class)
            .varargs()
            .returns(OBJECT_TYPE_NAME)
            .addCode("switch(signature) {\n");

        for (Object constructorObj : aClass.getConstructors()) {
            Constructor constructor = (Constructor) constructorObj;
            newInstanceMethodBuilder.addCode("  case($S) :\n", constructor.toString());

            invokeConstructor(aClass, newInstanceMethodBuilder, constructor);

        }
        newInstanceMethodBuilder.addCode("  default :\n");
        newInstanceMethodBuilder.addStatement("throw new InvocationTargetException(new java.lang.NoSuchMethodException(\"constructor:\" + signature + \" not found\"))");
        newInstanceMethodBuilder.addCode("}\n");

        return newInstanceMethodBuilder.build();
    }

    private void invokeConstructor(Class<?> aClass, MethodSpec.Builder invokeMethodBuilder, Constructor constructor) {
        final TypeName enclosingClassName = util.getClassName(aClass);
        boolean hasExceptions = constructor.getExceptionTypes().length != 0;
        if (hasExceptions) {
            invokeMethodBuilder.beginControlFlow("try");
        }

        invokeMethodBuilder.addCode("  return  new $T(", enclosingClassName);
        addInvocationParameters(invokeMethodBuilder, constructor);
        invokeMethodBuilder.addCode(");\n");

        if (hasExceptions) {
            invokeMethodBuilder.endControlFlow();
            invokeMethodBuilder.beginControlFlow("catch(Exception e)");
            invokeMethodBuilder.addStatement("throw new InvocationTargetException(e)");
            invokeMethodBuilder.endControlFlow();
        }
    }

    private void addInvocationParameters(MethodSpec.Builder invokeMethodBuilder, Constructor constructor) {
        int indexParam = 0;
        for (Class<?> paramClass : constructor.getParameterTypes()) {
            final boolean isLast = indexParam == constructor.getParameterTypes().length - 1;
            System.out.println(constructor.getName());
            if (isLast && paramClass.isArray()) {
                invokeMethodBuilder.addCode("($T) params", util.getClassName(paramClass));
            } else {
                invokeMethodBuilder.addCode("($T) params[$L]", util.getClassName(paramClass), indexParam);
            }
            if (!isLast) {
                invokeMethodBuilder.addCode(",");
            }
            indexParam++;
        }
    }
}
