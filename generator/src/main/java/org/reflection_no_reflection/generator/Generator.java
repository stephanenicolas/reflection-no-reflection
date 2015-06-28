package org.reflection_no_reflection.generator;

import com.squareup.javapoet.JavaFile;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedOptions;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import org.reflection_no_reflection.Class;
import org.reflection_no_reflection.generator.annotation.AnnotationCreatorClassPoolVisitor;
import org.reflection_no_reflection.generator.introspector.IntrospectorDumperClassPoolVisitor;
import org.reflection_no_reflection.generator.module.ModuleDumperClassPoolVisitor;
import org.reflection_no_reflection.processor.Processor;
import org.reflection_no_reflection.visit.ClassPoolVisitStrategy;

/**
 * An annotation processor sample that demonstrates how to use the RNR annotation processor.
 */
@SupportedOptions({"targetAnnotatedClasses", "maxLevel","targetPackageName",
    "introspector.includes","introspector.excludes"})
public class Generator extends AbstractProcessor {

    private String targetPackageName = null;
    private Processor processor = new Processor();
    private ProcessingEnvironment processingEnv;

    private List<Pattern> introspectorIncludes;
    private List<Pattern> introspectorExcludes;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        this.processingEnv = processingEnv;
        //comma separated list of injected classes
        String annotatedClassesString = processingEnv.getOptions().get("targetAnnotatedClasses");
        if (annotatedClassesString != null) {
            processor.setTargetAnnotatedClasses(new HashSet<>(Arrays.asList(annotatedClassesString.split(","))));
        }

        String maxLevelString = processingEnv.getOptions().get("maxLevel");
        if (maxLevelString != null) {
            processor.setMaxLevel(Integer.parseInt(maxLevelString));
        }

        //introspector includes and excludes
        setIntrospectorIncludes(createPatterns(processingEnv, "introspector.includes"));
        setIntrospectorExcludes(createPatterns(processingEnv, "introspector.excludes"));

        String targetPackageNameString = processingEnv.getOptions().get("targetPackageName");
        if (targetPackageNameString != null) {
            targetPackageName = targetPackageNameString;
        }

        Class.clearAllClasses();

        processor.init(processingEnv);
        System.out.println("RNR Generator created.");
    }

    private List<Pattern> createPatterns(ProcessingEnvironment processingEnv, String option) {
        String patternString = processingEnv.getOptions().get(option);
        List<Pattern> patternList = new ArrayList<>();
        if (patternString != null) {
            for (String pattern : patternString.split(",")) {
                patternList.add(Pattern.compile(pattern));
            }
        }
        return patternList;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        boolean processed = processor.process(annotations, roundEnv);

        if (annotations.isEmpty() || roundEnv.processingOver()) {
            //module creation
            Set<Class> annotatedClassSet = Class.getClassPool();
            IntrospectorDumperClassPoolVisitor reflectorsDumper = new IntrospectorDumperClassPoolVisitor();
            reflectorsDumper.setIncludes(introspectorIncludes);
            reflectorsDumper.setExcludes(introspectorExcludes);
            ModuleDumperClassPoolVisitor moduleDumper = new ModuleDumperClassPoolVisitor(reflectorsDumper);
            moduleDumper.setTargetPackageName(targetPackageName);
            moduleDumper.getMapAnnotationTypeToClassContainingAnnotation().putAll(processor.getMapAnnotationTypeToClassContainingAnnotation());
            JavaFile rnRModuleJavaFile = createRnRModuleJavaFile(annotatedClassSet, moduleDumper);
            System.out.println("Dumping all collected data: \n");
            printJavaFile(rnRModuleJavaFile);
            writeJavaFile(rnRModuleJavaFile);

            //reflectors creation
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

    public void setIntrospectorIncludes(List<Pattern> introspectorIncludes) {
        this.introspectorIncludes = introspectorIncludes;
    }

    public void setIntrospectorExcludes(List<Pattern> introspectorExcludes) {
        this.introspectorExcludes = introspectorExcludes;
    }


}
