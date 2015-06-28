package org.reflection_no_reflection.processor;

import com.google.common.base.Joiner;
import com.google.testing.compile.JavaFileObjects;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.tools.JavaFileObject;
import org.junit.Before;
import org.reflection_no_reflection.*;
import org.reflection_no_reflection.Class;

import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;
import static org.truth0.Truth.ASSERT;

public class AbstractRnRTest {

    protected AbstractProcessor processor;
    protected JavaFileObject javaSourceCode;

    @Before
    public void setup() {
        processor = new Processor();
        javaSourceCode = null;
    }

    protected void javaSourceCode(String fullyQualifiedName, String... source) {
        javaSourceCode = JavaFileObjects.forSourceString(fullyQualifiedName, Joiner.on('\n').join(source));
    }

    protected void setTargetAnnotations(String... annotations) {
        ((Processor)processor).setTargetAnnotatedClasses(new HashSet<>(Arrays.asList(annotations)));
    }

    protected void setMaxLevel(int maxLevel) {
        ((Processor)processor).setMaxLevel(maxLevel);
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

    protected Set<Class> getProcessedClasses() {
        return ((Processor)processor).getAnnotatedClassSet();
    }
}
