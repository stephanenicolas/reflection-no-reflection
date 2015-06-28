package org.reflection_no_reflection.processor;

import java.util.Arrays;
import java.util.HashSet;
import javax.lang.model.element.Modifier;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * @author SNI.
 */
public class ProcessorTest {

    Processor processor;

    @Before
    public void setup() {
        processor = new Processor();
    }

    @Test
    public void testSimpleModifiers() {
        assertThat(convert(Modifier.PUBLIC), is(java.lang.reflect.Modifier.PUBLIC));
        assertThat(convert(Modifier.PRIVATE), is(java.lang.reflect.Modifier.PRIVATE));
        assertThat(convert(Modifier.PROTECTED), is(java.lang.reflect.Modifier.PROTECTED));
        assertThat(convert(Modifier.FINAL), is(java.lang.reflect.Modifier.FINAL));
        assertThat(convert(Modifier.STATIC), is(java.lang.reflect.Modifier.STATIC));
        assertThat(convert(Modifier.VOLATILE), is(java.lang.reflect.Modifier.VOLATILE));
        assertThat(convert(Modifier.SYNCHRONIZED), is(java.lang.reflect.Modifier.SYNCHRONIZED));
        assertThat(convert(Modifier.ABSTRACT), is(java.lang.reflect.Modifier.ABSTRACT));
    }

    @Test
    public void testCompundModifiers() {
        assertThat(convert(Modifier.PUBLIC, Modifier.FINAL), is(java.lang.reflect.Modifier.FINAL + java.lang.reflect.Modifier.PUBLIC));
        assertThat(convert(Modifier.PROTECTED, Modifier.STATIC), is(java.lang.reflect.Modifier.STATIC + java.lang.reflect.Modifier.PROTECTED));
        assertThat(convert(Modifier.PRIVATE, Modifier.SYNCHRONIZED), is(java.lang.reflect.Modifier.PRIVATE + java.lang.reflect.Modifier.SYNCHRONIZED));
    }

    private int convert(Modifier... modifier) {
        return processor.convertModifiersFromAnnotationProcessing(new HashSet(Arrays.asList(modifier)));
    }
}
