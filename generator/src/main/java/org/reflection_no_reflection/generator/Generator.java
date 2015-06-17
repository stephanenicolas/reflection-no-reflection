package org.reflection_no_reflection.generator;

import com.squareup.javapoet.JavaFile;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import org.reflection_no_reflection.Annotation;
import org.reflection_no_reflection.Class;
import org.reflection_no_reflection.generator.annotation.AnnotationCreatorClassPoolVisitor;
import org.reflection_no_reflection.generator.introspector.IntrospectorDumperClassPoolVisitor;
import org.reflection_no_reflection.generator.module.ModuleDumperClassPoolVisitor;
import org.reflection_no_reflection.processor.Processor;
import org.reflection_no_reflection.visit.ClassPoolVisitStrategy;

/**
 * An annotation processor sample that demonstrates how to use the RNR annotation processor.
 */
public class Generator extends AbstractProcessor {

    private final String targetPackageName = "org.reflection_no_reflection.generator.sample.gen";
    private Processor processor = new Processor();
    private ProcessingEnvironment processingEnv;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        this.processingEnv = processingEnv;
        //comma separated list of injected classes
        processor.init(processingEnv);
        processor.setTargetAnnotatedClasses(new HashSet<>(Arrays.asList(javax.inject.Inject.class.getName())));
        System.out.println("RNR Generator created.");
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        boolean processed = processor.process(annotations, roundEnv);

        if (annotations.isEmpty() || roundEnv.processingOver()) {
            //module creation
            Set<Class> annotatedClassSet = Class.getClassPool();
            ModuleDumperClassPoolVisitor moduleDumper = new ModuleDumperClassPoolVisitor();
            moduleDumper.setTargetPackageName(targetPackageName);
            moduleDumper.getMapAnnotationTypeToClassContainingAnnotation().putAll(processor.getMapAnnotationTypeToClassContainingAnnotation());
            JavaFile rnRModuleJavaFile = createRnRModuleJavaFile(annotatedClassSet, moduleDumper);
            System.out.println("Dumping all collected data: \n");
            printJavaFile(rnRModuleJavaFile);
            writeJavaFile(rnRModuleJavaFile);

            //reflectors creation
            IntrospectorDumperClassPoolVisitor reflectorsDumper = new IntrospectorDumperClassPoolVisitor();
            ClassPoolVisitStrategy visitor = new ClassPoolVisitStrategy();
            visitor.visit(annotatedClassSet, reflectorsDumper);

            for (JavaFile javaFile : reflectorsDumper.getJavaFiles()) {
                System.out.println("Dumping reflector: \n");
                printJavaFile(javaFile);
                writeJavaFile(javaFile);
            }

            //annotation implementations creation
            AnnotationCreatorClassPoolVisitor annotationDumper = new AnnotationCreatorClassPoolVisitor();
            annotationDumper.setTargetPackageName(targetPackageName);
            ClassPoolVisitStrategy visitor2 = new ClassPoolVisitStrategy();
            final Set<Class> annotationClasses = processor.getAnnotationClasses();
            visitor2.visit(annotationClasses, annotationDumper);

            for (JavaFile javaFile : annotationDumper.getJavaFiles()) {
                writeJavaFile(javaFile);
                System.out.println("Dumping Annotations implementations: \n");
                printJavaFile(javaFile);
            }
        }
        return processed;
    }

    private void printJavaFile(JavaFile javaFile) {
        String buffer = javaFile.toString();
        System.out.println(buffer);
    }

    private void writeJavaFile(JavaFile javaFile) {
        try {
            javaFile.writeTo(processingEnv.getFiler());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private JavaFile createRnRModuleJavaFile(Set<Class> annotatedClassSet, ModuleDumperClassPoolVisitor dumper) {
        ClassPoolVisitStrategy visitor = new ClassPoolVisitStrategy();
        visitor.visit(annotatedClassSet, dumper);
        return dumper.getJavaFile();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return processor.getSupportedAnnotationTypes();
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return processor.getSupportedSourceVersion();
    }

    @Override
    public Set<String> getSupportedOptions() {
        return processor.getSupportedOptions();
    }

    public void setTargetAnnotatedClasses(Set<String> targetAnnotatedClasses) {
        processor.setTargetAnnotatedClasses(targetAnnotatedClasses);
    }

}
