package org.reflection_no_reflection.generator.introspector;

import com.squareup.javapoet.ArrayTypeName;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;
import org.reflection_no_reflection.*;

public class IntrospectorUtil {

    public TypeName getClassName(org.reflection_no_reflection.Class<?> clazz) {
        return getClassName(clazz.getName());
    }

    public TypeName getClassName(String className) {
        if (className.endsWith("[]")) {
            final String componentName = className.substring(0, className.lastIndexOf('['));
            return ArrayTypeName.of(getClassName(componentName));
        } else if (className.contains(".")) {
            final String packageName = className.substring(0, className.lastIndexOf('.'));
            final String simpleName = className.substring(className.lastIndexOf('.')+1);
            return ClassName.get(packageName, simpleName);
        } else {
            //for primitives
            switch (className) {
                case "short" :
                    return TypeName.get(short.class);
                case "byte" :
                    return TypeName.get(byte.class);
                case "int" :
                    return TypeName.get(int.class);
                case "long" :
                    return TypeName.get(long.class);
                case "float" :
                    return TypeName.get(float.class);
                case "double" :
                    return TypeName.get(double.class);
                case "boolean" :
                    return TypeName.get(boolean.class);
                case "char" :
                    return TypeName.get(char.class);
            }
            throw new RuntimeException("Impossible to get typename for " + className);
        }
    }

    public String createCapitalizedName(String fieldName) {
        return Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
    }
}
