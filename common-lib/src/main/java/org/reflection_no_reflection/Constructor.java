package org.reflection_no_reflection;

import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import sun.reflect.generics.factory.GenericsFactory;
import sun.reflect.generics.repository.ConstructorRepository;

/**
 * @author SNI.
 */
public class Constructor<T> extends GenericDeclaration {

    private Class<T> clazz;
    private int slot;
    private Class[] parameterTypes;
    private Class[] exceptionTypes;
    private int modifiers;
    // Generics and annotations support
    private transient String signature;
    // generic info repository; lazily initialized
    private ConstructorRepository genericInfo;
    private byte[] annotations;
    private byte[] parameterAnnotations;

    // For non-public members or members in package-private classes,
    // it is necessary to perform somewhat expensive security checks.
    // If the security check succeeds for a given class, it will
    // always succeed (it is not affected by the granting or revoking
    // of permissions); we speed up the check in the common case by
    // remembering the last Class for which the check succeeded.
    private volatile Class securityCheckCache;

    // Modifiers that can be applied to a constructor in source code
    private static final int LANGUAGE_MODIFIERS =
        Modifier.PUBLIC | Modifier.PROTECTED | Modifier.PRIVATE;

    // Generics infrastructure
    // Accessor for factory
    private GenericsFactory getFactory() {
        // create scope and factory
        throw new UnsupportedOperationException();
    }

    // Accessor for generic info repository
    private ConstructorRepository getGenericInfo() {
        // lazily initialize repository if necessary
        if (genericInfo == null) {
            // create and cache generic info repository
            genericInfo =
                ConstructorRepository.make(getSignature(),
                                           getFactory());
        }
        return genericInfo; //return cached repository
    }

    // For sharing of ConstructorAccessors. This branching structure
    // is currently only two levels deep (i.e., one root Constructor
    // and potentially many Constructor objects pointing to it.)
    private Constructor<T> root;

    /**
     * Package-private constructor used by ReflectAccess to enable
     * instantiation of these objects in Java code from the java.lang
     * package via sun.reflect.LangReflectAccess.
     */
    public Constructor(Class<T> declaringClass,
                       Class[] parameterTypes,
                       Class[] checkedExceptions,
                       int modifiers) {
        this.clazz = declaringClass;
        this.parameterTypes = parameterTypes;
        this.exceptionTypes = checkedExceptions;
        this.modifiers = modifiers;
    }

    /**
     * Returns the {@code Class} object representing the class that declares
     * the constructor represented by this {@code Constructor} object.
     */
    public Class<T> getDeclaringClass() {
        return clazz;
    }

    /**
     * Returns the name of this constructor, as a string.  This is
     * always the same as the simple name of the constructor's declaring
     * class.
     */
    public String getName() {
        return getDeclaringClass().getName();
    }

    /**
     * Returns the Java language modifiers for the constructor
     * represented by this {@code Constructor} object, as an integer. The
     * {@code Modifier} class should be used to decode the modifiers.
     *
     * @see Modifier
     */
    public int getModifiers() {
        return modifiers;
    }

    /**
     * Returns an array of {@code TypeVariable} objects that represent the
     * type variables declared by the generic declaration represented by this
     * {@code GenericDeclaration} object, in declaration order.  Returns an
     * array of length 0 if the underlying generic declaration declares no type
     * variables.
     *
     * @return an array of {@code TypeVariable} objects that represent
     * the type variables declared by this generic declaration
     * @since 1.5
     */
    public TypeVariable<Constructor<T>>[] getTypeParameters() {
        if (getSignature() != null) {
            return (TypeVariable<Constructor<T>>[]) getGenericInfo().getTypeParameters();
        } else {
            return (TypeVariable<Constructor<T>>[]) new TypeVariable[0];
        }
    }

    /**
     * Returns an array of {@code Class} objects that represent the formal
     * parameter types, in declaration order, of the constructor
     * represented by this {@code Constructor} object.  Returns an array of
     * length 0 if the underlying constructor takes no parameters.
     *
     * @return the parameter types for the constructor this object
     * represents
     */
    public Class<?>[] getParameterTypes() {
        return (Class<?>[]) parameterTypes.clone();
    }

    /**
     * Returns an array of {@code Type} objects that represent the formal
     * parameter types, in declaration order, of the method represented by
     * this {@code Constructor} object. Returns an array of length 0 if the
     * underlying method takes no parameters.
     *
     * <p>If a formal parameter type is a parameterized type,
     * the {@code Type} object returned for it must accurately reflect
     * the actual type parameters used in the source code.
     *
     * <p>If a formal parameter type is a type variable or a parameterized
     * type, it is created. Otherwise, it is resolved.
     *
     * @return an array of {@code Type}s that represent the formal
     * parameter types of the underlying method, in declaration order
     * @throws TypeNotPresentException if any of the parameter
     * types of the underlying method refers to a non-existent type
     * declaration
     * @since 1.5
     */
    public Type[] getGenericParameterTypes() {
        if (getSignature() != null) {
            return getGenericInfo().getParameterTypes();
        } else {
            return getParameterTypes();
        }
    }

