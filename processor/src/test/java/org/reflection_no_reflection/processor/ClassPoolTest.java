package org.reflection_no_reflection.processor;

import org.junit.Before;
import org.junit.Test;
import org.reflection_no_reflection.Class;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * @author SNI.
 */
public class ClassPoolTest {

    @Before
    public void setup() {
        Class.clearAllClasses();
    }

    @Test
    public void classesAreSingletons() throws ClassNotFoundException {
        assertThat((Class) Class.forNameSafe("foo"), is((Class) Class.forName("foo")));
    }

    @Test(expected = ClassNotFoundException.class)
    public void classNotFound() throws ClassNotFoundException {
        Class.forName("foo");
    }
}
