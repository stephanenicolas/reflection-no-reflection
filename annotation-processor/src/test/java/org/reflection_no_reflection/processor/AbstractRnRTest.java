package org.reflection_no_reflection.processor;

import com.google.common.base.Joiner;
import com.google.testing.compile.JavaFileObjects;
import java.util.Arrays;
import java.util.HashSet;
import javax.tools.JavaFileObject;
import org.junit.Before;

import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;
import static org.truth0.Truth.ASSERT;

public class AbstractRnRTest {

    protected Processor processor;
    protected JavaFileObject javaSourceCode;

    @Before
    public void setup() {
        processor = new Processor();
        javaSourceCode = null;
    }

    protected void javaSourceCode(String fullyQualifiedName, String... source) {
        javaSourceCode = JavaFileObjects.forSourceString(fullyQualifiedName, Joiner.on('\n').join(source));
    }

    protected void configureProcessor(String... annotations) {
        processor.setAnnotatedClasses(new HashSet<>(Arrays.asList(annotations)));
    }

    protected Iterable<? extends javax.annotation.processing.Processor> rnrProcessors() {
        return Arrays.asList(processor);
    }

    protected void assertJavaSourceCompileWithoutError() {
        ASSERT.about(javaSource())
            .that(javaSourceCode)
            .processedWith(rnrProcessors())
            .compilesWithoutError();
    }
}
