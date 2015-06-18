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
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeMirror;
import org.reflection_no_reflection.Annotation;
import org.reflection_no_reflection.Class;
import org.reflection_no_reflection.Constructor;
import org.reflection_no_reflection.Field;
import org.reflection_no_reflection.GenericDeclaration;
import org.reflection_no_reflection.Method;
import org.reflection_no_reflection.TypeVariable;
import org.reflection_no_reflection.TypeVariableImpl;

/**
 * An annotation processor that detects classes that need to receive injections.
 * It is a {@link AbstractProcessor} that can be triggered for all kinds of annotations.
 * It will create a RNR database of annotated fields, methods and constuctors.
 *
 * @author SNI
 */
@SupportedOptions({"targetAnnotatedClasses"})
public class Processor extends AbstractProcessor {

    private Set<String> targetAnnotatedClasses = new HashSet<>();

    /** Contains all classes that contain annotations. */
    private HashSet<Class> annotatedClassSet = new HashSet<>();
    /** Maps annotation type to classes that contain this annotation. */
    private Map<Class<? extends Annotation>, Set<Class<?>>> mapAnnotationTypeToClassContainingAnnotation = new HashMap<>();

    private int maxLevel = 0;
    private Set<Class> annotationClasses = new HashSet<>();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        String annotatedClassesString = processingEnv.getOptions().get("annotatedClasses");
        if (annotatedClassesString != null) {
            targetAnnotatedClasses.addAll(Arrays.asList(annotatedClassesString.split(",")));
        }
        Class.clearAllClasses();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        // Not sure why, but sometimes we're getting called with an empty list of annotations.
        if (annotations.isEmpty() || roundEnv.processingOver()) {
            return true;
        }

        int level = 0;
        for (TypeElement annotation : annotations) {
            for (Element element : roundEnv.getElementsAnnotatedWith(annotation)) {
                mapElementToReflection(element, level);
            }
        }

