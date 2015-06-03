package org.reflection_no_reflection.generator.sample;

import org.junit.Before;
import org.junit.Test;
import org.reflection_no_reflection.Class;
import org.reflection_no_reflection.Field;
import org.reflection_no_reflection.runtime.Module;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

public class FieldTest {

    @Before
    public void setUp() throws Exception {
        Class.clearAllClasses();
        Module module = new org.reflection_no_reflection.generator.sample.gen.ModuleImpl();
        Class.loadModule(module);
    }

    @Test
    public void shouldDetectAllFields() throws ClassNotFoundException {
        //GIVEN

        //WHEN
        Class<?> classFoo = Class.forName("org.reflection_no_reflection.generator.sample.FieldTestCase");

        //THEN
        assertNotNull(classFoo);

        final Field[] fields = classFoo.getFields();
        assertNotNull(fields);
        assertThat(fields.length, is(10));
    }

    @Test
    public void shouldReflectStringField() throws ClassNotFoundException {
        //GIVEN

        //WHEN
        Class<?> classFoo = Class.forName("org.reflection_no_reflection.generator.sample.FieldTestCase");

        //THEN
        final Field[] fields = classFoo.getFields();
        final Field field = fields[0];
        assertThat(field.getName(), is("a"));
        assertThat(field.getType().getName(), is("java.lang.String"));
        FieldTestCase fieldTestCase = new FieldTestCase();
        assertNull(fieldTestCase.a);
        field.set(fieldTestCase, "foo");
        assertThat(fieldTestCase.a, is("foo"));
        //TODO getter
    }

    @Test
    public void shouldReflectObjectField() throws ClassNotFoundException {
        //GIVEN

        //WHEN
        Class<?> classFoo = Class.forName("org.reflection_no_reflection.generator.sample.FieldTestCase");

        //THEN
        final Field[] fields = classFoo.getFields();
        final Field field = fields[1];
        assertThat(field.getName(), is("foo"));
        assertThat(field.getType().getName(), is("org.reflection_no_reflection.generator.sample.Foo"));
        FieldTestCase fieldTestCase = new FieldTestCase();
        assertNull(fieldTestCase.foo);
        field.set(fieldTestCase, new Foo());
        assertNotNull(fieldTestCase.foo);
        //TODO getter
    }

    @Test
    public void shouldReflectByteField() throws ClassNotFoundException {
        //GIVEN

        //WHEN
        Class<?> classFoo = Class.forName("org.reflection_no_reflection.generator.sample.FieldTestCase");

        //THEN
        final Field[] fields = classFoo.getFields();
        final Field field = fields[2];
        assertThat(field.getName(), is("c"));
        assertThat(field.getType().getName(), is("byte"));
        FieldTestCase fieldTestCase = new FieldTestCase();
        assertThat(fieldTestCase.c, is((byte) 0));
        field.setByte(fieldTestCase, (byte) 2);
        assertThat(fieldTestCase.c, is((byte) 2));
        //TODO getter
    }

    @Test
    public void shouldReflectShortField() throws ClassNotFoundException {
        //GIVEN

        //WHEN
        Class<?> classFoo = Class.forName("org.reflection_no_reflection.generator.sample.FieldTestCase");

        //THEN
        final Field[] fields = classFoo.getFields();
        final Field field = fields[3];
        assertThat(field.getName(), is("d"));
        assertThat(field.getType().getName(), is("short"));
        FieldTestCase fieldTestCase = new FieldTestCase();
        assertThat(fieldTestCase.d, is((short) 0));
        field.setShort(fieldTestCase, (short) 2);
        assertThat(fieldTestCase.d, is((short)2));
        //TODO getter
    }

