package org.reflection_no_reflection.generator.sample;

import org.reflection_no_reflection.Class;
import org.reflection_no_reflection.Field;

/**
 * @author SNI.
 */
public class Main {

    public static void main(String[] args) {
        new A();
        org.reflection_no_reflection.generator.sample.gen.ModuleImpl module = new org.reflection_no_reflection.generator.sample.gen.ModuleImpl();
        org.reflection_no_reflection.Class.loadModule(module);
        try {
            final Class<?> classFoo = Class.forName("org.reflection_no_reflection.generator.sample.A");
            final Field[] fields = classFoo.getFields();
            System.out.println(fields[0].getName());  //should be B
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
