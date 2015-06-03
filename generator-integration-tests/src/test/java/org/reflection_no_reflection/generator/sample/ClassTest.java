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
    public void classesShouldBeUniq() throws Exception {
        //GIVEN
        final Class stringClass = Class.forNameSafe("java.lang.String");

        //WHEN
        final Class stringClass2 = Class.forNameSafe("java.lang.String");

        //THEN
        assertThat(stringClass2, sameInstance(stringClass));
    }

    @Test
    public void classPoolContainsClasses() throws Exception {
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


}
