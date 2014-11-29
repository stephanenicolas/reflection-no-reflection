package org.reflection_no_reflection.annotation_processor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import org.reflection_no_reflection.Annotation;
import org.reflection_no_reflection.Field;
import org.reflection_no_reflection.no_reflection.NoReflectionField;

/**
 * An annotation processor that detects classes that need to receive injections.
 * It is a {@link AbstractProcessor} that is triggered for all the annotations
 * of both Guice and RoboGuice.
 *
 * @author MikeBurton
 * @author SNI
 */
@SupportedAnnotationTypes({"com.google.inject.Inject", "com.google.inject.Provides", "javax.inject.Inject", "roboguice.inject.InjectView", "roboguice.inject.InjectResource", "roboguice.inject.InjectPreference", "roboguice.inject.InjectExtra", "roboguice.inject.InjectFragment", "roboguice.event.Observes", "roboguice.inject.ContentView"})
@SupportedOptions({"guiceAnnotationDatabasePackageName", "guiceUsesFragmentUtil", "guiceCommentsInjector"})
public class ReflectionNoReflectionAnnotationProcessor extends AbstractProcessor {

    public static final String TEMPLATE_ANNOTATION_DATABASE_PATH = "templates/AnnotationDatabaseImpl.vm";

    private boolean isUsingFragmentUtil = true;
    private boolean isCommentingInjector = true;
    //TODO add a HashMap<String, Set<String>>

    /**
     * Maps each annotation name to an inner map.
     * The inner map maps classes (containing injection points) names to the list of injected field names.
     */
    private HashMap<String, Map<String, Set<Field>>> mapAnnotationToMapClassContainingInjectionToInjectedFieldSet;
    /**
     * Maps each annotation name to an inner map.
     * The inner map maps classes (containing injection points) names to the list of injected method names and parameters classes.
     */
    private HashMap<String, Map<String, Set<String>>> mapAnnotationToMapClassContainingInjectionToInjectedMethodSet;
    /**
     * Maps each annotation name to an inner map.
     * The inner map maps classes (containing injection points) names to the list of injected constructors parameters classes.
     */
    private HashMap<String, Map<String, Set<String>>> mapAnnotationToMapClassContainingInjectionToInjectedConstructorsSet;

    /**
     * Maps each annotation name to a list of Annotation.
     */
    private HashMap<String, Annotation> mapAnnotationNameToAnnotation = new HashMap<>();

    /** Contains all classes that contain injection points. */
    private HashSet<String> classesContainingInjectionPointsSet = new HashSet<String>();

    /** Contains all classes that can be injected into a class with injection points. */
    private HashSet<String> bindableClasses;
    /** Name of the package to generate the annotation database into. */
    private String annotationDatabasePackageName;
    private ProcessingEnvironment processingEnv;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        this.processingEnv = processingEnv;
        annotationDatabasePackageName = processingEnv.getOptions().get("guiceAnnotationDatabasePackageName");
        mapAnnotationToMapClassContainingInjectionToInjectedFieldSet = new HashMap<String, Map<String, Set<Field>>>();
        mapAnnotationToMapClassContainingInjectionToInjectedMethodSet = new HashMap<String, Map<String, Set<String>>>();
        mapAnnotationToMapClassContainingInjectionToInjectedConstructorsSet = new HashMap<String, Map<String, Set<String>>>();
        bindableClasses = new HashSet<String>();
        String isUsingFragmentUtilString = processingEnv.getOptions().get("guiceUsesFragmentUtil");
        if (isUsingFragmentUtilString != null) {
            isUsingFragmentUtil = Boolean.parseBoolean(isUsingFragmentUtilString);
        }
        String isCommentingInjectorString = processingEnv.getOptions().get("guiceCommentsInjector");
        if (isCommentingInjectorString != null) {
            isCommentingInjector = Boolean.parseBoolean(isCommentingInjectorString);
        }
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        // Not sure why, but sometimes we're getting called with an empty list of annotations.
        if (annotations.isEmpty()) {
            return true;
        }