        return true;
    }

    private void mapElementToReflection(Element element, int level) {
        if (element.getEnclosingElement() instanceof TypeElement && element instanceof VariableElement) {
            addFieldToAnnotationDatabase(element, level);
        } else if (element.getEnclosingElement() instanceof ExecutableElement && element instanceof VariableElement) {
            addParameterToAnnotationDatabase(element, level);
        } else if (element instanceof ExecutableElement) {
            addMethodOrConstructorToAnnotationDatabase((ExecutableElement) element, level);
        } else if (element instanceof TypeElement) {
            addClassToAnnotationDatabase(element, level);
        }
    }

    private void addClassToAnnotationDatabase(Element classElement, int level) {
        Class newClass = createClass(classElement.asType(), level);
        annotatedClassSet.add(newClass);
        final List<Annotation> annotations = extractAnnotations(classElement, level);
        newClass.setAnnotations(annotations);
    }

    private void addFieldToAnnotationDatabase(Element fieldElement, int level) {
        Class fieldClass;
        //System.out.printf("Type: %s, injection: %s \n",typeElementName, fieldName);
        fieldClass = createClass(fieldElement.asType(), level);

        final Set<Modifier> modifiers = fieldElement.getModifiers();
        String fieldName = fieldElement.getSimpleName().toString();
        TypeElement declaringClassElement = (TypeElement) fieldElement.getEnclosingElement();
        String declaringClassName = declaringClassElement.getQualifiedName().toString();
        final List<Annotation> annotations = extractAnnotations(fieldElement, level);
        int modifiersInt = convertModifiersFromAnnnotationProcessing(modifiers);
        final Class<?> enclosingClass = Class.forNameSafe(declaringClassName, level + 1);
        if (level == 0) {
            final Class<? extends Annotation> annotationType = annotations.get(0).annotationType();
            Set<Class<?>> classes = mapAnnotationTypeToClassContainingAnnotation.get(annotationType);
            if (classes == null) {
                classes = new HashSet<>();
            }
            classes.add(enclosingClass);
            mapAnnotationTypeToClassContainingAnnotation.put(annotationType, classes);
        }
        final Field field = new Field(fieldName, fieldClass, enclosingClass, modifiersInt, annotations);
        enclosingClass.addField(field);
        annotatedClassSet.add(enclosingClass);
    }

    private void addParameterToAnnotationDatabase(Element paramElement, int level) {
        Element enclosing = paramElement.getEnclosingElement();
        String methodName = enclosing.getSimpleName().toString();
        //System.out.printf("Type: %s, injection: %s \n",typeElementName, methodName);
        final ExecutableElement methodOrConstructor = (ExecutableElement) paramElement.getEnclosingElement();
        if (methodName.startsWith("<init>")) {
            addConstructor(methodOrConstructor, level);
        } else {
            addMethod(methodOrConstructor, level);
        }
    }

    private Class[] getParameterTypes(ExecutableElement methodElement, int level) {
        final List<? extends VariableElement> parameters = methodElement.getParameters();
        Class[] paramTypes = new Class[parameters.size()];
        for (int indexParam = 0; indexParam < parameters.size(); indexParam++) {
            VariableElement parameter = parameters.get(indexParam);
            paramTypes[indexParam] = createClass(parameter.asType(), level);
        }
        return paramTypes;
    }

    private Class[] getExceptionTypes(ExecutableElement methodElement, int level) {
        final List<? extends TypeMirror> exceptionTypes = methodElement.getThrownTypes();
        Class[] paramTypes = new Class[exceptionTypes.size()];
        for (int indexParam = 0; indexParam < exceptionTypes.size(); indexParam++) {
            TypeMirror exceptionType = exceptionTypes.get(indexParam);
            paramTypes[indexParam] = createClass(exceptionType, level);
        }
        return paramTypes;
    }

    private void addMethodOrConstructorToAnnotationDatabase(ExecutableElement methodOrConstructorElement, int level) {
        String methodOrConstructorName = methodOrConstructorElement.getSimpleName().toString();
        //System.out.printf("Type: %s, injection: %s \n",typeElementName, methodOrConstructorName);
        if (methodOrConstructorName.startsWith("<init>")) {
            addConstructor(methodOrConstructorElement, level);
        } else {
            addMethod(methodOrConstructorElement, level);
        }
    }

    private void addConstructor(ExecutableElement methodElement, int level) {
        final Element enclosing = methodElement.getEnclosingElement();
        final TypeElement declaringClassElement = (TypeElement) enclosing;
        final Class[] paramTypes = getParameterTypes(methodElement, level);
        final Class[] exceptionTypes = getExceptionTypes(methodElement, level);
        final Class<?> classContainingMethod = Class.forNameSafe(declaringClassElement.asType().toString(), level + 1);
        final Constructor constructor = new Constructor(classContainingMethod,
                                                        paramTypes,
                                                        exceptionTypes,
                                                        convertModifiersFromAnnnotationProcessing(methodElement.getModifiers()));

        final List<Annotation> annotations = extractAnnotations(methodElement, level);

        constructor.setDeclaredAnnotations(annotations);

        classContainingMethod.addConstructor(constructor);
        annotatedClassSet.add(classContainingMethod);
    }

    private void addMethod(ExecutableElement methodElement, int level) {
        final Element enclosing = methodElement.getEnclosingElement();
        final String methodName = methodElement.getSimpleName().toString();
        final TypeElement declaringClassElement = (TypeElement) enclosing;
        final Class[] paramTypes = getParameterTypes(methodElement, level);
        final Class[] exceptionTypes = getExceptionTypes(methodElement, level);
        final String returnTypeName = methodElement.getReturnType().toString();
        final Class<?> declaringClass = Class.forNameSafe(declaringClassElement.asType().toString(), level + 1);
        final Method method = new Method(declaringClass,
                                         methodName,
                                         paramTypes,
                                         Class.forNameSafe(returnTypeName, level),
                                         exceptionTypes,
                                         convertModifiersFromAnnnotationProcessing(methodElement.getModifiers()));

        final List<Annotation> annotations = extractAnnotations(methodElement, level);

        method.setDeclaredAnnotations(annotations);
        method.setIsVarArgs(methodElement.isVarArgs());

        declaringClass.addMethod(method);
        annotatedClassSet.add(declaringClass);
    }

    private List<Annotation> extractAnnotations(Element annotatedElement, int level) {
        final List<Annotation> annotations = new ArrayList<>();
        for (AnnotationMirror annotationMirror : annotatedElement.getAnnotationMirrors()) {
            final Map<Method, Object> mapMethodToValue = new HashMap<>();

            final Class<?> annotationClass = createClass(annotationMirror.getAnnotationType(), level);
            annotationClass.setModifiers(annotationClass.getModifiers() | Class.ANNOTATION);
            annotationClasses.add(annotationClass);
            for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : annotationMirror.getElementValues().entrySet()) {
                final String methodOfAnnotationName = entry.getKey().getSimpleName().toString();

                //RnR 2
                final Method methodOfAnnotation = new Method(annotationClass,
                                                             methodOfAnnotationName,
                                                             //TODO : param types
                                                             new Class[0],
                                                             Class.forNameSafe(entry.getKey().getReturnType().toString(), level),
                                                             //TODO : exception types
                                                             new Class[0],
                                                             java.lang.reflect.Modifier.PUBLIC
                );
                mapMethodToValue.put(methodOfAnnotation, entry.getValue().getValue());
            }

            final Annotation annotation = new Annotation(annotationClass, mapMethodToValue);
            annotations.add(annotation);
        }
        return annotations;
    }

    private Class createClass(TypeMirror typeMirror, int level) {
        Class result;
        String className = null;
        boolean isPrimitive = false;
        boolean isArray = false;
        boolean isInterface = false;
        Class component = null;
        GenericDeclaration declaration = null;

        if (typeMirror instanceof DeclaredType) {
            className = ((TypeElement) ((DeclaredType) typeMirror).asElement()).getQualifiedName().toString();
            if (!((DeclaredType) typeMirror).getTypeArguments().isEmpty()) {
                declaration = new GenericDeclaration();
                TypeVariable[] typesVariables = new TypeVariable[((DeclaredType) typeMirror).getTypeArguments().size()];
                int index = 0;
                for (TypeMirror mirror : ((DeclaredType) typeMirror).getTypeArguments()) {
                    TypeVariableImpl typeVariableImpl = new TypeVariableImpl();
                    typesVariables[index] = typeVariableImpl;
                    typeVariableImpl.setName(mirror.toString());
                    index++;
                }

                declaration.setTypeParameters(typesVariables);
            }
            isInterface = ((com.sun.tools.javac.code.Type) typeMirror).isInterface();
            final int indexOfChevron = className.indexOf('<');
            if (indexOfChevron != -1) {
                className = className.substring(0, indexOfChevron);
            }
            if (level + 1 <= maxLevel) {
                final List<? extends Element> enclosedElements = ((TypeElement) ((DeclaredType) typeMirror).asElement()).getEnclosedElements();
                for (Element enclosedElement : enclosedElements) {
                    mapElementToReflection(enclosedElement, level + 1);
                }
            }
        } else if (typeMirror instanceof ArrayType) {
            //warning, this must come before Primitive as arrays are also primitives (here)
            isArray = true;
            className = ((ArrayType) typeMirror).getComponentType().toString() + "[]";
            component = createClass(((ArrayType) typeMirror).getComponentType(), level);
        } else if (typeMirror instanceof PrimitiveType) {
            isPrimitive = true;
            className = typeMirror.toString();
        }

        result = Class.forNameSafe(className, level);

        if (isArray) {
            result.setIsArray(true);
        }
        if (isPrimitive) {
            result.setIsPrimitive(true);
        }
        if (component != null) {
            result.setComponentType(component);
        }

        if (declaration != null) {
            result.setGenericInfo(declaration);
        }
        if (isInterface) {
            result.setIsInterface(true);
        }
        return result;
    }

    /*Visible for testing*/ int convertModifiersFromAnnnotationProcessing(Set<Modifier> modifiers) {
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
                case FINAL:
                    result |= java.lang.reflect.Modifier.FINAL;
                    break;
                case SYNCHRONIZED:
                    result |= java.lang.reflect.Modifier.SYNCHRONIZED;
                    break;
                case VOLATILE:
                    result |= java.lang.reflect.Modifier.VOLATILE;
                    break;
                default:
            }
        }
        return result;
    }

    private String getTypeName(Element typeElement) {
        String injectedClassName = null;
        final TypeMirror fieldTypeMirror = typeElement.asType();
        if (fieldTypeMirror instanceof DeclaredType) {
            injectedClassName = ((TypeElement) ((DeclaredType) fieldTypeMirror).asElement()).getQualifiedName().toString();
        } else if (fieldTypeMirror instanceof PrimitiveType) {
            injectedClassName = fieldTypeMirror.toString();
        } else if (fieldTypeMirror instanceof ArrayType) {
            injectedClassName = ((ArrayType) fieldTypeMirror).getComponentType().toString() + "[]";
        }
        return injectedClassName;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        //http://stackoverflow.com/a/8188860/693752
        return SourceVersion.latest();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return targetAnnotatedClasses;
    }

    public void setTargetAnnotatedClasses(Set<String> targetAnnotatedClasses) {
        this.targetAnnotatedClasses = targetAnnotatedClasses;
    }

    /**
     * Level 0 : everything that is annotated will place every class referenced in its signature in the pool.
     * Level N : everything that is of level N-1 will place every class referenced in the signature of its members in the pool.
     *
     * A class that is only referenced in the pool, but empty (of level N), is called partially referenced or partial.
     * A class whose members are fully known (of level 0-->N-1), is called fully referenced or full.
     *
     * @param maxLevel
     */
    public void setMaxLevel(int maxLevel) {
        this.maxLevel = maxLevel;
    }

    public Set<String> getTargetAnnotatedClasses() {
        return targetAnnotatedClasses;
    }

    public Set<Class> getAnnotatedClassSet() {
        return annotatedClassSet;
    }

    public Set<Class> getAnnotationClasses() {
        return annotationClasses;
    }

    public Set<Class<?>> getClassesContainingAnnotation(Class<? extends Annotation> annotationType) {
        return mapAnnotationTypeToClassContainingAnnotation.get(annotationType);
    }

    public Map<Class<? extends Annotation>, Set<Class<?>>> getMapAnnotationTypeToClassContainingAnnotation() {
        return mapAnnotationTypeToClassContainingAnnotation;
    }
}
