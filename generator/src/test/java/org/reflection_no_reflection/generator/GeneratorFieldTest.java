package org.reflection_no_reflection.generator;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.junit.Before;
import org.reflection_no_reflection.*;
import org.reflection_no_reflection.Class;
import org.reflection_no_reflection.processor.FieldTest;
import org.reflection_no_reflection.processor.Processor;
import org.reflection_no_reflection.runtime.Module;

/**
 * @author SNI.
 */
public class GeneratorFieldTest extends FieldTest {

    @Before
    public void setup() {
        processor = new Generator();
        javaSourceCode = null;
    }

    protected void configureProcessor(String... annotations) {
        ((Generator)processor).setTargetAnnotatedClasses(new HashSet<>(Arrays.asList(annotations)));
    }

    @Override
    protected Set<Class> getProcessedClasses() {
        try {
            Class.loadModule((Module) java.lang.Class.forName("org.reflection_no_reflection.generator.gen.ModuleImpl.class").newInstance());
            return Class.getClassPool();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
