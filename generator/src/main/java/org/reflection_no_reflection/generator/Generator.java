package org.reflection_no_reflection.generator;

import com.squareup.javapoet.JavaFile;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;
import org.reflection_no_reflection.Class;
import org.reflection_no_reflection.processor.Processor;
import org.reflection_no_reflection.visit.ClassPoolVisitStrategy;

/**
 * An annotation processor sample that demonstrates how to use the RNR annotation processor.
 */
public class Generator extends AbstractProcessor {

    private Processor processor = new Processor();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        //comma separated list of injected classes
        processor.init(processingEnv);
        processor.setTargetAnnotatedClasses(new HashSet<>(Arrays.asList(javax.inject.Inject.class.getName())));
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        //TODO
        boolean processed = processor.process(annotations, roundEnv);

        if (!roundEnv.processingOver()) {
            return processed;
        }
        HashSet<Class> annotatedClassSet = new HashSet<>(processor.getAnnotatedClassSet());

        JavaRuntimeDumperClassPoolVisitor dumper = new JavaRuntimeDumperClassPoolVisitor();
        ClassPoolVisitStrategy visitor = new ClassPoolVisitStrategy();
        visitor.visit(annotatedClassSet, dumper);

        JavaFile javaFile = dumper.getJavaFile();
        String buffer = javaFile.toString();
        System.out.println("Dumping all collected data: \n");
        System.out.println(buffer);
        return processed;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return processor.getSupportedAnnotationTypes();
    }
}
