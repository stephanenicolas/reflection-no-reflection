package org.reflection_no_reflection.integration_tests;

import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import javax.inject.Inject;
import org.hamcrest.core.Is;
import org.junit.Before;
import org.junit.Test;
import org.reflection_no_reflection.Field;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.fail;

/**
 * Base class of both all RNR fields implementations.
 * This class tests if a given implementation of RNR will provide both correct results
 * and identical results to pure java core reflection.
 * @author SNI.
 */
public abstract class FieldTest {

    //pure java reflection fields
    private java.lang.reflect.Field trueReflectionFieldPublicObjectField;
    private java.lang.reflect.Field trueReflectionFieldPublicPrimitiveIntField;
    private java.lang.reflect.Field trueReflectionFieldPublicPrimitiveByteField;
    private java.lang.reflect.Field trueReflectionFieldPublicPrimitiveShortField;
    private java.lang.reflect.Field trueReflectionFieldPublicPrimitiveLongField;
    private java.lang.reflect.Field trueReflectionFieldPublicPrimitiveFloatField;
    private java.lang.reflect.Field trueReflectionFieldPublicPrimitiveDoubleField;
    private java.lang.reflect.Field trueReflectionFieldPublicPrimitiveBooleanField;
    private java.lang.reflect.Field trueReflectionFieldPublicPrimitiveCharField;

    private java.lang.reflect.Field trueReflectionFieldPublicWrapperIntegerField;

    //fields obtained via RNR
    private Field rnrPublicObjectField;
    private Field rnrPublicPrimitiveIntField;
    private Field rnrPublicPrimitiveByteField;
    private Field rnrPublicPrimitiveShortField;
    private Field rnrPublicPrimitiveLongField;
    private Field rnrPublicPrimitiveFloatField;
    private Field rnrPublicPrimitiveDoubleField;
    private Field rnrPublicPrimitiveBooleanField;
    private Field rnrPublicPrimitiveCharField;

    private Field rnrPublicWrapperIntegerField;

    @Before
    public void setUp() throws NoSuchFieldException {
        //pure java reflection fields
        //primitives
        trueReflectionFieldPublicObjectField = A.class.getDeclaredField("publicObjectField");
        trueReflectionFieldPublicPrimitiveIntField = A.class.getDeclaredField("publicPrimitiveIntField");
        trueReflectionFieldPublicPrimitiveByteField = A.class.getDeclaredField("publicPrimitiveByteField");
        trueReflectionFieldPublicPrimitiveShortField = A.class.getDeclaredField("publicPrimitiveShortField");
        trueReflectionFieldPublicPrimitiveLongField = A.class.getDeclaredField("publicPrimitiveLongField");
        trueReflectionFieldPublicPrimitiveFloatField = A.class.getDeclaredField("publicPrimitiveFloatField");
        trueReflectionFieldPublicPrimitiveDoubleField = A.class.getDeclaredField("publicPrimitiveDoubleField");
        trueReflectionFieldPublicPrimitiveBooleanField = A.class.getDeclaredField("publicPrimitiveBooleanField");
        trueReflectionFieldPublicPrimitiveCharField = A.class.getDeclaredField("publicPrimitiveCharField");

        //wrappers
        trueReflectionFieldPublicWrapperIntegerField = A.class.getDeclaredField("publicWrapperIntegerField");

        //fields obtained via RNR
        //primitives
        rnrPublicObjectField = getField(A.class, "publicObjectField");
        rnrPublicPrimitiveIntField = getField(A.class, "publicPrimitiveIntField");
        rnrPublicPrimitiveByteField = getField(A.class, "publicPrimitiveByteField");
        rnrPublicPrimitiveShortField = getField(A.class, "publicPrimitiveShortField");
        rnrPublicPrimitiveLongField = getField(A.class, "publicPrimitiveLongField");
        rnrPublicPrimitiveFloatField = getField(A.class, "publicPrimitiveFloatField");
        rnrPublicPrimitiveDoubleField = getField(A.class, "publicPrimitiveDoubleField");
        rnrPublicPrimitiveBooleanField = getField(A.class, "publicPrimitiveBooleanField");
        rnrPublicPrimitiveCharField = getField(A.class, "publicPrimitiveCharField");

        //wrappers
        rnrPublicWrapperIntegerField = getField(A.class, "publicWrapperIntegerField");
    }