    @Test
    public void shouldReflectIntField() throws ClassNotFoundException {
        //GIVEN

        //WHEN
        Class<?> classFoo = Class.forName("org.reflection_no_reflection.generator.sample.FieldTestCase");

        //THEN
        final Field[] fields = classFoo.getFields();
        final Field field = fields[4];
        assertThat(field.getName(), is("e"));
        assertThat(field.getType().getName(), is("int"));
        FieldTestCase fieldTestCase = new FieldTestCase();
        assertThat(fieldTestCase.e, is(0));
        field.setInt(fieldTestCase, 2);
        assertThat(fieldTestCase.e, is(2));
        //TODO getter
    }

    @Test
    public void shouldReflectLongField() throws ClassNotFoundException {
        //GIVEN

        //WHEN
        Class<?> classFoo = Class.forName("org.reflection_no_reflection.generator.sample.FieldTestCase");

        //THEN
        final Field[] fields = classFoo.getFields();
        final Field field = fields[5];
        assertThat(field.getName(), is("f"));
        assertThat(field.getType().getName(), is("long"));
        FieldTestCase fieldTestCase = new FieldTestCase();
        assertThat(fieldTestCase.f, is((long)0));
        field.setLong(fieldTestCase, 2);
        assertThat(fieldTestCase.f, is((long)2));
        //TODO getter
    }

    @Test
    public void shouldReflectFloatField() throws ClassNotFoundException {
        //GIVEN

        //WHEN
        Class<?> classFoo = Class.forName("org.reflection_no_reflection.generator.sample.FieldTestCase");

        //THEN
        final Field[] fields = classFoo.getFields();
        final Field field = fields[6];
        assertThat(field.getName(), is("g"));
        assertThat(field.getType().getName(), is("float"));
        FieldTestCase fieldTestCase = new FieldTestCase();
        assertThat(fieldTestCase.g, is((float)0));
        field.setFloat(fieldTestCase, 2f);
        assertThat(fieldTestCase.g, is((float)2));
        //TODO getter
    }


    @Test
    public void shouldReflectDoubleField() throws ClassNotFoundException {
        //GIVEN

        //WHEN
        Class<?> classFoo = Class.forName("org.reflection_no_reflection.generator.sample.FieldTestCase");

        //THEN
        final Field[] fields = classFoo.getFields();
        final Field field = fields[7];
        assertThat(field.getName(), is("h"));
        assertThat(field.getType().getName(), is("double"));
        FieldTestCase fieldTestCase = new FieldTestCase();
        assertThat(fieldTestCase.h, is((double)0));
        field.setDouble(fieldTestCase, 2f);
        assertThat(fieldTestCase.h, is((double)2));
        //TODO getter
    }

    @Test
    public void shouldReflectCharField() throws ClassNotFoundException {
        //GIVEN

        //WHEN
        Class<?> classFoo = Class.forName("org.reflection_no_reflection.generator.sample.FieldTestCase");

        //THEN
        final Field[] fields = classFoo.getFields();
        final Field field = fields[8];
        assertThat(field.getName(), is("i"));
        assertThat(field.getType().getName(), is("char"));
        FieldTestCase fieldTestCase = new FieldTestCase();
        assertThat(fieldTestCase.i, is('\u0000'));
        field.setChar(fieldTestCase, 'a');
        assertThat(fieldTestCase.i, is('a'));
        //TODO getter
    }

    @Test
    public void shouldReflectBooleanField() throws ClassNotFoundException {
        //GIVEN

        //WHEN
        Class<?> classFoo = Class.forName("org.reflection_no_reflection.generator.sample.FieldTestCase");

        //THEN
        final Field[] fields = classFoo.getFields();
        final Field field = fields[9];
        assertThat(field.getName(), is("j"));
        assertThat(field.getType().getName(), is("boolean"));
        FieldTestCase fieldTestCase = new FieldTestCase();
        assertThat(fieldTestCase.j, is(false));
        field.setBoolean(fieldTestCase, true);
        assertThat(fieldTestCase.j, is(true));
        //TODO getter
    }
}