        for (TypeElement annotation : annotations) {
            String annotationClassName = getTypeName(annotation);

            for (Element injectionPoint : roundEnv.getElementsAnnotatedWith(annotation)) {
                if (injectionPoint.getEnclosingElement() instanceof TypeElement && injectionPoint instanceof VariableElement) {
                    addFieldToAnnotationDatabase(annotationClassName, injectionPoint);
                } else if (injectionPoint.getEnclosingElement() instanceof ExecutableElement && injectionPoint instanceof VariableElement) {
                    addParameterToAnnotationDatabase(annotationClassName, injectionPoint);
                } else if (injectionPoint instanceof ExecutableElement) {
                    addMethodOrConstructorToAnnotationDatabase(annotationClassName, injectionPoint);
                } else if (injectionPoint instanceof TypeElement) {
                    addClassToAnnotationDatabase(injectionPoint);
                }
            }
        }

        for (Map<String, Set<Field>> entryAnnotationToclassesContainingInjectionPoints : mapAnnotationToMapClassContainingInjectionToInjectedFieldSet.values()) {
            classesContainingInjectionPointsSet.addAll(entryAnnotationToclassesContainingInjectionPoints.keySet());
        }

        for (Map<String, Set<String>> entryAnnotationToclassesContainingInjectionPoints : mapAnnotationToMapClassContainingInjectionToInjectedMethodSet.values()) {
            classesContainingInjectionPointsSet.addAll(entryAnnotationToclassesContainingInjectionPoints.keySet());
        }

        for (Map<String, Set<String>> entryAnnotationToclassesContainingInjectionPoints : mapAnnotationToMapClassContainingInjectionToInjectedConstructorsSet.values()) {
            classesContainingInjectionPointsSet.addAll(entryAnnotationToclassesContainingInjectionPoints.keySet());
        }