    public abstract Field getField(Class<?> clazz, String fieldName);

    @Test
    public void testGetName() throws NoSuchFieldException {
        //GIVEN

        //WHEN
        String fieldName = rnrPublicObjectField.getName();

        //THEN
        assertThat(fieldName, is(trueReflectionFieldPublicObjectField.getName()));
        assertThat(fieldName, is("publicObjectField"));
    }

    @Test
    public void testGetType() throws NoSuchFieldException {
        //GIVEN

        //WHEN
        Class fieldType = rnrPublicObjectField.getType();

        //THEN
        assertThat(fieldType, Is.<Class>is(trueReflectionFieldPublicObjectField.getType()));
        assertThat(fieldType, Is.<Class>is(B.class));
    }

    @Test
    public void testGetGenericType() throws NoSuchFieldException {
        //GIVEN

        //WHEN
        Type type = rnrPublicObjectField.getGenericType();

        //THEN
        assertThat(type, Is.<Type>is(trueReflectionFieldPublicObjectField.getType()));
        assertThat(type, Is.<Type>is(B.class));
    }

    @Test
    public void testGetDeclaringClass() throws NoSuchFieldException {
        //GIVEN

        //WHEN
        Class fieldType = rnrPublicObjectField.getDeclaringClass();

        //THEN
        assertThat(fieldType, Is.<Class>is(trueReflectionFieldPublicObjectField.getDeclaringClass()));
        assertThat(fieldType, Is.<Class>is(A.class));
    }

    @Test
    public void testGetModifiers() throws NoSuchFieldException {
        //GIVEN

        //WHEN
        int fieldModifiers = rnrPublicObjectField.getModifiers();

        //THEN
        assertThat(fieldModifiers, is(trueReflectionFieldPublicObjectField.getModifiers()));
        assertThat(fieldModifiers, is(Modifier.PUBLIC));
    }

    @Test
    public void testGetAnnotationInject() throws NoSuchFieldException {
        //GIVEN

        //WHEN
        Inject fieldAnnotationInject = rnrPublicObjectField.getAnnotation(Inject.class);

        //THEN
        assertThat(fieldAnnotationInject, Is.<Inject>isA(Inject.class));
    }

    @Test
    public void testGetAnnotations() throws NoSuchFieldException {
        //GIVEN

        //WHEN
        Annotation[] annotations = rnrPublicObjectField.getDeclaredAnnotations();

        //THEN
        assertThat(annotations.length, is(1));
        assertThat((Inject) annotations[0], Is.<Inject>isA(Inject.class));
    }

    @Test
    public void testIsAnnotationPresent() throws NoSuchFieldException, IllegalAccessException {
        //GIVEN

        //WHEN
        A a = new A();
        a.publicObjectField = new B();
        boolean isInjectpresent = rnrPublicObjectField.isAnnotationPresent(Inject.class);

        //THEN
        assertThat(isInjectpresent, Is.is(trueReflectionFieldPublicObjectField.isAnnotationPresent(Inject.class)));
        assertThat(isInjectpresent, Is.is(true));
    }

    @Test
    public void testSet() throws NoSuchFieldException, IllegalAccessException {
        //GIVEN

        //WHEN
        B newB = new B();
        A a = new A();
        rnrPublicObjectField.set(a, newB);

        //THEN
        assertThat(a.publicObjectField, Is.<B>is((B) trueReflectionFieldPublicObjectField.get(a)));
        assertThat(a.publicObjectField, Is.<B>is(newB));
    }

    @Test
    public void testGet() throws NoSuchFieldException, IllegalAccessException {
        //GIVEN

        //WHEN
        A a = new A();
        a.publicObjectField = new B();
        B b = (B) rnrPublicObjectField.get(a);

        //THEN
        assertThat(b, Is.<B>is((B) trueReflectionFieldPublicObjectField.get(a)));
        assertThat(b, Is.<B>is(a.publicObjectField));
    }

    @Test
    public void testGetInt() throws NoSuchFieldException, IllegalAccessException {
        //GIVEN

        //WHEN
        A a = new A();
        a.publicPrimitiveIntField = 5;
        int b = rnrPublicPrimitiveIntField.getInt(a);

        //THEN
        assertThat(b, Is.is(trueReflectionFieldPublicPrimitiveIntField.getInt(a)));
        assertThat(b, Is.is(a.publicPrimitiveIntField));
    }

