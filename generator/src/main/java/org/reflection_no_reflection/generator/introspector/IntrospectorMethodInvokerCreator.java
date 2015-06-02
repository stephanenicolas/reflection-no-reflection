package org.reflection_no_reflection.generator.introspector;

import com.squareup.javapoet.ArrayTypeName;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import javax.lang.model.element.Modifier;
import org.reflection_no_reflection.Class;
import org.reflection_no_reflection.Method;

import static org.reflection_no_reflection.generator.introspector.IntrospectorDumperClassPoolVisitor.CLASS_TYPE_NAME;
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
            .varargs()
            .returns(OBJECT_TYPE_NAME)
            .addCode("switch(signature) {\n");

        for (Object methodObj : aClass.getMethods()) {
            Method method = (Method) methodObj;
            final TypeName enclosingClassName = util.getClassName(aClass);
            invokeMethodBuilder.addCode("  case($S) :\n", method.toString());
            int indexParam = 0;
            CodeBlock.Builder paramBlock = CodeBlock.builder();
            for (Class<?> paramClass : method.getParameterTypes()) {
                paramBlock.add("($T) params[$L]", paramClass, indexParam);
                if (indexParam != method.getParameterTypes().length) {
                    paramBlock.add(",");
                }
            }

            if (!method.getReturnType().getName().equals("void")) {
                invokeMethodBuilder.addStatement("  return  (($T) instance).$L($L)", enclosingClassName, method.getName(), paramBlock.build().toString());
            } else {
                invokeMethodBuilder.addStatement("  (($T) instance).$L($L)", enclosingClassName, method.getName(), paramBlock.build().toString());
                invokeMethodBuilder.addStatement("  return null");
            }
        }
        invokeMethodBuilder.addCode("}\n");
        invokeMethodBuilder.addStatement("return null");

        return invokeMethodBuilder.build();
    }
}
