package org.reflection_no_reflection.generator.sample;

import java.util.Set;
import javax.inject.Inject;
import org.reflection_no_reflection.Class;
import org.reflection_no_reflection.Field;
import org.reflection_no_reflection.Method;
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

        System.out.println(a.c); //should be 0
        fields[1].setByte(a, (byte) 2);
        System.out.println(a.c); //should be 2

        System.out.println(a.d); //should be 0
        fields[2].setShort(a, (short) 2);
        System.out.println(a.d); //should be 2

        System.out.println(a.e); //should be 0
        fields[3].setInt(a, 2);
        System.out.println(a.e); //should be 2

        System.out.println(a.f); //should be 0
        fields[4].setLong(a, (long) 2);
        System.out.println(a.f); //should be 2

        System.out.println(a.g); //should be 0
        fields[5].setFloat(a, (float) 2);
        System.out.println(a.g); //should be 2

        System.out.println(a.h); //should be 0
        fields[6].setDouble(a, (double) 2);
        System.out.println(a.h); //should be 2

        System.out.println(a.i); //should be
        fields[7].setChar(a, '2');
        System.out.println(a.i); //should be 2

        System.out.println(a.j); //should be false
        fields[8].setBoolean(a, true);
        System.out.println(a.j); //should be true

        System.out.println(fields[0].getAnnotation(Inject.class).annotationType().getName()); //should be javax.inject.Inject

        System.out.println(((Method) clazz.getMethods().get(0)).getName()); //should be m
        System.out.println(((Method) clazz.getMethods().get(0)).toString()); //should be m
        ((Method) clazz.getMethods().get(0)).invoke(a);
        System.out.println(a.e); //should be 3

    }
}
