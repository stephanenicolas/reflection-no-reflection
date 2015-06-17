package org.reflection_no_reflection.generator.sample;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import org.junit.Before;
import org.junit.Test;
import org.reflection_no_reflection.Class;
import org.reflection_no_reflection.Constructor;
import org.reflection_no_reflection.Method;
import org.reflection_no_reflection.runtime.Module;

import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class ConstructorTest {

    public static final String CONSTRUCTOR_TEST_CASE_CLASS_NAME = "org.reflection_no_reflection.generator.sample.ConstructorTestCase";

    @Before
    public void setUp() throws Exception {
        Class.clearAllClasses();
        Module module = new org.reflection_no_reflection.generator.sample.gen.ModuleImpl();
        Class.loadModule(module);
    }

    @Test
    public void shouldDetectAllConstructors() throws ClassNotFoundException {
        //GIVEN

        //WHEN
        Class<ConstructorTestCase> classFoo = (Class<ConstructorTestCase>) Class.forName("org.reflection_no_reflection.generator.sample.ConstructorTestCase");

        //THEN
        assertNotNull(classFoo);

        final List<Constructor<ConstructorTestCase>> constructors = classFoo.getConstructors();
        assertNotNull(constructors);
        assertThat(constructors.size(), is(1));
    }

    @Test
    public void shouldDetectAnnotationsOfConstructor() throws ClassNotFoundException {
        //GIVEN

        //WHEN
        Class<ConstructorTestCase> classFoo = (Class<ConstructorTestCase>) Class.forName("org.reflection_no_reflection.generator.sample.ConstructorTestCase");

        //THEN
        final List<Constructor<ConstructorTestCase>> constructors = classFoo.getConstructors();
        final Constructor<?> constructor = constructors.get(0);
        assertThat(constructor.getAnnotation(Inject.class).annotationType(), sameInstance((java.lang.Class) Inject.class));
    }

    @Test
    public void shouldReflectSimpleConstructor() throws ClassNotFoundException, InvocationTargetException, InstantiationException {
        //GIVEN

        //WHEN
        Class<ConstructorTestCase> classFoo = (Class<ConstructorTestCase>) Class.forName("org.reflection_no_reflection.generator.sample.ConstructorTestCase");

        //THEN
        final List<Constructor<ConstructorTestCase>> constructors = classFoo.getConstructors();
        final Constructor<ConstructorTestCase> constructor = constructors.get(0);
        assertThat(constructor.getName(), is("ConstructorTestCase"));
        ConstructorTestCase methodTestCase = constructor.newInstance();
        assertThat(methodTestCase.a, is(3));
    }

}
