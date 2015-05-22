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
//TODO MUST REMOVE THIS. ANNOTATION TYPES SHOULD BE DYNAMIC
//getSupportedAnnotationType must be triggered
//chances are that the processor must be called in a different way by the gradle
//plugin. We have to get full control over annotation processor instance creation.
@SupportedOptions({"guiceAnnotationDatabasePackageName", "guiceUsesFragmentUtil", "guiceCommentsInjector", "annotatedClasses"})
public class Processor extends AbstractProcessor {

    private boolean isUsingFragmentUtil = true;
    private boolean isCommentingInjector = true;
    private Set<String> annotatedClasses = new HashSet<>();

    /** Contains all classes that contain injection points. */
    private HashSet<Class> annotatedClassSet = new HashSet<>();

    private ProcessingEnvironment processingEnv;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        this.processingEnv = processingEnv;
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
            for (Element injectionPoint : roundEnv.getElementsAnnotatedWith(annotation)) {
                if (injectionPoint.getEnclosingElement() instanceof TypeElement && injectionPoint instanceof VariableElement) {
                    addFieldToAnnotationDatabase(injectionPoint);
                } else if (injectionPoint.getEnclosingElement() instanceof ExecutableElement && injectionPoint instanceof VariableElement) {
                    addParameterToAnnotationDatabase(injectionPoint);
                } else if (injectionPoint instanceof ExecutableElement) {
                    addMethodOrConstructorToAnnotationDatabase((ExecutableElement) injectionPoint);
                } else if (injectionPoint instanceof TypeElement) {
                    addClassToAnnotationDatabase(injectionPoint);
                }
            }
        }

        return true;
    }

    private void addClassToAnnotationDatabase(Element classElement) {
        String typeElementName = getTypeName((TypeElement) classElement);
        //System.out.printf("Type: %s, is injected\n",typeElementName);
        final Class newClass = new Class(typeElementName);
        annotatedClassSet.add(newClass);
        final List<Annotation> annotations = extractAnnotations(classElement);
        newClass.setAnnotations(annotations);
    }

    private void addFieldToAnnotationDatabase(Element fieldElement) {
        Class fieldClass;
        //System.out.printf("Type: %s, injection: %s \n",typeElementName, injectionPointName);
        //TODO change this to get primitives and arrays
        fieldClass = getClass(fieldElement.asType());

        final Set<Modifier> modifiers = fieldElement.getModifiers();
        String injectionPointName = fieldElement.getSimpleName().toString();
        TypeElement declaringClassElement = (TypeElement) fieldElement.getEnclosingElement();
        String declaringClassName = getTypeName(declaringClassElement);
        final List<Annotation> annotations = extractAnnotations(fieldElement);
        int modifiersInt = convertModifiersFromAnnnotationProcessing(modifiers);
        final Field field = new Field(injectionPointName, fieldClass, getClass(declaringClassName), modifiersInt, annotations);

        //rnr 2
        final Class<?> classContainingField = getClass(declaringClassName);
        classContainingField.addField(field);
        annotatedClassSet.add(classContainingField);
    }

    private void addParameterToAnnotationDatabase(Element paramElement) {
        Element enclosing = paramElement.getEnclosingElement();
        String injectionPointName = enclosing.getSimpleName().toString();
        //System.out.printf("Type: %s, injection: %s \n",typeElementName, injectionPointName);
        final ExecutableElement methodOrConstructor = (ExecutableElement) paramElement.getEnclosingElement();
        if (injectionPointName.startsWith("<init>")) {
            addConstructor(methodOrConstructor);
        } else {
            addMethod(methodOrConstructor);
        }
    }

    private Class[] getParameterTypes(ExecutableElement methodElement) {
        final List<? extends VariableElement> parameters = methodElement.getParameters();
        Class[] paramTypes = new Class[parameters.size()];
        for (int indexParam = 0; indexParam < parameters.size(); indexParam++) {
            VariableElement parameter = parameters.get(indexParam);
            paramTypes[indexParam] = getClass(getTypeName(parameter));
        }
        return paramTypes;
    }

    private Class[] getExceptionTypes(ExecutableElement methodElement) {
        final List<? extends TypeMirror> exceptionTypes = methodElement.getThrownTypes();
        Class[] paramTypes = new Class[exceptionTypes.size()];
        for (int indexParam = 0; indexParam < exceptionTypes.size(); indexParam++) {
            TypeMirror exceptionType = exceptionTypes.get(indexParam);
            paramTypes[indexParam] = getClass(exceptionType.toString());
        }
        return paramTypes;
    }

    private void addMethodOrConstructorToAnnotationDatabase(ExecutableElement methodOrConstructorElement) {
        String injectionPointName = methodOrConstructorElement.getSimpleName().toString();
        //System.out.printf("Type: %s, injection: %s \n",typeElementName, injectionPointName);
        if (injectionPointName.startsWith("<init>")) {
            addConstructor(methodOrConstructorElement);
        } else {
            addMethod(methodOrConstructorElement);
        }
    }

    protected void addToInjectedFields(Set<Modifier> modifiers, String injectedClassName, Element fieldElement) {
        String injectionPointName = fieldElement.getSimpleName().toString();
        TypeElement declaringClassElement = (TypeElement) fieldElement.getEnclosingElement();
        String declaringClassName = getTypeName(declaringClassElement);
        final List<Annotation> annotations = extractAnnotations(fieldElement);
        int modifiersInt = convertModifiersFromAnnnotationProcessing(modifiers);
        final Field field = new Field(injectionPointName, getClass(injectedClassName), getClass(declaringClassName), modifiersInt, annotations);

        //rnr 2
        final Class<?> classContainingField = getClass(declaringClassName);
        classContainingField.addField(field);
        annotatedClassSet.add(classContainingField);
    }

    private void addConstructor(ExecutableElement methodElement) {
        final Element enclosing = methodElement.getEnclosingElement();
        final TypeElement declaringClassElement = (TypeElement) enclosing;
        final String declaringClassName = getTypeName(declaringClassElement);
        final Class[] paramTypes = getParameterTypes(methodElement);
        final Class[] exceptionTypes = getExceptionTypes(methodElement);
        final Constructor constructor = new Constructor(getClass(declaringClassName),
                                                        paramTypes,
                                                        exceptionTypes,
                                                        convertModifiersFromAnnnotationProcessing(methodElement.getModifiers()));

        final List<Annotation> annotations = extractAnnotations(methodElement);

        constructor.setDeclaredAnnotations(annotations);

        final Class<?> classContainingMethod = getClass(declaringClassName);
        classContainingMethod.addConstructor(constructor);
        annotatedClassSet.add(classContainingMethod);
    }

    private void addMethod(ExecutableElement methodElement) {
        final Element enclosing = methodElement.getEnclosingElement();
        final String methodName = methodElement.getSimpleName().toString();
        final TypeElement declaringClassElement = (TypeElement) enclosing;
        final String declaringClassName = getTypeName(declaringClassElement);
        final Class[] paramTypes = getParameterTypes(methodElement);
        final Class[] exceptionTypes = getExceptionTypes(methodElement);
        final String returnTypeName = methodElement.getReturnType().toString();
        final Method method = new Method(getClass(declaringClassName),
                                         methodName,
                                         paramTypes,
                                         getClass(returnTypeName),
                                         exceptionTypes,
                                         convertModifiersFromAnnnotationProcessing(methodElement.getModifiers()));

        final List<Annotation> annotations = extractAnnotations(methodElement);

        method.setDeclaredAnnotations(annotations);

        final Class<?> classContainingMethod = getClass(declaringClassName);
        classContainingMethod.addMethod(method);
        annotatedClassSet.add(classContainingMethod);
    }

    private List<Annotation> extractAnnotations(Element methodElement) {
        final List<Annotation> annotations = new ArrayList<>();
        for (AnnotationMirror annotationMirror : methodElement.getAnnotationMirrors()) {
            final Map<Method, Object> mapMethodToValue = new HashMap<>();
            final String annotationType = annotationMirror.getAnnotationType().toString();
            for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : annotationMirror.getElementValues().entrySet()) {
                final String methodOfAnnotationName = entry.getKey().getSimpleName().toString();

                //RnR 2
                final Method methodOfAnnotation = new Method(getClass(annotationType),
                                                             methodOfAnnotationName,
                                                             //TODO : param types
                                                             new Class[0],
                                                             getClass(entry.getKey().getReturnType().toString()),
                                                             //TODO : exception types
                                                             new Class[0],
                                                             java.lang.reflect.Modifier.PUBLIC
                );
                mapMethodToValue.put(methodOfAnnotation, entry.getValue().getValue());
            }

            final Annotation annotation = new Annotation(getClass(annotationType), mapMethodToValue);
            annotations.add(annotation);
        }
        return annotations;
    }

    private Class getClass(TypeMirror typeMirror) {
        Class result;
        String className = null;
        boolean isPrimitive = false;
        boolean isArray = false;
        boolean isInterface = false;
        Class component = null;
        GenericDeclaration declaration = null;

        if (typeMirror instanceof DeclaredType) {
            className = getTypeName((TypeElement) ((DeclaredType) typeMirror).asElement());
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
            //TODO
            //isInterface = ((DeclaredType) typeMirror).
        } else if (typeMirror instanceof PrimitiveType) {
            isPrimitive = true;
            className = typeMirror.toString();
        } else if (typeMirror instanceof ArrayType) {
            isArray = true;
            className = ((ArrayType) typeMirror).getComponentType().toString() + "[]";
            component = getClass(((ArrayType) typeMirror).getComponentType());
        }

        result = new Class(className);
        result.setIsArray(isArray);
        result.setIsPrimitive(isPrimitive);
        result.setComponentType(component);
        result.setGenericInfo(declaration);
        return result;
    }

    private Class getClass(String name) {
        try {
            return Class.forName(name);
        } catch (ClassNotFoundException e) {
            return new Class(name);
        }
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

    private String getTypeName(TypeElement typeElement) {
        if (typeElement.getEnclosingElement() instanceof TypeElement) {
            return getTypeName(typeElement.getEnclosingElement()) + "$" + typeElement.getSimpleName().toString();
        } else {
            return typeElement.getQualifiedName().toString();
        }
    }

    private String getTypeName(Element injectionPoint) {
        String injectedClassName = null;
        final TypeMirror fieldTypeMirror = injectionPoint.asType();
        if (fieldTypeMirror instanceof DeclaredType) {
            injectedClassName = getTypeName((TypeElement) ((DeclaredType) fieldTypeMirror).asElement());
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
