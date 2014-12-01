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

    private java.lang.reflect.Field trueReflectionFieldPrivateObjectField;
    private java.lang.reflect.Field trueReflectionFieldPrivatePrimitiveIntField;
    private java.lang.reflect.Field trueReflectionFieldPrivatePrimitiveByteField;
    private java.lang.reflect.Field trueReflectionFieldPrivatePrimitiveShortField;
    private java.lang.reflect.Field trueReflectionFieldPrivatePrimitiveLongField;
    private java.lang.reflect.Field trueReflectionFieldPrivatePrimitiveFloatField;
    private java.lang.reflect.Field trueReflectionFieldPrivatePrimitiveDoubleField;
    private java.lang.reflect.Field trueReflectionFieldPrivatePrimitiveBooleanField;
    private java.lang.reflect.Field trueReflectionFieldPrivatePrimitiveCharField;
    
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

    private Field rnrPrivateObjectField;
    private Field rnrPrivatePrimitiveIntField;
    private Field rnrPrivatePrimitiveByteField;
    private Field rnrPrivatePrimitiveShortField;
    private Field rnrPrivatePrimitiveLongField;
    private Field rnrPrivatePrimitiveFloatField;
    private Field rnrPrivatePrimitiveDoubleField;
    private Field rnrPrivatePrimitiveBooleanField;
    private Field rnrPrivatePrimitiveCharField;

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

        trueReflectionFieldPrivateObjectField = A.class.getDeclaredField("privateObjectField");
        trueReflectionFieldPrivateObjectField.setAccessible(true);
        trueReflectionFieldPrivatePrimitiveIntField = A.class.getDeclaredField("privatePrimitiveIntField");
        trueReflectionFieldPrivatePrimitiveIntField.setAccessible(true);
        trueReflectionFieldPrivatePrimitiveByteField = A.class.getDeclaredField("privatePrimitiveByteField");
        trueReflectionFieldPrivatePrimitiveByteField.setAccessible(true);
        trueReflectionFieldPrivatePrimitiveShortField = A.class.getDeclaredField("privatePrimitiveShortField");
        trueReflectionFieldPrivatePrimitiveShortField.setAccessible(true);
        trueReflectionFieldPrivatePrimitiveLongField = A.class.getDeclaredField("privatePrimitiveLongField");
        trueReflectionFieldPrivatePrimitiveLongField.setAccessible(true);
        trueReflectionFieldPrivatePrimitiveFloatField = A.class.getDeclaredField("privatePrimitiveFloatField");
        trueReflectionFieldPrivatePrimitiveFloatField.setAccessible(true);
        trueReflectionFieldPrivatePrimitiveDoubleField = A.class.getDeclaredField("privatePrimitiveDoubleField");
        trueReflectionFieldPrivatePrimitiveDoubleField.setAccessible(true);
        trueReflectionFieldPrivatePrimitiveBooleanField = A.class.getDeclaredField("privatePrimitiveBooleanField");
        trueReflectionFieldPrivatePrimitiveBooleanField.setAccessible(true);
        trueReflectionFieldPrivatePrimitiveCharField = A.class.getDeclaredField("privatePrimitiveCharField");
        trueReflectionFieldPrivatePrimitiveCharField.setAccessible(true);

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

        rnrPrivateObjectField = getField(A.class, "privateObjectField");
        rnrPrivatePrimitiveIntField = getField(A.class, "privatePrimitiveIntField");
        rnrPrivatePrimitiveByteField = getField(A.class, "privatePrimitiveByteField");
        rnrPrivatePrimitiveShortField = getField(A.class, "privatePrimitiveShortField");
        rnrPrivatePrimitiveLongField = getField(A.class, "privatePrimitiveLongField");
        rnrPrivatePrimitiveFloatField = getField(A.class, "privatePrimitiveFloatField");
        rnrPrivatePrimitiveDoubleField = getField(A.class, "privatePrimitiveDoubleField");
        rnrPrivatePrimitiveBooleanField = getField(A.class, "privatePrimitiveBooleanField");
        rnrPrivatePrimitiveCharField = getField(A.class, "privatePrimitiveCharField");
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
    public void testPublicGet() throws NoSuchFieldException, IllegalAccessException {
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
    public void testPublicSet() throws NoSuchFieldException, IllegalAccessException {
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
    public void testPublicGetInt() throws NoSuchFieldException, IllegalAccessException {
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
    public void testPublicSetInt() throws NoSuchFieldException, IllegalAccessException {
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
    public void testPublicGetByte() throws NoSuchFieldException, IllegalAccessException {
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
    public void testPublicSetByte() throws NoSuchFieldException, IllegalAccessException {
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
    public void testPublicGetShort() throws NoSuchFieldException, IllegalAccessException {
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
    public void testPublicSetShort() throws NoSuchFieldException, IllegalAccessException {
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
    public void testPublicGetLong() throws NoSuchFieldException, IllegalAccessException {
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
    public void testPublicSetLong() throws NoSuchFieldException, IllegalAccessException {
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
    public void testPublicGetFloat() throws NoSuchFieldException, IllegalAccessException {
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
    public void testPublicSetFloat() throws NoSuchFieldException, IllegalAccessException {
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
    public void testPublicGetDouble() throws NoSuchFieldException, IllegalAccessException {
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
    public void testPublicSetDouble() throws NoSuchFieldException, IllegalAccessException {
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
    public void testPublicGetBoolean() throws NoSuchFieldException, IllegalAccessException {
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
    public void testPublicSetBoolean() throws NoSuchFieldException, IllegalAccessException {
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
    public void testPublicGetChar() throws NoSuchFieldException, IllegalAccessException {
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
    public void testPublicSetChar() throws NoSuchFieldException, IllegalAccessException {
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

    @Test
    public void testPrivateGet() throws NoSuchFieldException, IllegalAccessException {
        //GIVEN

        //WHEN
        A a = new A();
        a.setPrivateObjectField(new B());
        B b = (B) rnrPrivateObjectField.get(a);

        //THEN
        assertThat(b, Is.<B>is((B) trueReflectionFieldPrivateObjectField.get(a)));
        assertThat(b, Is.<B>is(a.getPrivateObjectField()));
    }

    @Test
    public void testPrivateSet() throws NoSuchFieldException, IllegalAccessException {
        //GIVEN

        //WHEN
        B newB = new B();
        A a = new A();
        rnrPrivateObjectField.set(a, newB);

        //THEN
        assertThat(a.getPrivateObjectField(), Is.<B>is((B) trueReflectionFieldPrivateObjectField.get(a)));
        assertThat(a.getPrivateObjectField(), Is.<B>is(newB));
    }

    @Test
    public void testPrivateGetInt() throws NoSuchFieldException, IllegalAccessException {
        //GIVEN

        //WHEN
        A a = new A();
        a.setPrivatePrimitiveIntField(5);
        int b = rnrPrivatePrimitiveIntField.getInt(a);

        //THEN
        assertThat(b, Is.is(trueReflectionFieldPrivatePrimitiveIntField.getInt(a)));
        assertThat(b, Is.is(a.getPrivatePrimitiveIntField()));
    }

    @Test
    public void testPrivateSetInt() throws NoSuchFieldException, IllegalAccessException {
        //GIVEN

        //WHEN
        A a = new A();
        rnrPrivatePrimitiveIntField.setInt(a, 5);
        int b = a.getPrivatePrimitiveIntField();

        //THEN
        assertThat(b, Is.is(trueReflectionFieldPrivatePrimitiveIntField.getInt(a)));
        assertThat(b, Is.is(5));
    }

    @Test
    public void testPrivateGetByte() throws NoSuchFieldException, IllegalAccessException {
        //GIVEN

        //WHEN
        A a = new A();
        a.setPrivatePrimitiveByteField((byte)5);
        byte b = rnrPrivatePrimitiveByteField.getByte(a);

        //THEN
        assertThat(b, Is.is(trueReflectionFieldPrivatePrimitiveByteField.getByte(a)));
        assertThat(b, Is.is(a.getPrivatePrimitiveByteField()));
    }

    @Test
    public void testPrivateSetByte() throws NoSuchFieldException, IllegalAccessException {
        //GIVEN

        //WHEN
        A a = new A();
        rnrPrivatePrimitiveByteField.setByte(a, (byte) 5);
        byte b = a.getPrivatePrimitiveByteField();

        //THEN
        assertThat(b, Is.is(trueReflectionFieldPrivatePrimitiveByteField.getByte(a)));
        assertThat(b, Is.is( (byte) 5));
    }

    @Test
    public void testPrivateGetShort() throws NoSuchFieldException, IllegalAccessException {
        //GIVEN

        //WHEN
        A a = new A();
        a.setPrivatePrimitiveShortField((short)5);
        short b = rnrPrivatePrimitiveShortField.getShort(a);

        //THEN
        assertThat(b, Is.is(trueReflectionFieldPrivatePrimitiveShortField.getShort(a)));
        assertThat(b, Is.is(a.getPrivatePrimitiveShortField()));
    }

    @Test
    public void testPrivateSetShort() throws NoSuchFieldException, IllegalAccessException {
        //GIVEN

        //WHEN
        A a = new A();
        rnrPrivatePrimitiveShortField.setShort(a, (short) 5);
        short b = a.getPrivatePrimitiveShortField();

        //THEN
        assertThat(b, Is.is(trueReflectionFieldPrivatePrimitiveShortField.getShort(a)));
        assertThat(b, Is.is( (short) 5));
    }

    @Test
    public void testPrivateGetLong() throws NoSuchFieldException, IllegalAccessException {
        //GIVEN

        //WHEN
        A a = new A();
        a.setPrivatePrimitiveLongField(5);
        long b = rnrPrivatePrimitiveLongField.getLong(a);

        //THEN
        assertThat(b, Is.is(trueReflectionFieldPrivatePrimitiveLongField.getLong(a)));
        assertThat(b, Is.is(a.getPrivatePrimitiveLongField()));
    }

    @Test
    public void testPrivateSetLong() throws NoSuchFieldException, IllegalAccessException {
        //GIVEN

        //WHEN
        A a = new A();
        rnrPrivatePrimitiveLongField.setLong(a, (long) 5);
        long b = a.getPrivatePrimitiveLongField();

        //THEN
        assertThat(b, Is.is(trueReflectionFieldPrivatePrimitiveLongField.getLong(a)));
        assertThat(b, Is.is( (long) 5));
    }


    @Test
    public void testPrivateGetFloat() throws NoSuchFieldException, IllegalAccessException {
        //GIVEN

        //WHEN
        A a = new A();
        a.setPrivatePrimitiveFloatField(5);
        float b = rnrPrivatePrimitiveFloatField.getFloat(a);

        //THEN
        assertThat(b, Is.is(trueReflectionFieldPrivatePrimitiveFloatField.getFloat(a)));
        assertThat(b, Is.is(a.getPrivatePrimitiveFloatField()));
    }

    @Test
    public void testPrivateSetFloat() throws NoSuchFieldException, IllegalAccessException {
        //GIVEN

        //WHEN
        A a = new A();
        rnrPrivatePrimitiveFloatField.setFloat(a, (float) 5);
        float b = a.getPrivatePrimitiveFloatField();

        //THEN
        assertThat(b, Is.is(trueReflectionFieldPrivatePrimitiveFloatField.getFloat(a)));
        assertThat(b, Is.is( (float) 5));
    }

    @Test
    public void testPrivateGetDouble() throws NoSuchFieldException, IllegalAccessException {
        //GIVEN

        //WHEN
        A a = new A();
        a.setPrivatePrimitiveDoubleField(5);
        double b = rnrPrivatePrimitiveDoubleField.getDouble(a);

        //THEN
        assertThat(b, Is.is(trueReflectionFieldPrivatePrimitiveDoubleField.getDouble(a)));
        assertThat(b, Is.is(a.getPrivatePrimitiveDoubleField()));
    }

    @Test
    public void testPrivateSetDouble() throws NoSuchFieldException, IllegalAccessException {
        //GIVEN

        //WHEN
        A a = new A();
        rnrPrivatePrimitiveDoubleField.setDouble(a, (double) 5);
        double b = a.getPrivatePrimitiveDoubleField();

        //THEN
        assertThat(b, Is.is(trueReflectionFieldPrivatePrimitiveDoubleField.getDouble(a)));
        assertThat(b, Is.is( (double) 5));
    }

    @Test
    public void testPrivateGetBoolean() throws NoSuchFieldException, IllegalAccessException {
        //GIVEN

        //WHEN
        A a = new A();
        a.setPrivatePrimitiveBooleanField(true);
        boolean b = rnrPrivatePrimitiveBooleanField.getBoolean(a);

        //THEN
        assertThat(b, Is.is(trueReflectionFieldPrivatePrimitiveBooleanField.getBoolean(a)));
        assertThat(b, Is.is(a.isPrivatePrimitiveBooleanField()));
    }

    @Test
    public void testPrivateSetBoolean() throws NoSuchFieldException, IllegalAccessException {
        //GIVEN

        //WHEN
        A a = new A();
        rnrPrivatePrimitiveBooleanField.setBoolean(a, true);
        boolean b = a.isPrivatePrimitiveBooleanField();

        //THEN
        assertThat(b, Is.is(trueReflectionFieldPrivatePrimitiveBooleanField.getBoolean(a)));
        assertThat(b, Is.is(true));
    }

    @Test
    public void testPrivateGetChar() throws NoSuchFieldException, IllegalAccessException {
        //GIVEN

        //WHEN
        A a = new A();
        a.setPrivatePrimitiveCharField('c');
        char b = rnrPrivatePrimitiveCharField.getChar(a);

        //THEN
        assertThat(b, Is.is(trueReflectionFieldPrivatePrimitiveCharField.getChar(a)));
        assertThat(b, Is.is(a.getPrivatePrimitiveCharField()));
    }

    @Test
    public void testPrivateSetChar() throws NoSuchFieldException, IllegalAccessException {
        //GIVEN

        //WHEN
        A a = new A();
        rnrPrivatePrimitiveCharField.setChar(a, 'c');
        char b = a.getPrivatePrimitiveCharField();

        //THEN
        assertThat(b, Is.is(trueReflectionFieldPrivatePrimitiveCharField.getChar(a)));
        assertThat(b, Is.is('c'));
    }
}