    @Test
    public void testSetInt() throws NoSuchFieldException, IllegalAccessException {
        //GIVEN

        //WHEN
        A a = new A();
        rnrPublicPrimitiveIntField.setInt(a, 5);
        int b = a.publicPrimitiveIntField;

        //THEN
        assertThat(b, Is.is(trueReflectionFieldPublicPrimitiveIntField.getInt(a)));
        assertThat(b, Is.is(5));
    }

    @Test
    public void testGetByte() throws NoSuchFieldException, IllegalAccessException {
        //GIVEN

        //WHEN
        A a = new A();
        a.publicPrimitiveByteField = 5;
        byte b = rnrPublicPrimitiveByteField.getByte(a);

        //THEN
        assertThat(b, Is.is(trueReflectionFieldPublicPrimitiveByteField.getByte(a)));
        assertThat(b, Is.is(a.publicPrimitiveByteField));
    }
    
    @Test
    public void testSetByte() throws NoSuchFieldException, IllegalAccessException {
        //GIVEN

        //WHEN
        A a = new A();
        rnrPublicPrimitiveByteField.setByte(a, (byte) 5);
        byte b = a.publicPrimitiveByteField;

        //THEN
        assertThat(b, Is.is(trueReflectionFieldPublicPrimitiveByteField.getByte(a)));
        assertThat(b, Is.is( (byte) 5));
    }

    @Test
    public void testGetShort() throws NoSuchFieldException, IllegalAccessException {
        //GIVEN

        //WHEN
        A a = new A();
        a.publicPrimitiveShortField = 5;
        short b = rnrPublicPrimitiveShortField.getShort(a);

        //THEN
        assertThat(b, Is.is(trueReflectionFieldPublicPrimitiveShortField.getShort(a)));
        assertThat(b, Is.is(a.publicPrimitiveShortField));
    }
    
    @Test
    public void testSetShort() throws NoSuchFieldException, IllegalAccessException {
        //GIVEN

        //WHEN
        A a = new A();
        rnrPublicPrimitiveShortField.setShort(a, (short) 5);
        short b = a.publicPrimitiveShortField;

        //THEN
        assertThat(b, Is.is(trueReflectionFieldPublicPrimitiveShortField.getShort(a)));
        assertThat(b, Is.is( (short) 5));
    }

    @Test
    public void testGetLong() throws NoSuchFieldException, IllegalAccessException {
        //GIVEN

        //WHEN
        A a = new A();
        a.publicPrimitiveLongField = 5;
        long b = rnrPublicPrimitiveLongField.getLong(a);

        //THEN
        assertThat(b, Is.is(trueReflectionFieldPublicPrimitiveLongField.getLong(a)));
        assertThat(b, Is.is(a.publicPrimitiveLongField));
    }
    
    @Test
    public void testSetLong() throws NoSuchFieldException, IllegalAccessException {
        //GIVEN

        //WHEN
        A a = new A();
        rnrPublicPrimitiveLongField.setLong(a, (long) 5);
        long b = a.publicPrimitiveLongField;

        //THEN
        assertThat(b, Is.is(trueReflectionFieldPublicPrimitiveLongField.getLong(a)));
        assertThat(b, Is.is( (long) 5));
    }


    @Test
    public void testGetFloat() throws NoSuchFieldException, IllegalAccessException {
        //GIVEN

        //WHEN
        A a = new A();
        a.publicPrimitiveFloatField = 5;
        float b = rnrPublicPrimitiveFloatField.getFloat(a);

        //THEN
        assertThat(b, Is.is(trueReflectionFieldPublicPrimitiveFloatField.getFloat(a)));
        assertThat(b, Is.is(a.publicPrimitiveFloatField));
    }

    @Test
    public void testSetFloat() throws NoSuchFieldException, IllegalAccessException {
        //GIVEN

        //WHEN
        A a = new A();
        rnrPublicPrimitiveFloatField.setFloat(a, (float) 5);
        float b = a.publicPrimitiveFloatField;

        //THEN
        assertThat(b, Is.is(trueReflectionFieldPublicPrimitiveFloatField.getFloat(a)));
        assertThat(b, Is.is( (float) 5));
    }
    