    /**
     * Returns an array of {@code Class} objects that represent the types
     * of exceptions declared to be thrown by the underlying constructor
     * represented by this {@code Constructor} object.  Returns an array of
     * length 0 if the constructor declares no exceptions in its {@code throws} clause.
     *
     * @return the exception types declared as being thrown by the
     * constructor this object represents
     */
    public Class<?>[] getExceptionTypes() {
        return (Class<?>[]) exceptionTypes.clone();
    }

    /**
     * Returns an array of {@code Type} objects that represent the
     * exceptions declared to be thrown by this {@code Constructor} object.
     * Returns an array of length 0 if the underlying method declares
     * no exceptions in its {@code throws} clause.
     *
     * <p>If an exception type is a parameterized type, the {@code Type}
     * object returned for it must accurately reflect the actual type
     * parameters used in the source code.
     *
     * <p>If an exception type is a type variable or a parameterized
     * type, it is created. Otherwise, it is resolved.
     *
     * @return an array of Types that represent the exception types
     * thrown by the underlying method
     */
    public Type[] getGenericExceptionTypes() {
        Type[] result;
        if (getSignature() != null &&
            ((result = getGenericInfo().getExceptionTypes()).length > 0)) {
            return result;
        } else {
            return getExceptionTypes();
        }
    }

