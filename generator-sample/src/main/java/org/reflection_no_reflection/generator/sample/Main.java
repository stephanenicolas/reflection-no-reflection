package org.reflection_no_reflection.generator.sample;

import java.util.Set;
import org.reflection_no_reflection.Class;
import org.reflection_no_reflection.Field;
import org.reflection_no_reflection.runtime.Module;

/**
 * @author SNI.
 */
public class Main {

    public static void main(String[] args) {
        final A a = new A();
        Module module = new org.reflection_no_reflection.generator.sample.gen.ModuleImpl();
        org.reflection_no_reflection.Class.loadModule(module);
        try {
            final Class<?> classFoo = Class.forName("org.reflection_no_reflection.generator.sample.A");
            final Field[] fields = classFoo.getFields();
            System.out.println(fields[0].getName());  //should be b
            System.out.println(fields[0].getType().getName());  //should be B
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        final Set<Class> classesContainingInject = module.getMapOfAnnotationTypeToClassesContainingAnnotation().get(Class.forNameSafe("javax.inject.Inject"));
        final Class clazz = classesContainingInject.iterator().next();
        System.out.println(clazz.getName()); //should be A

        final Field[] fields = clazz.getFields();
        System.out.println(a.b); //should be null
        fields[0].set(a, new B());
        System.out.println(a.b); //should be non null
    }
}