    @Test
    public void testGetDouble() throws NoSuchFieldException, IllegalAccessException {
        //GIVEN

        //WHEN
        A a = new A();
        a.publicPrimitiveDoubleField = 5;
        double b = rnrPublicPrimitiveDoubleField.getDouble(a);

        //THEN
        assertThat(b, Is.is(trueReflectionFieldPublicPrimitiveDoubleField.getDouble(a)));
        assertThat(b, Is.is(a.publicPrimitiveDoubleField));
    }

    @Test
    public void testSetDouble() throws NoSuchFieldException, IllegalAccessException {
        //GIVEN

        //WHEN
        A a = new A();
        rnrPublicPrimitiveDoubleField.setDouble(a, (double) 5);
        double b = a.publicPrimitiveDoubleField;

        //THEN
        assertThat(b, Is.is(trueReflectionFieldPublicPrimitiveDoubleField.getDouble(a)));
        assertThat(b, Is.is( (double) 5));
    }

    @Test
    public void testGetBoolean() throws NoSuchFieldException, IllegalAccessException {
        //GIVEN

        //WHEN
        A a = new A();
        a.publicPrimitiveBooleanField = true;
        boolean b = rnrPublicPrimitiveBooleanField.getBoolean(a);

        //THEN
        assertThat(b, Is.is(trueReflectionFieldPublicPrimitiveBooleanField.getBoolean(a)));
        assertThat(b, Is.is(a.publicPrimitiveBooleanField));
    }

    @Test
    public void testSetBoolean() throws NoSuchFieldException, IllegalAccessException {
        //GIVEN

        //WHEN
        A a = new A();
        rnrPublicPrimitiveBooleanField.setBoolean(a, true);
        boolean b = a.publicPrimitiveBooleanField;

        //THEN
        assertThat(b, Is.is(trueReflectionFieldPublicPrimitiveBooleanField.getBoolean(a)));
        assertThat(b, Is.is(true));
    }

    @Test
    public void testGetChar() throws NoSuchFieldException, IllegalAccessException {
        //GIVEN

        //WHEN
        A a = new A();
        a.publicPrimitiveCharField = 'c';
        char b = rnrPublicPrimitiveCharField.getChar(a);

        //THEN
        assertThat(b, Is.is(trueReflectionFieldPublicPrimitiveCharField.getChar(a)));
        assertThat(b, Is.is(a.publicPrimitiveCharField));
    }

    @Test
    public void testSetChar() throws NoSuchFieldException, IllegalAccessException {
        //GIVEN

        //WHEN
        A a = new A();
        rnrPublicPrimitiveCharField.setChar(a, 'c');
        char b = a.publicPrimitiveCharField;

        //THEN
        assertThat(b, Is.is(trueReflectionFieldPublicPrimitiveCharField.getChar(a)));
        assertThat(b, Is.is('c'));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetInteger_whenUsingPrimitveGetter() throws NoSuchFieldException, IllegalAccessException {
        //GIVEN

        //WHEN
        A a = new A();
        rnrPublicWrapperIntegerField.getInt(a);

        //THEN
        fail("Should throw an exception");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetInteger_whenUsingPrimitveSetter() throws NoSuchFieldException, IllegalAccessException {
        //GIVEN

        //WHEN
        A a = new A();
        rnrPublicWrapperIntegerField.setInt(a, 5);

        //THEN
        fail("Should throw an exception");
    }

    @Test
    public void testGetInteger_whenUsingObjectGetter() throws NoSuchFieldException, IllegalAccessException {
        //GIVEN

        //WHEN
        A a = new A();
        a.publicWrapperIntegerField = 5;
        Integer b = (Integer) rnrPublicWrapperIntegerField.get(a);

        //THEN
        assertThat(b, Is.is(trueReflectionFieldPublicWrapperIntegerField.get(a)));
        assertThat(b, Is.is(a.publicWrapperIntegerField));
    }

    @Test
    public void testSetInteger_whenUsingObjectSetter() throws NoSuchFieldException, IllegalAccessException {
        //GIVEN

        //WHEN
        A a = new A();
        rnrPublicWrapperIntegerField.set(a, 5);
        Integer b = a.publicWrapperIntegerField;

        //THEN
        assertThat(b, Is.is(trueReflectionFieldPublicWrapperIntegerField.get(a)));
        assertThat(b, Is.is(5));
    }
}