    /**
     * Compares this {@code Constructor} against the specified object.
     * Returns true if the objects are the same.  Two {@code Constructor} objects are
     * the same if they were declared by the same class and have the
     * same formal parameter types.
     */
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof Constructor) {
            Constructor other = (Constructor) obj;
            if (getDeclaringClass() == other.getDeclaringClass()) {
                /* Avoid unnecessary cloning */
                Class[] params1 = parameterTypes;
                Class[] params2 = other.parameterTypes;
                if (params1.length == params2.length) {
                    for (int i = 0; i < params1.length; i++) {
                        if (params1[i] != params2[i]) {
                            return false;
                        }
                    }
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns a hashcode for this {@code Constructor}. The hashcode is
     * the same as the hashcode for the underlying constructor's
     * declaring class name.
     */
    public int hashCode() {
        return getDeclaringClass().getName().hashCode();
    }

    /**
     * Returns a string describing this {@code Constructor}.  The string is
     * formatted as the constructor access modifiers, if any,
     * followed by the fully-qualified name of the declaring class,
     * followed by a parenthesized, comma-separated list of the
     * constructor's formal parameter types.  For example:
     * <pre>
     *    public java.util.Hashtable(int,float)
     * </pre>
     *
     * <p>The only possible modifiers for constructors are the access
     * modifiers {@code public}, {@code protected} or
     * {@code private}.  Only one of these may appear, or none if the
     * constructor has default (package) access.
     */
    public String toString() {
        try {
            StringBuffer sb = new StringBuffer();
            int mod = getModifiers() & LANGUAGE_MODIFIERS;
            if (mod != 0) {
                sb.append(Modifier.toString(mod) + " ");
            }
            sb.append(Field.getTypeName(getDeclaringClass()));
            sb.append("(");
            Class[] params = parameterTypes; // avoid clone
            for (int j = 0; j < params.length; j++) {
                sb.append(Field.getTypeName(params[j]));
                if (j < (params.length - 1)) {
                    sb.append(",");
                }
            }
            sb.append(")");
            Class[] exceptions = exceptionTypes; // avoid clone
            if (exceptions.length > 0) {
                sb.append(" throws ");
                for (int k = 0; k < exceptions.length; k++) {
                    sb.append(exceptions[k].getName());
                    if (k < (exceptions.length - 1)) {
                        sb.append(",");
                    }
                }
            }
            return sb.toString();
        } catch (Exception e) {
            return "<" + e + ">";
        }
    }

    /**
     * Returns a string describing this {@code Constructor},
     * including type parameters.  The string is formatted as the
     * constructor access modifiers, if any, followed by an
     * angle-bracketed comma separated list of the constructor's type
     * parameters, if any, followed by the fully-qualified name of the
     * declaring class, followed by a parenthesized, comma-separated
     * list of the constructor's generic formal parameter types.
     *
     * A space is used to separate access modifiers from one another
     * and from the type parameters or return type.  If there are no
     * type parameters, the type parameter list is elided; if the type
     * parameter list is present, a space separates the list from the
     * class name.  If the constructor is declared to throw
     * exceptions, the parameter list is followed by a space, followed
     * by the word "{@code throws}" followed by a
     * comma-separated list of the thrown exception types.
     *
     * <p>The only possible modifiers for constructors are the access
     * modifiers {@code public}, {@code protected} or
     * {@code private}.  Only one of these may appear, or none if the
     * constructor has default (package) access.
     *
     * @return a string describing this {@code Constructor},
     * include type parameters
     * @since 1.5
     */
    public String toGenericString() {
        try {
            StringBuilder sb = new StringBuilder();
            int mod = getModifiers() & LANGUAGE_MODIFIERS;
            if (mod != 0) {
                sb.append(Modifier.toString(mod) + " ");
            }
            TypeVariable<?>[] typeparms = getTypeParameters();
            if (typeparms.length > 0) {
                boolean first = true;
                sb.append("<");
                for (TypeVariable<?> typeparm : typeparms) {
                    if (!first) {
                        sb.append(",");
                    }
                    // Class objects can't occur here; no need to test
                    // and call Class.getName().
                    sb.append(typeparm.toString());
                    first = false;
                }
                sb.append("> ");
            }
            sb.append(Field.getTypeName(getDeclaringClass()));
            sb.append("(");
            Type[] params = getGenericParameterTypes();
            for (int j = 0; j < params.length; j++) {
                String param = (params[j] instanceof Class<?>) ?
                    Field.getTypeName((Class<?>) params[j]) :
                    (params[j].toString());
                sb.append(param);
                if (j < (params.length - 1)) {
                    sb.append(",");
                }
            }
            sb.append(")");
            Type[] exceptions = getGenericExceptionTypes();
            if (exceptions.length > 0) {
                sb.append(" throws ");
                for (int k = 0; k < exceptions.length; k++) {
                    sb.append((exceptions[k] instanceof Class) ?
                                  ((Class) exceptions[k]).getName() :
                        exceptions[k].toString());
                    if (k < (exceptions.length - 1)) {
                        sb.append(",");
                    }
                }
            }
            return sb.toString();
        } catch (Exception e) {
            return "<" + e + ">";
        }
    }

    /**
     * Returns {@code true} if this constructor was declared to take
     * a variable number of arguments; returns {@code false}
     * otherwise.
     *
     * @return {@code true} if an only if this constructor was declared to
     * take a variable number of arguments.
     * @since 1.5
     */
    public boolean isVarArgs() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns {@code true} if this constructor is a synthetic
     * constructor; returns {@code false} otherwise.
     *
     * @return true if and only if this constructor is a synthetic
     * constructor as defined by the Java Language Specification.
     * @since 1.5
     */
    public boolean isSynthetic() {
        throw new UnsupportedOperationException();
    }

    int getSlot() {
        return slot;
    }

    String getSignature() {
        return signature;
    }

    byte[] getRawAnnotations() {
        return annotations;
    }

    byte[] getRawParameterAnnotations() {
        return parameterAnnotations;
    }

    /**
     * @throws NullPointerException {@inheritDoc}
     * @since 1.5
     */
    public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
        if (annotationClass == null) {
            throw new NullPointerException();
        }

        return (T) declaredAnnotations().get(annotationClass);
    }

    private static final Annotation[] EMPTY_ANNOTATION_ARRAY = new Annotation[0];

    /**
     * @since 1.5
     */
    public Annotation[] getDeclaredAnnotations() {
        return declaredAnnotations().values().toArray(EMPTY_ANNOTATION_ARRAY);
    }

    public void setDeclaredAnnotations(List<Annotation> annotations) {
        declaredAnnotations.clear();
        for (Annotation annotation : annotations) {
            declaredAnnotations.put(annotation.annotationType(), annotation);
        }
    }

    private Map<Class, Annotation> declaredAnnotations = new HashMap<>();

    private synchronized Map<Class, Annotation> declaredAnnotations() {
        return declaredAnnotations;
    }

    /**
     * Returns an array of arrays that represent the annotations on the formal
     * parameters, in declaration order, of the method represented by
     * this {@code Constructor} object. (Returns an array of length zero if the
     * underlying method is parameterless.  If the method has one or more
     * parameters, a nested array of length zero is returned for each parameter
     * with no annotations.) The annotation objects contained in the returned
     * arrays are serializable.  The caller of this method is free to modify
     * the returned arrays; it will have no effect on the arrays returned to
     * other callers.
     *
     * @return an array of arrays that represent the annotations on the formal
     * parameters, in declaration order, of the method represented by this
     * Constructor object
     * @since 1.5
     */
    public Annotation[][] getParameterAnnotations() {
        throw new UnsupportedOperationException();
    }
}
