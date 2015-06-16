package org.reflection_no_reflection.generator.sample;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
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
        assertThat(methods.size(), is(11));
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
    public void shouldReflectSimpleMethod() throws ClassNotFoundException, InvocationTargetException {
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
    public void shouldReflectMethodReturningString() throws ClassNotFoundException, InvocationTargetException {
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
    public void shouldReflectMethodReturningInt() throws ClassNotFoundException, InvocationTargetException {
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
    public void shouldReflectMethodReturningIntArray() throws ClassNotFoundException, InvocationTargetException {
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

    @Test
    public void shouldReflectMethodWithIntParam() throws ClassNotFoundException, InvocationTargetException {
        //GIVEN

        //WHEN
        Class<?> classFoo = Class.forName(METHOD_TEST_CASE_CLASS_NAME);

        //THEN
        final List<Method> methods = classFoo.getMethods();
        final Method method = methods.get(4);
        assertThat(method.getName(), is("methodWithPrimitiveParam"));
        assertThat(method.getParameterTypes().length, is(1));
        assertThat(method.getParameterTypes()[0].getName(), is("int"));
        MethodTestCase methodTestCase = new MethodTestCase();
        method.invoke(methodTestCase, 3);
        assertThat(methodTestCase.a, is(3));
    }

    @Test
    public void shouldReflectMethodWithObjectParam() throws ClassNotFoundException, InvocationTargetException {
        //GIVEN

        //WHEN
        Class<?> classFoo = Class.forName(METHOD_TEST_CASE_CLASS_NAME);

        //THEN
        final List<Method> methods = classFoo.getMethods();
        final Method method = methods.get(5);
        assertThat(method.getName(), is("methodWithObjectParam"));
        assertThat(method.getParameterTypes().length, is(1));
        assertThat(method.getParameterTypes()[0].getName(), is("java.lang.String"));
        MethodTestCase methodTestCase = new MethodTestCase();
        method.invoke(methodTestCase, "s");
        assertThat(methodTestCase.a, is(3));
    }

    @Test
    public void shouldReflectMethodWithArrayNotLastParam() throws ClassNotFoundException, InvocationTargetException {
        //GIVEN

        //WHEN
        Class<?> classFoo = Class.forName(METHOD_TEST_CASE_CLASS_NAME);

        //THEN
        final List<Method> methods = classFoo.getMethods();
        final Method method = methods.get(6);
        assertThat(method.getName(), is("methodWithArrayNotLastParam"));
        assertThat(method.getParameterTypes().length, is(2));
        assertThat(method.getParameterTypes()[0].getName(), is("java.lang.String[]"));
        assertThat(method.getParameterTypes()[1].getName(), is("int"));
        MethodTestCase methodTestCase = new MethodTestCase();
        method.invoke(methodTestCase, new String[] {"s"}, 0);
        assertThat(methodTestCase.a, is(3));
    }

    @Test
    public void shouldReflectMethodWithArrayLastParam() throws ClassNotFoundException, InvocationTargetException {
        //GIVEN

        //WHEN
        Class<?> classFoo = Class.forName(METHOD_TEST_CASE_CLASS_NAME);

        //THEN
        final List<Method> methods = classFoo.getMethods();
        final Method method = methods.get(7);
        assertThat(method.getName(), is("methodWithArrayLastParam"));
        assertThat(method.getParameterTypes().length, is(1));
        assertThat(method.getParameterTypes()[0].getName(), is("java.lang.String[]"));
        MethodTestCase methodTestCase = new MethodTestCase();
        method.invoke(methodTestCase, new String[] {"s"});
        assertThat(methodTestCase.a, is(3));
    }

    @Test
    public void shouldReflectMethodWithVarArgs() throws ClassNotFoundException, InvocationTargetException {
        //GIVEN

        //WHEN
        Class<?> classFoo = Class.forName(METHOD_TEST_CASE_CLASS_NAME);

        //THEN
        final List<Method> methods = classFoo.getMethods();
        final Method method = methods.get(8);
        assertThat(method.getName(), is("methodWithVarArgsParam"));
        assertThat(method.getParameterTypes().length, is(1));
        assertThat(method.getParameterTypes()[0].getName(), is("java.lang.String[]"));
        assertThat(method.isVarArgs(), is(true));
        MethodTestCase methodTestCase = new MethodTestCase();
        method.invoke(methodTestCase, new String[] {"s"});
        assertThat(methodTestCase.a, is(3));
    }

    @Test
    public void shouldReflectMethodWithGenericsParam() throws ClassNotFoundException, InvocationTargetException {
        //GIVEN

        //WHEN
        Class<?> classFoo = Class.forName(METHOD_TEST_CASE_CLASS_NAME);

        //THEN
        final List<Method> methods = classFoo.getMethods();
        final Method method = methods.get(9);
        assertThat(method.getName(), is("methodWithGenericsParam"));
        assertThat(method.getParameterTypes().length, is(1));
        assertThat(method.getParameterTypes()[0].getName(), is("java.util.List"));
        MethodTestCase methodTestCase = new MethodTestCase();
        method.invoke(methodTestCase, new ArrayList());
        assertThat(methodTestCase.a, is(3));
    }

    @Test
    public void shouldReflectMethodThrowingException() throws ClassNotFoundException, InvocationTargetException {
        //GIVEN

        //WHEN
        Class<?> classFoo = Class.forName(METHOD_TEST_CASE_CLASS_NAME);

        //THEN
        final List<Method> methods = classFoo.getMethods();
        final Method method = methods.get(10);
        assertThat(method.getName(), is("methodWithException"));
        assertThat(method.getParameterTypes().length, is(0));
        assertThat(method.getExceptionTypes().length, is(1));
        assertThat(method.getExceptionTypes()[0].getName(), is("java.lang.Exception"));
        MethodTestCase methodTestCase = new MethodTestCase();
        method.invoke(methodTestCase, new String[] {"s"});
        assertThat(methodTestCase.a, is(3));
    }
}