        JavaFileObject jfo;
        try {
            String className = "AnnotationDatabaseImpl";
            if (annotationDatabasePackageName != null && !annotationDatabasePackageName.isEmpty()) {
                className = annotationDatabasePackageName + '.' + className;
            }
            jfo = processingEnv.getFiler().createSourceFile(className);
            ReflectionNoReflectionAnnotationDatabaseGenerator annotationDatabaseGenerator = createAnnotationDatabaseGenerator();
            configure(annotationDatabaseGenerator);
            annotationDatabaseGenerator.generateAnnotationDatabase(jfo);
        } catch (IOException e) {
            e.printStackTrace();
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.getMessage());
        }

        return true;
    }

    protected void configure(ReflectionNoReflectionAnnotationDatabaseGenerator annotationDatabaseGenerator) {
        annotationDatabaseGenerator.setTemplatePath(TEMPLATE_ANNOTATION_DATABASE_PATH);
        annotationDatabaseGenerator.setPackageName(annotationDatabasePackageName);
        annotationDatabaseGenerator.setBindableClasses(bindableClasses);
        annotationDatabaseGenerator.setClassesContainingInjectionPointsSet(classesContainingInjectionPointsSet);
        annotationDatabaseGenerator.setMapAnnotationToMapClassWithInjectionNameToConstructorSet(mapAnnotationToMapClassContainingInjectionToInjectedConstructorsSet);
        annotationDatabaseGenerator.setMapAnnotationToMapClassWithInjectionNameToMethodSet(mapAnnotationToMapClassContainingInjectionToInjectedMethodSet);
        annotationDatabaseGenerator.setMapAnnotationToMapClassWithInjectionNameToFieldSet(mapAnnotationToMapClassContainingInjectionToInjectedFieldSet);
        annotationDatabaseGenerator.setMapAnnotationNameToAnnotation(mapAnnotationNameToAnnotation);
        annotationDatabaseGenerator.setUsingFragmentUtil(isUsingFragmentUtil);
        annotationDatabaseGenerator.setCommentingInjector(isCommentingInjector);
    }

    private void addClassToAnnotationDatabase(Element injectionPoint) {
        TypeElement typeElementRequiringScanning = (TypeElement) injectionPoint;
        String typeElementName = getTypeName(typeElementRequiringScanning);
        //System.out.printf("Type: %s, is injected\n",typeElementName);
        classesContainingInjectionPointsSet.add(typeElementName);
    }

    private void addFieldToAnnotationDatabase(String annotationClassName, Element injectionPoint) {
        String injectionPointName;
        String injectedClassName = getTypeName(injectionPoint);
        if (isPrimitiveType(injectedClassName)) {
            bindableClasses.add(injectedClassName + ".class");
        } else  {
            bindableClasses.add(injectedClassName);
        }
        injectionPointName = injectionPoint.getSimpleName().toString();

        TypeElement typeElementRequiringScanning = (TypeElement) injectionPoint.getEnclosingElement();
        String typeElementName = getTypeName(typeElementRequiringScanning);

        //System.out.printf("Type: %s, injection: %s \n",typeElementName, injectionPointName);
        addToInjectedFields(annotationClassName, typeElementName, injectionPointName, injectedClassName, injectionPoint.getAnnotationMirrors());
    }

    private boolean isPrimitiveType(String injectedClassName) {
        switch (injectedClassName) {
            case "byte":
            case "short":
            case "int":
            case "long":
            case "float":
            case "double":
            case "boolean":
            case "char":
                return true;
        }
        return false;
    }

    private void addParameterToAnnotationDatabase(String annotationClassName, Element injectionPoint) {
        Element enclosing = injectionPoint.getEnclosingElement();
        String injectionPointName = enclosing.getSimpleName().toString();
        for (VariableElement variable : ((ExecutableElement) enclosing).getParameters()) {
            String parameterTypeName = getTypeName(variable);
            bindableClasses.add(parameterTypeName);
            injectionPointName += ":" + parameterTypeName;
        }

        TypeElement typeElementRequiringScanning = (TypeElement) ((ExecutableElement) injectionPoint.getEnclosingElement()).getEnclosingElement();
        String typeElementName = getTypeName(typeElementRequiringScanning);
        //System.out.printf("Type: %s, injection: %s \n",typeElementName, injectionPointName);
        if (injectionPointName.startsWith("<init>")) {
            addToInjectedConstructors(annotationClassName, typeElementName, injectionPointName);
        } else {
            addToInjectedMethods(annotationClassName, typeElementName, injectionPointName);
        }
    }

    private void addMethodOrConstructorToAnnotationDatabase(String annotationClassName, Element injectionPoint) {
        String injectionPointName = injectionPoint.getSimpleName().toString();
        for (VariableElement variable : ((ExecutableElement) injectionPoint).getParameters()) {
            String parameterTypeName = getTypeName((TypeElement) ((DeclaredType) variable.asType()).asElement());
            bindableClasses.add(parameterTypeName);
            injectionPointName += ":" + parameterTypeName;
        }

        TypeElement typeElementRequiringScanning = (TypeElement) injectionPoint.getEnclosingElement();
        String typeElementName = getTypeName(typeElementRequiringScanning);

        //System.out.printf("Type: %s, injection: %s \n",typeElementName, injectionPointName);
        if (injectionPointName.startsWith("<init>")) {
            addToInjectedConstructors(annotationClassName, typeElementName, injectionPointName);
        } else {
            addToInjectedMethods(annotationClassName, typeElementName, injectionPointName);
        }
    }

    protected void addToInjectedConstructors(String annotationClassName, String typeElementName, String injectionPointName) {
        addToInjectedMembers(annotationClassName, typeElementName, injectionPointName, mapAnnotationToMapClassContainingInjectionToInjectedConstructorsSet);
    }

    protected void addToInjectedMethods(String annotationClassName, String typeElementName, String injectionPointName) {
        addToInjectedMembers(annotationClassName, typeElementName, injectionPointName, mapAnnotationToMapClassContainingInjectionToInjectedMethodSet);
    }

    protected void addToInjectedFields(String annotationClassName, String typeElementName, String injectionPointName, String injectedClassName, List<? extends AnnotationMirror> annotationMirrors) {
        Map<String, Set<Field>> mapClassWithInjectionNameToMemberSet = ((HashMap<String, Map<String, Set<Field>>>) mapAnnotationToMapClassContainingInjectionToInjectedFieldSet)
            .get(annotationClassName);
        if (mapClassWithInjectionNameToMemberSet == null) {
            mapClassWithInjectionNameToMemberSet = new HashMap<String, Set<Field>>();
            mapAnnotationToMapClassContainingInjectionToInjectedFieldSet
                .put(annotationClassName, mapClassWithInjectionNameToMemberSet);
        }

        Set<Field> injectionPointNameSet = mapClassWithInjectionNameToMemberSet.get(typeElementName);
        if (injectionPointNameSet == null) {
            injectionPointNameSet = new HashSet<Field>();
            mapClassWithInjectionNameToMemberSet.put(typeElementName, injectionPointNameSet);
        }

        List<org.reflection_no_reflection.Annotation> annotationList = new ArrayList<>();
        for (AnnotationMirror annotationMirror : annotationMirrors) {
            Map<String,Object> mapMethodToValue = new HashMap<>();
            Map<String,String> mapMethodToReturnType = new HashMap<>();

            for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : annotationMirror.getElementValues().entrySet()) {
                String methodName = entry.getKey().getSimpleName().toString();
                mapMethodToValue.put(methodName, entry.getValue().getValue());
                mapMethodToReturnType.put(methodName, entry.getKey().getReturnType().toString());
            }
            org.reflection_no_reflection.Annotation annotationInstance = new Annotation(annotationMirror.getAnnotationType().toString(), mapMethodToValue, mapMethodToReturnType);
            annotationList.add(annotationInstance);
            mapAnnotationNameToAnnotation.put(annotationInstance.getAnnotationTypeName(), annotationInstance);
        }
        injectionPointNameSet.add(new NoReflectionField(injectionPointName, typeElementName, injectedClassName, annotationList));
    }

    private String getTypeName(TypeElement typeElementRequiringScanning) {
        if (typeElementRequiringScanning.getEnclosingElement() instanceof TypeElement) {
            return getTypeName(typeElementRequiringScanning.getEnclosingElement()) + "$" + typeElementRequiringScanning.getSimpleName().toString();
        } else {
            return typeElementRequiringScanning.getQualifiedName().toString();
        }
    }

    private String getTypeName(Element injectionPoint) {
        String injectedClassName = null;
        final TypeMirror fieldTypeMirror = injectionPoint.asType();
        if (fieldTypeMirror instanceof DeclaredType) {
            injectedClassName = getTypeName((TypeElement) ((DeclaredType) fieldTypeMirror).asElement());
        } else if (fieldTypeMirror instanceof PrimitiveType) {
            injectedClassName = fieldTypeMirror.toString();
        }
        return injectedClassName;
    }

    private void addToInjectedMembers(String annotationClassName, String typeElementName, String injectionPointName, HashMap<String, Map<String, Set<String>>> mapAnnotationToMapClassWithInjectionNameToMembersSet) {
        Map<String, Set<String>> mapClassWithInjectionNameToMemberSet = mapAnnotationToMapClassWithInjectionNameToMembersSet.get(annotationClassName);
        if (mapClassWithInjectionNameToMemberSet == null) {
            mapClassWithInjectionNameToMemberSet = new HashMap<String, Set<String>>();
            mapAnnotationToMapClassWithInjectionNameToMembersSet.put(annotationClassName, mapClassWithInjectionNameToMemberSet);
        }

        Set<String> injectionPointNameSet = mapClassWithInjectionNameToMemberSet.get(typeElementName);
        if (injectionPointNameSet == null) {
            injectionPointNameSet = new HashSet<String>();
            mapClassWithInjectionNameToMemberSet.put(typeElementName, injectionPointNameSet);
        }
        injectionPointNameSet.add(injectionPointName);
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        //http://stackoverflow.com/a/8188860/693752
        return SourceVersion.latest();
    }

    protected ReflectionNoReflectionAnnotationDatabaseGenerator createAnnotationDatabaseGenerator() {
        return new ReflectionNoReflectionAnnotationDatabaseGenerator();
    }
}
