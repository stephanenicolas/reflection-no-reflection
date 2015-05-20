package org.reflection_no_reflection.processor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedOptions;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeMirror;
import org.reflection_no_reflection.Annotation;
import org.reflection_no_reflection.Class;
import org.reflection_no_reflection.Field;
import org.reflection_no_reflection.Method;

/**
 * An annotation processor that detects classes that need to receive injections.
 * It is a {@link AbstractProcessor} that can be triggered for all kinds of annotations.
 * It will create a RNR database of annotated fields, methods and constuctors.
 *
 * @author SNI
 */
//TODO MUST REMOVE THIS. ANNOTATION TYPES SHOULD BE DYNAMIC
//getSupportedAnnotationType must be triggered
//chances are that the processor must be called in a different way by the gradle
//plugin. We have to get full control over annotation processor instance creation.
@SupportedOptions({"guiceAnnotationDatabasePackageName", "guiceUsesFragmentUtil", "guiceCommentsInjector", "annotatedClasses"})
public class Processor extends AbstractProcessor {

    private boolean isUsingFragmentUtil = true;
    private boolean isCommentingInjector = true;
    private Set<String> annotatedClasses = new HashSet<String>();
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
    private HashSet<Class> annotatedClassSet = new HashSet<>();

    /** Contains all classes that can be injected into a class with injection points. */
    private HashSet<String> bindableClasses;
    /** Name of the package to generate the annotation database into. */
    private String annotationDatabasePackageName;
    private ProcessingEnvironment processingEnv;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        this.processingEnv = processingEnv;
        annotationDatabasePackageName = processingEnv.getOptions().get("guiceAnnotationDatabasePackageName");
        mapAnnotationToMapClassContainingInjectionToInjectedFieldSet = new HashMap<>();
        mapAnnotationToMapClassContainingInjectionToInjectedMethodSet = new HashMap<>();
        mapAnnotationToMapClassContainingInjectionToInjectedConstructorsSet = new HashMap<>();
        bindableClasses = new HashSet<>();
        String isUsingFragmentUtilString = processingEnv.getOptions().get("guiceUsesFragmentUtil");
        if (isUsingFragmentUtilString != null) {
            isUsingFragmentUtil = Boolean.parseBoolean(isUsingFragmentUtilString);
        }
        String isCommentingInjectorString = processingEnv.getOptions().get("guiceCommentsInjector");
        if (isCommentingInjectorString != null) {
            isCommentingInjector = Boolean.parseBoolean(isCommentingInjectorString);
        }
        String annotatedClassesString = processingEnv.getOptions().get("annotatedClasses");
        if (annotatedClassesString != null) {
            annotatedClasses.addAll(Arrays.asList(annotatedClassesString.split(",")));
        }
        Class.purgeAllClasses();
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
            final Set<String> classNames = entryAnnotationToclassesContainingInjectionPoints.keySet();
            for (String className : classNames) {
                annotatedClassSet.add(new Class(className));
            }
        }

        for (Map<String, Set<String>> entryAnnotationToclassesContainingInjectionPoints : mapAnnotationToMapClassContainingInjectionToInjectedMethodSet.values()) {
            final Set<String> classNames = entryAnnotationToclassesContainingInjectionPoints.keySet();
            for (String className : classNames) {
                annotatedClassSet.add(new Class(className));
            }
        }

        for (Map<String, Set<String>> entryAnnotationToclassesContainingInjectionPoints : mapAnnotationToMapClassContainingInjectionToInjectedConstructorsSet.values()) {
            final Set<String> classNames = entryAnnotationToclassesContainingInjectionPoints.keySet();
            for (String className : classNames) {
                annotatedClassSet.add(new Class(className));
            }
        }

        return true;
    }

    private void addClassToAnnotationDatabase(Element injectionPoint) {
        TypeElement typeElementRequiringScanning = (TypeElement) injectionPoint;
        String typeElementName = getTypeName(typeElementRequiringScanning);
        //System.out.printf("Type: %s, is injected\n",typeElementName);
        annotatedClassSet.add(new Class(typeElementName));
    }

    private void addFieldToAnnotationDatabase(String annotationClassName, Element injectionPoint) {
        String injectionPointName;
        String injectedClassName = getTypeName(injectionPoint);
        if (isPrimitiveType(injectedClassName)) {
            bindableClasses.add(injectedClassName + ".class");
        } else {
            bindableClasses.add(injectedClassName);
        }
        injectionPointName = injectionPoint.getSimpleName().toString();

        TypeElement typeElementRequiringScanning = (TypeElement) injectionPoint.getEnclosingElement();
        String typeElementName = getTypeName(typeElementRequiringScanning);

        //System.out.printf("Type: %s, injection: %s \n",typeElementName, injectionPointName);
        addToInjectedFields(annotationClassName, typeElementName, injectionPointName, injectionPoint.getModifiers(), injectedClassName, injectionPoint.getAnnotationMirrors());
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

        TypeElement typeElementRequiringScanning = (TypeElement) injectionPoint.getEnclosingElement().getEnclosingElement();
        String typeElementName = getTypeName(typeElementRequiringScanning);
        //System.out.printf("Type: %s, injection: %s \n",typeElementName, injectionPointName);
        if (injectionPointName.startsWith("<init>")) {
            addToInjectedConstructors(annotationClassName, typeElementName, injectionPointName);
            //TODO add constructor
        } else {
            addToInjectedMethods(annotationClassName, typeElementName, injectionPointName);
            //RnR 2
            try {
                Method method = new Method(getClass(typeElementName),
                                           injectionPointName,
                                           //TODO : param types
                                           new Class[0],
                                           getClass(((ExecutableElement) injectionPoint).getReturnType().toString()),
                                           //TODO : exception types
                                           new Class[0],
                                           convertModifiersFromAnnnotationProcessing(injectionPoint.getModifiers()));
                Class.forName(typeElementName).addMethod(method);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
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
            //TODO add constructor
        } else {
            addToInjectedMethods(annotationClassName, typeElementName, injectionPointName);
            //RnR 2
            try {
                Method method = new Method(getClass(typeElementName),
                                           injectionPointName,
                                           //TODO : param types
                                           new Class[0],
                                           getClass(((ExecutableElement) injectionPoint).getReturnType().toString()),
                                           //TODO : exception types
                                           new Class[0],
                                           convertModifiersFromAnnnotationProcessing(injectionPoint.getModifiers()));

                Map<Class, Annotation> mapAnnotationClassToAnnotationInstance = new HashMap<>();
                Map<String, Object> mapMethodToValue = new HashMap<>();
                Map<String, Method> mapMethodNameToMethod = new HashMap<>();

                for (AnnotationMirror annotationMirror : injectionPoint.getAnnotationMirrors()) {
                    String annotationType = annotationMirror.getAnnotationType().toString();
                    for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : annotationMirror.getElementValues().entrySet()) {
                        String methodName = entry.getKey().getSimpleName().toString();
                        mapMethodToValue.put(methodName, entry.getValue().getValue());

                        //RnR 2
                        Method methodOfAnnotation = new Method(getClass(annotationClassName),
                                                               methodName,
                                                               //TODO : param types
                                                               new Class[0],
                                                               getClass(entry.getKey().getReturnType().toString()),
                                                               //TODO : exception types
                                                               new Class[0],
                                                               java.lang.reflect.Modifier.PUBLIC
                        );
                        mapMethodNameToMethod.put(methodName, methodOfAnnotation);
                    }

                    Annotation annotation = new Annotation(getClass(annotationType), mapMethodToValue, mapMethodNameToMethod);
                    mapAnnotationClassToAnnotationInstance.put(getClass(annotationType), annotation);
                }

                method.setDeclaredAnnotations(mapAnnotationClassToAnnotationInstance);
                Class.forName(typeElementName).addMethod(method);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    protected void addToInjectedConstructors(String annotationClassName, String typeElementName, String injectionPointName) {
        addToInjectedMembers(annotationClassName, typeElementName, injectionPointName, mapAnnotationToMapClassContainingInjectionToInjectedConstructorsSet);
    }

    protected void addToInjectedMethods(String annotationClassName, String typeElementName, String injectionPointName) {
        addToInjectedMembers(annotationClassName, typeElementName, injectionPointName, mapAnnotationToMapClassContainingInjectionToInjectedMethodSet);

    }

    protected void addToInjectedFields(String annotationClassName, String typeElementName, String injectionPointName, Set<Modifier> modifiers, String injectedClassName, List<? extends AnnotationMirror> annotationMirrors) {
        Map<String, Set<Field>> mapClassWithInjectionNameToMemberSet = mapAnnotationToMapClassContainingInjectionToInjectedFieldSet
            .get(annotationClassName);
        if (mapClassWithInjectionNameToMemberSet == null) {
            mapClassWithInjectionNameToMemberSet = new HashMap<>();
            mapAnnotationToMapClassContainingInjectionToInjectedFieldSet
                .put(annotationClassName, mapClassWithInjectionNameToMemberSet);
        }

        Set<Field> injectionPointNameSet = mapClassWithInjectionNameToMemberSet.get(typeElementName);
        if (injectionPointNameSet == null) {
            injectionPointNameSet = new HashSet<>();
            mapClassWithInjectionNameToMemberSet.put(typeElementName, injectionPointNameSet);
        }

        //TODO add that code here to add a method !
        List<org.reflection_no_reflection.Annotation> annotationList = new ArrayList<>();
        for (AnnotationMirror annotationMirror : annotationMirrors) {
            Map<String, Object> mapMethodToValue = new HashMap<>();
            Map<String, Method> mapMethodNameToMethod = new HashMap<>();

            for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : annotationMirror.getElementValues().entrySet()) {
                String methodName = entry.getKey().getSimpleName().toString();
                mapMethodToValue.put(methodName, entry.getValue().getValue());

                //RnR 2
                Method method = new Method(getClass(annotationClassName),
                                           methodName,
                                           //TODO : param types
                                           new Class[0],
                                           getClass(entry.getKey().getReturnType().toString()),
                                           //TODO : exception types
                                           new Class[0],
                                           java.lang.reflect.Modifier.PUBLIC
                );
                mapMethodNameToMethod.put(methodName, method);
            }
            final Class annotationType = getClass(annotationMirror.getAnnotationType().toString());
            org.reflection_no_reflection.Annotation annotationInstance = new Annotation(annotationType, mapMethodToValue, mapMethodNameToMethod);
            annotationList.add(annotationInstance);
            mapAnnotationNameToAnnotation.put(annotationInstance.getAnnotationTypeName(), annotationInstance);
        }
        int modifiersInt = convertModifiersFromAnnnotationProcessing(modifiers);
        final Field field = new Field(injectionPointName, getClass(injectedClassName), getClass(typeElementName), modifiersInt, annotationList);
        injectionPointNameSet.add(field);

        //rnr 2
        try {
            Class.forName(typeElementName).addField(field);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private Class getClass(String name) {
        try {
            return Class.forName(name);
        } catch (ClassNotFoundException e) {
            return new Class(name);
        }
    }

    private int convertModifiersFromAnnnotationProcessing(Set<Modifier> modifiers) {
        int result = 0;
        for (Modifier modifier : modifiers) {
            switch (modifier) {
                case ABSTRACT:
                    result |= java.lang.reflect.Modifier.ABSTRACT;
                    break;
                case PUBLIC:
                    result |= java.lang.reflect.Modifier.PUBLIC;
                    break;
                case PRIVATE:
                    result |= java.lang.reflect.Modifier.PRIVATE;
                    break;
                case STATIC:
                    result |= java.lang.reflect.Modifier.STATIC;
                    break;
                case PROTECTED:
                    result |= java.lang.reflect.Modifier.PROTECTED;
                    break;
                default:
            }
        }
        return result;
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
            mapClassWithInjectionNameToMemberSet = new HashMap<>();
            mapAnnotationToMapClassWithInjectionNameToMembersSet.put(annotationClassName, mapClassWithInjectionNameToMemberSet);
        }

        Set<String> injectionPointNameSet = mapClassWithInjectionNameToMemberSet.get(typeElementName);
        if (injectionPointNameSet == null) {
            injectionPointNameSet = new HashSet<>();
            mapClassWithInjectionNameToMemberSet.put(typeElementName, injectionPointNameSet);
        }
        injectionPointNameSet.add(injectionPointName);
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        //http://stackoverflow.com/a/8188860/693752
        return SourceVersion.latest();
    }

    @Override public Set<String> getSupportedAnnotationTypes() {
        return annotatedClasses;
    }

    public void setAnnotatedClasses(Set<String> annotatedClasses) {
        this.annotatedClasses = annotatedClasses;
    }

    public Set<Class> getAnnotatedClasses() {
        return annotatedClassSet;
    }
}
