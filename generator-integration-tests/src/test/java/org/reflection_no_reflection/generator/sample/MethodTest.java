package org.reflection_no_reflection.generator.sample;

import java.util.List;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.reflection_no_reflection.Class;
import org.reflection_no_reflection.Method;
import org.reflection_no_reflection.runtime.Module;

import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class MethodTest {

    public static final String METHOD_TEST_CASE_CLASS_NAME = "org.reflection_no_reflection.generator.sample.MethodTestCase";

    @Before
    public void setUp() throws Exception {
        Class.clearAllClasses();
        Module module = new org.reflection_no_reflection.generator.sample.gen.ModuleImpl();
        Class.loadModule(module);
    }

    @Test
    public void shouldDetectAllMethods() throws ClassNotFoundException {
        //GIVEN

        //WHEN
        Class<?> classFoo = Class.forName("org.reflection_no_reflection.generator.sample.MethodTestCase");

        //THEN
        assertNotNull(classFoo);

        final List<Method> methods = classFoo.getMethods();
        assertNotNull(methods);
        assertThat(methods.size(), is(4));
    }

    @Test @Ignore
    public void shouldDetectAnnotationsOfMethod() throws ClassNotFoundException {
        //GIVEN

        //WHEN
        Class<?> classFoo = Class.forName("org.reflection_no_reflection.generator.sample.MethodTestCase");

        //THEN
        final List<Method> methods = classFoo.getMethods();
        final Method method = methods.get(0);
        assertThat(method.getAnnotations().length, is(1));
        assertThat((Class) method.getAnnotations()[0].annotationType(), sameInstance((Class) Class.forNameSafe("javax.inject.Inject")));
    }

    @Test
    public void shouldReflectSimpleMethod() throws ClassNotFoundException {
        //GIVEN

        //WHEN
        Class<?> classFoo = Class.forName("org.reflection_no_reflection.generator.sample.MethodTestCase");

        //THEN
        final List<Method> methods = classFoo.getMethods();
        final Method method = methods.get(0);
        assertThat(method.getName(), is("simpleMethod"));
        assertThat(method.getReturnType().getName(), is("void"));
        MethodTestCase methodTestCase = new MethodTestCase();
        assertThat(methodTestCase.a, is(0));
        method.invoke(methodTestCase);
        assertThat(methodTestCase.a, is(3));
    }

    @Test
    public void shouldReflectMethodReturningString() throws ClassNotFoundException {
        //GIVEN

        //WHEN
        Class<?> classFoo = Class.forName("org.reflection_no_reflection.generator.sample.MethodTestCase");

        //THEN
        final List<Method> methods = classFoo.getMethods();
        final Method method = methods.get(1);
        assertThat(method.getName(), is("methodReturningString"));
        assertThat(method.getReturnType().getName(), is("java.lang.String"));
        MethodTestCase methodTestCase = new MethodTestCase();
        Object obj = method.invoke(methodTestCase);
        assertThat((String) obj, is("foo"));
    }

    @Test
    public void shouldReflectMethodReturningInt() throws ClassNotFoundException {
        //GIVEN

        //WHEN
        Class<?> classFoo = Class.forName("org.reflection_no_reflection.generator.sample.MethodTestCase");

        //THEN
        final List<Method> methods = classFoo.getMethods();
        final Method method = methods.get(2);
        assertThat(method.getName(), is("methodReturningInt"));
        assertThat(method.getReturnType().getName(), is("int"));
        MethodTestCase methodTestCase = new MethodTestCase();
        Object obj = method.invoke(methodTestCase);
        assertThat((int) obj, is(3));
    }

    @Test
    public void shouldReflectMethodReturningIntArray() throws ClassNotFoundException {
        //GIVEN

        //WHEN
        Class<?> classFoo = Class.forName(METHOD_TEST_CASE_CLASS_NAME);

        //THEN
        final List<Method> methods = classFoo.getMethods();
        final Method method = methods.get(3);
        assertThat(method.getName(), is("methodReturningIntArray"));
        assertThat(method.getReturnType().getName(), is("int[]"));
        MethodTestCase methodTestCase = new MethodTestCase();
        Object obj = method.invoke(methodTestCase);
        assertThat((int[]) obj, is(new int[] {3}));
    }
}
