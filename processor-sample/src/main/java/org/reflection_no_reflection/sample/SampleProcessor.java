package org.reflection_no_reflection.sample;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;
import org.reflection_no_reflection.Class;
import org.reflection_no_reflection.processor.Processor;
import org.reflection_no_reflection.visit.dump.JavaDumperClassPoolVisitor;

/**
 * An annotation processor sample that demonstrates how to use the RNR annotation processor.
 */
public class SampleProcessor extends AbstractProcessor {

    private Processor processor = new Processor();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        //comma separated list of injected classes
        processor.init(processingEnv);
        processor.setTargetAnnotatedClasses(new HashSet<>(Arrays.asList(javax.inject.Inject.class.getName())));
        processor.setMaxLevel(1);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        boolean processed = processor.process(annotations, roundEnv);

        if (!roundEnv.processingOver()) {
            return processed;
        }
        //HashSet<Class> annotatedClassSet = new HashSet<>(processor.getAnnotatedClassSet());

        JavaDumperClassPoolVisitor dumper = new JavaDumperClassPoolVisitor();
        Class.visit(dumper);
        final StringBuffer buffer = dumper.getBuffer();

        System.out.println("Dumping all collected data: \n");
        System.out.println(buffer);
        return processed;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return processor.getSupportedAnnotationTypes();
    }
}
