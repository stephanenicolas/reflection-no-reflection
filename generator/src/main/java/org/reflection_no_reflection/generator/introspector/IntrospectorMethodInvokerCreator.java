package org.reflection_no_reflection.generator.introspector;

import com.squareup.javapoet.ArrayTypeName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import java.lang.reflect.InvocationTargetException;
import javax.lang.model.element.Modifier;
import org.reflection_no_reflection.Class;
import org.reflection_no_reflection.Method;

import static org.reflection_no_reflection.generator.introspector.IntrospectorDumperClassPoolVisitor.OBJECT_TYPE_NAME;
import static org.reflection_no_reflection.generator.introspector.IntrospectorDumperClassPoolVisitor.STRING_TYPE_NAME;

/**
 * @author SNI.
 */
public class IntrospectorMethodInvokerCreator {
    IntrospectorUtil util = new IntrospectorUtil();

    public MethodSpec createMethodInvoker(Class<?> aClass) {
        ParameterSpec parameterSpec1 = ParameterSpec.builder(OBJECT_TYPE_NAME, "instance").build();
        ParameterSpec parameterSpec2 = ParameterSpec.builder(STRING_TYPE_NAME, "methodName").build();
        ParameterSpec parameterSpec3 = ParameterSpec.builder(STRING_TYPE_NAME, "signature").build();
        TypeName objectVarArgsType = ArrayTypeName.get(Object[].class);
        ParameterSpec parameterSpec4 = ParameterSpec.builder(objectVarArgsType, "params").build();
        MethodSpec.Builder invokeMethodBuilder = MethodSpec.methodBuilder("invokeMethod")
            .addModifiers(Modifier.PUBLIC)
            .addAnnotation(Override.class)
            .addParameter(parameterSpec1)
            .addParameter(parameterSpec2)
            .addParameter(parameterSpec3)
            .addParameter(parameterSpec4)
            .addException(InvocationTargetException.class)
            .varargs()
            .returns(OBJECT_TYPE_NAME)
            .addCode("switch(signature) {\n");

        for (Object methodObj : aClass.getMethods()) {
            Method method = (Method) methodObj;
            final TypeName enclosingClassName = util.getClassName(aClass);
            invokeMethodBuilder.addCode("  case($S) :\n", method.toString());

            boolean hasExceptions = method.getExceptionTypes().length != 0;
            if (hasExceptions) {
                invokeMethodBuilder.beginControlFlow("try");
            }

            if (!method.getReturnType().getName().equals("void")) {
                invokeMethodBuilder.addCode("  return  (($T) instance).$L(", enclosingClassName, method.getName());
                createInvocationParameters(invokeMethodBuilder, method);
                invokeMethodBuilder.addCode(");\n");
            } else {
                invokeMethodBuilder.addCode("  (($T) instance).$L(", enclosingClassName, method.getName());
                createInvocationParameters(invokeMethodBuilder, method);
                invokeMethodBuilder.addCode(");\n");
                invokeMethodBuilder.addStatement("  return null");
            }

            if (hasExceptions) {
                invokeMethodBuilder.endControlFlow();
                invokeMethodBuilder.beginControlFlow("catch(Exception e)");
                invokeMethodBuilder.addStatement("throw new InvocationTargetException(e)");
                invokeMethodBuilder.endControlFlow();
            }

        }
        invokeMethodBuilder.addCode("}\n");
        invokeMethodBuilder.addStatement("return null");

        return invokeMethodBuilder.build();
    }

    private void createInvocationParameters(MethodSpec.Builder invokeMethodBuilder, Method method) {
        int indexParam = 0;
        for (Class<?> paramClass : method.getParameterTypes()) {
            final boolean isLast = indexParam == method.getParameterTypes().length - 1;
            System.out.println(method.getName());
            if (isLast && paramClass.isArray() && !method.isVarArgs()) {
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
