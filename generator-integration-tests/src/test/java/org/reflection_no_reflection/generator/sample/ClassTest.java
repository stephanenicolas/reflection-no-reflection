package org.reflection_no_reflection.generator.sample;

import org.junit.Before;
import org.junit.Test;
import org.reflection_no_reflection.Class;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsSame.sameInstance;
import static org.junit.Assert.assertThat;

public class ClassTest {

    @Before
    public void setUp() throws Exception {
        Class.clearAllClasses();
    }

    @Test
    public void classesShouldBeUnique() throws Exception {
        //GIVEN
        final Class stringClass = Class.forNameSafe("java.lang.String");

        //WHEN
        final Class stringClass2 = Class.forNameSafe("java.lang.String");

        //THEN
        assertThat(stringClass2, sameInstance(stringClass));
    }

    @Test
    public void classPoolContainsClasses() {
        //GIVEN
        final Class stringClass = Class.forNameSafe("java.lang.String");

        //WHEN

        //THEN
        assertThat(Class.getClassPool().size(), is(1));
    }

    @Test
    public void classPoolCanBeCleared() throws Exception {
        //GIVEN
        final Class stringClass = Class.forNameSafe("java.lang.String");

        //WHEN
        Class.clearAllClasses();

        //THEN
        assertThat(Class.getClassPool().size(), is(0));
    }

    @Test(expected = ClassNotFoundException.class)
    public void classForNameShouldFailIfClassDoesNotExist() throws Exception {
        //GIVEN
        //WHEN
        final Class stringClass = Class.forName("java.lang.String");
        //THEN
    }

    @Test
    public void classForNameSafeShouldNotFailIfClassDoesNotExist() {
        //GIVEN
        //WHEN
        final Class stringClass = Class.forNameSafe("java.lang.String");
        //THEN
    }

    @Test
    public void primitiveClassesShouldBeWellDefined() {
        //GIVEN
        //WHEN
        final Class shortClass = Class.forNameSafe("short");
        final Class byteClass = Class.forNameSafe("byte");
        final Class intClass = Class.forNameSafe("int");
        final Class longClass = Class.forNameSafe("long");
        final Class floatClass = Class.forNameSafe("float");
        final Class doubleClass = Class.forNameSafe("double");
        final Class charClass = Class.forNameSafe("char");
        final Class booleanClass = Class.forNameSafe("boolean");
        final Class objectClass = Class.forNameSafe("java.lang.Object");
        
        //THEN
        assertThat(shortClass.getName(),is("short"));
        assertThat(byteClass.getName(),is("byte"));
        assertThat(intClass.getName(),is("int"));
        assertThat(longClass.getName(),is("long"));
        assertThat(floatClass.getName(),is("float"));
        assertThat(doubleClass.getName(),is("double"));
        assertThat(charClass.getName(),is("char"));
        assertThat(booleanClass.getName(),is("boolean"));

        assertThat(shortClass.isPrimitive(),is(true));
        assertThat(byteClass.isPrimitive(),is(true));
        assertThat(intClass.isPrimitive(),is(true));
        assertThat(longClass.isPrimitive(),is(true));
        assertThat(floatClass.isPrimitive(),is(true));
        assertThat(doubleClass.isPrimitive(),is(true));
        assertThat(charClass.isPrimitive(),is(true));
        assertThat(booleanClass.isPrimitive(),is(true));
        assertThat(objectClass.isPrimitive(),is(false));
    }
}
