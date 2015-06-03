package org.reflection_no_reflection.generator.introspector;

import com.squareup.javapoet.ArrayTypeName;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;
import org.reflection_no_reflection.*;

/**
 * @author SNI.
 */
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
                case "int" :
                    return TypeName.get(int.class);
            }
            return null;
        }
    }

}
