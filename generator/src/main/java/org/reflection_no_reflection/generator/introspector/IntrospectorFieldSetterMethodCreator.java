package org.reflection_no_reflection.generator.introspector;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import javax.lang.model.element.Modifier;
import org.reflection_no_reflection.*;
import org.reflection_no_reflection.Class;

import static org.reflection_no_reflection.generator.introspector.IntrospectorDumperClassPoolVisitor.BYTE_TYPE_NAME;
import static org.reflection_no_reflection.generator.introspector.IntrospectorDumperClassPoolVisitor.SHORT_TYPE_NAME;
import static org.reflection_no_reflection.generator.introspector.IntrospectorDumperClassPoolVisitor.INT_TYPE_NAME;
import static org.reflection_no_reflection.generator.introspector.IntrospectorDumperClassPoolVisitor.LONG_TYPE_NAME;
import static org.reflection_no_reflection.generator.introspector.IntrospectorDumperClassPoolVisitor.FLOAT_TYPE_NAME;
import static org.reflection_no_reflection.generator.introspector.IntrospectorDumperClassPoolVisitor.DOUBLE_TYPE_NAME;
import static org.reflection_no_reflection.generator.introspector.IntrospectorDumperClassPoolVisitor.CHAR_TYPE_NAME;
import static org.reflection_no_reflection.generator.introspector.IntrospectorDumperClassPoolVisitor.BOOLEAN_TYPE_NAME;
import static org.reflection_no_reflection.generator.introspector.IntrospectorDumperClassPoolVisitor.OBJECT_TYPE_NAME;
import static org.reflection_no_reflection.generator.introspector.IntrospectorDumperClassPoolVisitor.STRING_TYPE_NAME;

/**
 * @author SNI.
 */
public class IntrospectorFieldSetterMethodCreator {
    IntrospectorUtil util = new IntrospectorUtil();

    public MethodSpec createSetObjectFieldMethod(org.reflection_no_reflection.Class<?> aClass) {
        return createSetFieldMethod(aClass, OBJECT_TYPE_NAME, "setObjectField", false, new FieldEvaluator() {
            @Override public boolean accepts(Class<?> clazz) {
                return !clazz.isPrimitive();
            }
        });
    }

    public MethodSpec createSetByteFieldMethod(Class<?> aClass) {
        return createSetFieldMethod(aClass,  BYTE_TYPE_NAME, "setByteField", true, new FieldEvaluator() {
            @Override public boolean accepts(Class<?> clazz) {
                return clazz.isPrimitive() && clazz == Class.forNameSafe("byte");
            }
        });
    }

    public MethodSpec createSetShortFieldMethod(Class<?> aClass) {
        return createSetFieldMethod(aClass, SHORT_TYPE_NAME, "setShortField", true, new FieldEvaluator() {
            @Override public boolean accepts(Class<?> clazz) {
                return clazz.isPrimitive() && clazz == Class.forNameSafe("short");
            }
        });
    }

    public MethodSpec createSetIntFieldMethod(Class<?> aClass) {
        return createSetFieldMethod(aClass, INT_TYPE_NAME, "setIntField", true, new FieldEvaluator() {
            @Override public boolean accepts(Class<?> clazz) {
                return clazz.isPrimitive() && clazz == Class.forNameSafe("int");
            }
        });
    }

    public MethodSpec createSetLongFieldMethod(Class<?> aClass) {
        return createSetFieldMethod(aClass, LONG_TYPE_NAME, "setLongField", true, new FieldEvaluator() {
            @Override public boolean accepts(Class<?> clazz) {
                return clazz.isPrimitive() && clazz == Class.forNameSafe("long");
            }
        });
    }

    public MethodSpec createSetFloatFieldMethod(Class<?> aClass) {
        return createSetFieldMethod(aClass, FLOAT_TYPE_NAME, "setFloatField", true, new FieldEvaluator() {
            @Override public boolean accepts(Class<?> clazz) {
                return clazz.isPrimitive() && clazz == Class.forNameSafe("float");
            }
        });
    }

    public MethodSpec createSetDoubleFieldMethod(Class<?> aClass) {
        return createSetFieldMethod(aClass, DOUBLE_TYPE_NAME, "setDoubleField", true, new FieldEvaluator() {
            @Override public boolean accepts(Class<?> clazz) {
                return clazz.isPrimitive() && clazz == Class.forNameSafe("double");
            }
        });
    }

    public MethodSpec createSetCharFieldMethod(Class<?> aClass) {
        return createSetFieldMethod(aClass, CHAR_TYPE_NAME, "setCharField", true, new FieldEvaluator() {
            @Override public boolean accepts(Class<?> clazz) {
                return clazz.isPrimitive() && clazz == Class.forNameSafe("char");
            }
        });
    }

    public MethodSpec createSetBooleanFieldMethod(Class<?> aClass) {
        return createSetFieldMethod(aClass, BOOLEAN_TYPE_NAME, "setBooleanField", true, new FieldEvaluator() {
            @Override public boolean accepts(Class<?> clazz) {
                return clazz.isPrimitive() && clazz == Class.forNameSafe("boolean");
            }
        });
    }

    private MethodSpec createSetFieldMethod(Class<?> aClass, TypeName paramTypeName, String methodName, boolean isPrimitive, FieldEvaluator evaluator) {
        boolean hasFieldOfThatType = false;
        ParameterSpec parameterSpec1 = ParameterSpec.builder(OBJECT_TYPE_NAME, "instance").build();
        ParameterSpec parameterSpec2 = ParameterSpec.builder(STRING_TYPE_NAME, "name").build();
        ParameterSpec parameterSpec3 = ParameterSpec.builder(paramTypeName, "value").build();
        MethodSpec.Builder setFieldMethodBuilder = MethodSpec.methodBuilder(methodName)
            .addModifiers(Modifier.PUBLIC)
            .addAnnotation(Override.class)
            .addParameter(parameterSpec1)
            .addParameter(parameterSpec2)
            .addParameter(parameterSpec3)
            .addCode("switch(name) {\n");

        for (Field field : aClass.getFields()) {
            if( evaluator.accepts(field.getType())) {
                hasFieldOfThatType = true;
                final TypeName enclosingClassName = util.getClassName(aClass);
                setFieldMethodBuilder
                    .addCode("case($S) :", field.getName());
                TypeName fieldTypeName = util.getClassName(field.getType());
                if (isPrimitive) {
                    setFieldMethodBuilder.addStatement("(($T) instance).$L = value", enclosingClassName, field.getName());
                } else {
                    setFieldMethodBuilder.addStatement("(($T) instance).$L = ($T) value", enclosingClassName, field.getName(), fieldTypeName);
                }
                setFieldMethodBuilder.addStatement("break");
            }
        }

        if( !hasFieldOfThatType) {
            return null;
        }

        setFieldMethodBuilder.addStatement("}");
        return setFieldMethodBuilder.build();
    }

    interface FieldEvaluator {
        boolean accepts(Class<?> clazz);
    }

}
