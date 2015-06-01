package org.reflection_no_reflection.generator.introspector;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;
import org.reflection_no_reflection.*;

/**
 * @author SNI.
 */
public class IntrospectorUtil {

    public TypeName getClassName(org.reflection_no_reflection.Class<?> clazz) {
        final String className = clazz.getName();
        if (className.contains(".")) {
            final String packageName = className.substring(0, className.lastIndexOf('.'));
            return ClassName.get(packageName, clazz.getSimpleName());
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
