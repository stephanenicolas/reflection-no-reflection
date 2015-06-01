package org.reflection_no_reflection;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.reflection_no_reflection.runtime.BaseReflector;
import org.reflection_no_reflection.runtime.Module;
import org.reflection_no_reflection.visit.ClassPoolVisitStrategy;
import org.reflection_no_reflection.visit.ClassPoolVisitor;
import sun.reflect.annotation.AnnotationType;

/**
 * @author SNI.
 */
public class Class<T> extends GenericDeclaration implements java.io.Serializable,
    java.lang.reflect.Type {

    public static final int ANNOTATION = 0x00002000;
    private static final int ENUM = 0x00004000;
    private static final int SYNTHETIC = 0x00001000;
    private boolean isInterface;
    private boolean isArray;
    private boolean isPrimitive;
    // cache the name to reduce the number of calls into the VM
    private String name;
    private Class<? super T> superclass;
    private Package myPackage;
    private Class<?>[] interfaces;
    private Class<?> componentType;
    private int modifiers;
    private Method enclosingMethod;
    private EnclosingMethodInfo[] enclosingMethodInfos;
    private Class<?>[] classes;
    private List<Field> fields = new ArrayList<>();
    private List<Method> methods = new ArrayList<>();
    private List<Constructor<?>> constructors = new ArrayList<>();
    private List<org.reflection_no_reflection.Annotation> annotationList = new ArrayList<>();
    private GenericDeclaration genericInfo;
    private Constructor<?> enclosingConstructor;
    private int level;

    private static Set<Class> CLASS_POOL = new HashSet<>();
    private Class<?> declaringClass;
    private BaseReflector reflector;

    private Class(String name) {
        if (name == null) {
            throw new RuntimeException("Class has null name");
        }
        this.name = name;
        CLASS_POOL.add(this);
    }

    /**
     * Converts the object to a string. The string representation is the
     * string "class" or "interface", followed by a space, and then by the
     * fully qualified name of the class in the format returned by
     * {@code getName}.  If this {@code Class} object represents a
     * primitive type, this method returns the name of the primitive type.  If
     * this {@code Class} object represents void this method returns
     * "void".
     *
     * @return a string representation of this class object.
     */
    public String toString() {
        return (isInterface() ? "interface " : (isPrimitive() ? "" : "class "))
            + getName();
    }

    /**
     * Returns the {@code Class} object associated with the class or
     * interface with the given string name.  Invoking this method is
     * equivalent to:
     *
     * <blockquote>
     * {@code Class.forName(className, true, currentLoader)}
     * </blockquote>
     *
     * where {@code currentLoader} denotes the defining class loader of
     * the current class.
     *
     * <p> For example, the following code fragment returns the
     * runtime {@code Class} descriptor for the class named
     * {@code java.lang.Thread}:
     *
     * <blockquote>
     * {@code Class t = Class.forName("java.lang.Thread")}
     * </blockquote>
     * <p>
     * A call to {@code forName("X")} causes the class named
     * {@code X} to be initialized.
     *
     * @param className the fully qualified name of the desired class.
     * @return the {@code Class} object for the class with the
     * specified name.
     * @throws LinkageError if the linkage fails
     * @throws ExceptionInInitializerError if the initialization provoked
     * by this method fails
     * @throws ClassNotFoundException if the class cannot be located
     */
    public static Class<?> forName(String className)
        throws ClassNotFoundException {
        for (Class aClass : CLASS_POOL) {
            if (aClass.getName().equals(className)) {
                return aClass;
            }
        }

        throw new ClassNotFoundException(className);
    }

    public static Class<?> forNameSafe(String className) {
        for (Class aClass : CLASS_POOL) {
            if (aClass.getName().equals(className)) {
                return aClass;
            }
        }

        final Class aClass = new Class(className);
        CLASS_POOL.add(aClass);
        return aClass;
    }

    public static Class<?> forNameSafe(String className, int level) {
        if (className == null) {
            return null;
        }
        for (Class aClass : CLASS_POOL) {
            if (aClass.getName().equals(className)) {
                if (aClass.level > level) {
                    aClass.level = level;
                }
                return aClass;
            }
        }

        final Class aClass = new Class(className);
        CLASS_POOL.add(aClass);
        aClass.level = level;
        return aClass;
    }

    public static void visit(ClassPoolVisitor classPoolVisitor) {
        ClassPoolVisitStrategy visitStrategy = new ClassPoolVisitStrategy();
        visitStrategy.visit(CLASS_POOL, classPoolVisitor);
    }

    /**
     * Returns the {@code Class} object associated with the class or
     * interface with the given string name, using the given class loader.
     * Given the fully qualified name for a class or interface (in the same
     * format returned by {@code getName}) this method attempts to
     * locate, load, and link the class or interface.  The specified class
     * loader is used to load the class or interface.  If the parameter
     * {@code loader} is null, the class is loaded through the bootstrap
     * class loader.  The class is initialized only if the
     * {@code initialize} parameter is {@code true} and if it has
     * not been initialized earlier.
     *
     * <p> If {@code name} denotes a primitive type or void, an attempt
     * will be made to locate a user-defined class in the unnamed package whose
     * name is {@code name}. Therefore, this method cannot be used to
     * obtain any of the {@code Class} objects representing primitive
     * types or void.
     *
     * <p> If {@code name} denotes an array class, the component type of
     * the array class is loaded but not initialized.
     *
     * <p> For example, in an instance method the expression:
     *
     * <blockquote>
     * {@code Class.forName("Foo")}
     * </blockquote>
     *
     * is equivalent to:
     *
     * <blockquote>
     * {@code Class.forName("Foo", true, this.getClass().getClassLoader())}
     * </blockquote>
     *
     * Note that this method throws errors related to loading, linking or
     * initializing as specified in Sections 12.2, 12.3 and 12.4 of <em>The
     * Java Language Specification</em>.
     * Note that this method does not check whether the requested class
     * is accessible to its caller.
     *
     * <p> If the {@code loader} is {@code null}, and a security
     * manager is present, and the caller's class loader is not null, then this
     * method calls the security manager's {@code checkPermission} method
     * with a {@code RuntimePermission("getClassLoader")} permission to
     * ensure it's ok to access the bootstrap class loader.
     *
     * @param name fully qualified name of the desired class
     * @param initialize whether the class must be initialized
     * @param loader class loader from which the class must be loaded
     * @return class object representing the desired class
     * @throws LinkageError if the linkage fails
     * @throws ExceptionInInitializerError if the initialization provoked
     * by this method fails
     * @throws ClassNotFoundException if the class cannot be located by
     * the specified class loader
     * @see java.lang.Class#forName(String)
     * @see java.lang.ClassLoader
     * @since 1.2
     */
    public static Class<?> forName(String name, boolean initialize,
                                   ClassLoader loader)
        throws ClassNotFoundException {
        throw new UnsupportedOperationException();
    }

    /**
     * Determines if the class or interface represented by this
     * {@code Class} object is either the same as, or is a superclass or
     * superinterface of, the class or interface represented by the specified
     * {@code Class} parameter. It returns {@code true} if so;
     * otherwise it returns {@code false}. If this {@code Class}
     * object represents a primitive type, this method returns
     * {@code true} if the specified {@code Class} parameter is
     * exactly this {@code Class} object; otherwise it returns
     * {@code false}.
     *
     * <p> Specifically, this method tests whether the type represented by the
     * specified {@code Class} parameter can be converted to the type
     * represented by this {@code Class} object via an identity conversion
     * or via a widening reference conversion. See <em>The Java Language
     * Specification</em>, sections 5.1.1 and 5.1.4 , for details.
     *
     * @param cls the {@code Class} object to be checked
     * @return the {@code boolean} value indicating whether objects of the
     * type {@code cls} can be assigned to objects of this class
     * @throws NullPointerException if the specified Class parameter is
     * null.
     * @since JDK1.1
     */
    public boolean isAssignableFrom(Class<?> cls) {
        throw new UnsupportedOperationException();
    }

    /**
     * Determines if the specified {@code Class} object represents an
     * interface type.
     *
     * @return {@code true} if this object represents an interface;
     * {@code false} otherwise.
     */
    public boolean isInterface() {
        return isInterface;
    }

    public void setIsInterface(boolean isInterface) {
        this.isInterface = isInterface;
    }

    /**
     * Determines if this {@code Class} object represents an array class.
     *
     * @return {@code true} if this object represents an array class;
     * {@code false} otherwise.
     * @since JDK1.1
     */
    public boolean isArray() {
        return isArray;
    }

    public void setIsArray(boolean isArray) {
        this.isArray = isArray;
    }

    /**
     * Determines if the specified {@code Class} object represents a
     * primitive type.
     *
     * <p> There are nine predefined {@code Class} objects to represent
     * the eight primitive types and void.  These are created by the Java
     * Virtual Machine, and have the same names as the primitive types that
     * they represent, namely {@code boolean}, {@code byte},
     * {@code char}, {@code short}, {@code int},
     * {@code long}, {@code float}, and {@code double}.
     *
     * <p> These objects may only be accessed via the following public static
     * final variables, and are the only {@code Class} objects for which
     * this method returns {@code true}.
     *
     * @return true if and only if this class represents a primitive type
     * @see java.lang.Boolean#TYPE
     * @see java.lang.Character#TYPE
     * @see java.lang.Byte#TYPE
     * @see java.lang.Short#TYPE
     * @see java.lang.Integer#TYPE
     * @see java.lang.Long#TYPE
     * @see java.lang.Float#TYPE
     * @see java.lang.Double#TYPE
     * @see java.lang.Void#TYPE
     * @since JDK1.1
     */
    public boolean isPrimitive() {
        return isPrimitive;
    }

    public void setIsPrimitive(boolean isPrimitive) {
        this.isPrimitive = isPrimitive;
    }

    /**
     * Returns true if this {@code Class} object represents an annotation
     * type.  Note that if this method returns true, {@link #isInterface()}
     * would also return true, as all annotation types are also interfaces.
     *
     * @return {@code true} if this class object represents an annotation
     * type; {@code false} otherwise
     * @since 1.5
     */
    public boolean isAnnotation() {
        return (getModifiers() & ANNOTATION) != 0;
    }

    /**
     * Returns {@code true} if this class is a synthetic class;
     * returns {@code false} otherwise.
     *
     * @return {@code true} if and only if this class is a synthetic class as
     * defined by the Java Language Specification.
     * @since 1.5
     */
    public boolean isSynthetic() {
        return (getModifiers() & SYNTHETIC) != 0;
    }

    /**
     * Returns the  name of the entity (class, interface, array class,
     * primitive type, or void) represented by this {@code Class} object,
     * as a {@code String}.
     *
     * <p> If this class object represents a reference type that is not an
     * array type then the binary name of the class is returned, as specified
     * by the Java Language Specification, Second Edition.
     *
     * <p> If this class object represents a primitive type or void, then the
     * name returned is a {@code String} equal to the Java language
     * keyword corresponding to the primitive type or void.
     *
     * <p> If this class object represents a class of arrays, then the internal
     * form of the name consists of the name of the element type preceded by
     * one or more '{@code [}' characters representing the depth of the array
     * nesting.  The encoding of element type names is as follows:
     *
     * <blockquote><table summary="Element types and encodings">
     * <tr><th> Element Type <th> &nbsp;&nbsp;&nbsp; <th> Encoding
     * <tr><td> boolean      <td> &nbsp;&nbsp;&nbsp; <td align=center> Z
     * <tr><td> byte         <td> &nbsp;&nbsp;&nbsp; <td align=center> B
     * <tr><td> char         <td> &nbsp;&nbsp;&nbsp; <td align=center> C
     * <tr><td> class or interface
     * <td> &nbsp;&nbsp;&nbsp; <td align=center> L<i>classname</i>;
     * <tr><td> double       <td> &nbsp;&nbsp;&nbsp; <td align=center> D
     * <tr><td> float        <td> &nbsp;&nbsp;&nbsp; <td align=center> F
     * <tr><td> int          <td> &nbsp;&nbsp;&nbsp; <td align=center> I
     * <tr><td> long         <td> &nbsp;&nbsp;&nbsp; <td align=center> J
     * <tr><td> short        <td> &nbsp;&nbsp;&nbsp; <td align=center> S
     * </table></blockquote>
     *
     * <p> The class or interface name <i>classname</i> is the binary name of
     * the class specified above.
     *
     * <p> Examples:
     * <blockquote><pre>
     * String.class.getName()
     *     returns "java.lang.String"
     * byte.class.getName()
     *     returns "byte"
     * (new Object[3]).getClass().getName()
     *     returns "[Ljava.lang.Object;"
     * (new int[3][4][5][6][7][8][9]).getClass().getName()
     *     returns "[[[[[[[I"
     * </pre></blockquote>
     *
     * @return the name of the class or interface
     * represented by this object.
     */
    public String getName() {
        return name;
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
    public TypeVariable<Class<T>>[] getTypeParameters() {
        if (getGenericSignature() != null) {
            return (TypeVariable<Class<T>>[]) getGenericInfo().getTypeParameters();
        } else {
            return (TypeVariable<Class<T>>[]) new TypeVariable[0];
        }
    }

    /**
     * Returns the {@code Class} representing the superclass of the entity
     * (class, interface, primitive type or void) represented by this
     * {@code Class}.  If this {@code Class} represents either the
     * {@code Object} class, an interface, a primitive type, or void, then
     * null is returned.  If this object represents an array class then the
     * {@code Class} object representing the {@code Object} class is
     * returned.
     *
     * @return the superclass of the class represented by this object.
     */
    public Class<? super T> getSuperclass() {
        return superclass;
    }

    public void setSuperclass(Class<? super T> superclass) {
        this.superclass = superclass;
    }

    /**
     * Returns the {@code Type} representing the direct superclass of
     * the entity (class, interface, primitive type or void) represented by
     * this {@code Class}.
     *
     * <p>If the superclass is a parameterized type, the {@code Type}
     * object returned must accurately reflect the actual type
     * parameters used in the source code. The parameterized type
     * representing the superclass is created if it had not been
     * created before. See the declaration of {@link
     * java.lang.reflect.ParameterizedType ParameterizedType} for the
     * semantics of the creation process for parameterized types.  If
     * this {@code Class} represents either the {@code Object}
     * class, an interface, a primitive type, or void, then null is
     * returned.  If this object represents an array class then the
     * {@code Class} object representing the {@code Object} class is
     * returned.
     *
     * @return the superclass of the class represented by this object
     * @since 1.5
     */
    public Type getGenericSuperclass() {
        if (getGenericSignature() != null) {
            // Historical irregularity:
            // Generic signature marks interfaces with superclass = Object
            // but this API returns null for interfaces
            if (isInterface()) {
                return null;
            }
            throw new UnsupportedOperationException();
            //return getGenericInfo().getSuperclass();
        } else {
            return getSuperclass();
        }
    }

    /**
     * Gets the package for this class.  The class loader of this class is used
     * to find the package.  If the class was loaded by the bootstrap class
     * loader the set of packages loaded from CLASSPATH is searched to find the
     * package of the class. Null is returned if no package object was created
     * by the class loader of this class.
     *
     * <p> Packages have attributes for versions and specifications only if the
     * information was defined in the manifests that accompany the classes, and
     * if the class loader created the package instance with the attributes
     * from the manifest.
     *
     * @return the package of the class, or null if no package
     * information is available from the archive or codebase.
     */
    public Package getPackage() {
        return myPackage;
    }

    public void setPackage(Package myPackage) {
        this.myPackage = myPackage;
    }

    /**
     * Determines the interfaces implemented by the class or interface
     * represented by this object.
     *
     * <p> If this object represents a class, the return value is an array
     * containing objects representing all interfaces implemented by the
     * class. The order of the interface objects in the array corresponds to
     * the order of the interface names in the {@code implements} clause
     * of the declaration of the class represented by this object. For
     * example, given the declaration:
     * <blockquote>
     * {@code class Shimmer implements FloorWax, DessertTopping { ... }}
     * </blockquote>
     * suppose the value of {@code s} is an instance of
     * {@code Shimmer}; the value of the expression:
     * <blockquote>
     * {@code s.getClass().getInterfaces()[0]}
     * </blockquote>
     * is the {@code Class} object that represents interface
     * {@code FloorWax}; and the value of:
     * <blockquote>
     * {@code s.getClass().getInterfaces()[1]}
     * </blockquote>
     * is the {@code Class} object that represents interface
     * {@code DessertTopping}.
     *
     * <p> If this object represents an interface, the array contains objects
     * representing all interfaces extended by the interface. The order of the
     * interface objects in the array corresponds to the order of the interface
     * names in the {@code extends} clause of the declaration of the
     * interface represented by this object.
     *
     * <p> If this object represents a class or interface that implements no
     * interfaces, the method returns an array of length 0.
     *
     * <p> If this object represents a primitive type or void, the method
     * returns an array of length 0.
     *
     * @return an array of interfaces implemented by this class.
     */
    public Class<?>[] getInterfaces() {
        return interfaces;
    }

    public void setInterfaces(Class<?>[] interfaces) {
        this.interfaces = interfaces;
    }

    /**
     * Returns the {@code Type}s representing the interfaces
     * directly implemented by the class or interface represented by
     * this object.
     *
     * <p>If a superinterface is a parameterized type, the
     * {@code Type} object returned for it must accurately reflect
     * the actual type parameters used in the source code. The
     * parameterized type representing each superinterface is created
     * if it had not been created before. See the declaration of
     * {@link java.lang.reflect.ParameterizedType ParameterizedType}
     * for the semantics of the creation process for parameterized
     * types.
     *
     * <p> If this object represents a class, the return value is an
     * array containing objects representing all interfaces
     * implemented by the class. The order of the interface objects in
     * the array corresponds to the order of the interface names in
     * the {@code implements} clause of the declaration of the class
     * represented by this object.  In the case of an array class, the
     * interfaces {@code Cloneable} and {@code Serializable} are
     * returned in that order.
     *
     * <p>If this object represents an interface, the array contains
     * objects representing all interfaces directly extended by the
     * interface.  The order of the interface objects in the array
     * corresponds to the order of the interface names in the
     * {@code extends} clause of the declaration of the interface
     * represented by this object.
     *
     * <p>If this object represents a class or interface that
     * implements no interfaces, the method returns an array of length
     * 0.
     *
     * <p>If this object represents a primitive type or void, the
     * method returns an array of length 0.
     *
     * @return an array of interfaces implemented by this class
     * generic superinterfaces refer to a parameterized type that cannot
     * be instantiated  for any reason
     * @since 1.5
     */
    public Type[] getGenericInterfaces() {
        if (getGenericSignature() != null) {
            //return getGenericInfo().getSuperInterfaces();
            throw new UnsupportedOperationException();
        } else {
            return getInterfaces();
        }
    }

    /**
     * Returns the {@code Class} representing the component type of an
     * array.  If this class does not represent an array class this method
     * returns null.
     *
     * @return the {@code Class} representing the component type of this
     * class if this class is an array
     * @see java.lang.reflect.Array
     * @since JDK1.1
     */
    public Class<?> getComponentType() {
        return componentType;
    }

    public void setComponentType(Class<?> componentType) {
        this.componentType = componentType;
    }

    /**
     * Returns the Java language modifiers for this class or interface, encoded
     * in an integer. The modifiers consist of the Java Virtual Machine's
     * constants for {@code public}, {@code protected},
     * {@code private}, {@code final}, {@code static},
     * {@code abstract} and {@code interface}; they should be decoded
     * using the methods of class {@code Modifier}.
     *
     * <p> If the underlying class is an array class, then its
     * {@code public}, {@code private} and {@code protected}
     * modifiers are the same as those of its component type.  If this
     * {@code Class} represents a primitive type or void, its
     * {@code public} modifier is always {@code true}, and its
     * {@code protected} and {@code private} modifiers are always
     * {@code false}. If this object represents an array class, a
     * primitive type or void, then its {@code final} modifier is always
     * {@code true} and its interface modifier is always
     * {@code false}. The values of its other modifiers are not determined
     * by this specification.
     *
     * <p> The modifier encodings are defined in <em>The Java Virtual Machine
     * Specification</em>, table 4.1.
     *
     * @return the {@code int} representing the modifiers for this class
     * @see java.lang.reflect.Modifier
     * @since JDK1.1
     */
    public int getModifiers() {
        return modifiers;
    }

    public void setModifiers(int modifiers) {
        this.modifiers = modifiers;
    }

    /**
     * If this {@code Class} object represents a local or anonymous
     * class within a method, returns a {@link
     * java.lang.reflect.Method Method} object representing the
     * immediately enclosing method of the underlying class. Returns
     * {@code null} otherwise.
     *
     * In particular, this method returns {@code null} if the underlying
     * class is a local or anonymous class immediately enclosed by a type
     * declaration, instance initializer or static initializer.
     *
     * @return the immediately enclosing method of the underlying class, if
     * that class is a local or anonymous class; otherwise {@code null}.
     * @since 1.5
     */
    public Method getEnclosingMethod() {
        return enclosingMethod;
    }

    public void setEnclosingMethod(Method enclosingMethod) {
        this.enclosingMethod = enclosingMethod;
    }

    private EnclosingMethodInfo getEnclosingMethodInfo() {
        Object[] enclosingInfo = enclosingMethodInfos;
        if (enclosingInfo == null) {
            return null;
        } else {
            return new EnclosingMethodInfo(enclosingInfo);
        }
    }

    public GenericDeclaration getGenericInfo() {
        return genericInfo;
    }

    public void setGenericInfo(GenericDeclaration genericInfo) {
        this.genericInfo = genericInfo;
    }

    public void addField(Field field) {
        for (Field field1 : fields) {
            if (field1.equals(field)) {
                return;
            }
        }

        fields.add(field);
    }

    public void addMethod(Method method) {
        for (Method method1 : methods) {
            if (method1.equals(method)) {
                return;
            }
        }

        methods.add(method);
    }

    public void addConstructor(Constructor constructor) {
        for (Constructor constructor1 : constructors) {
            if (constructor1.equals(constructor)) {
                return;
            }
        }
        constructors.add(constructor);
    }

    public static void purgeAllClasses() {
        CLASS_POOL.clear();
    }

    public int getLevel() {
        return level;
    }

    public static void loadModule(Module module) {
        for (Class aClass : module.getClassSet()) {
            //TODO all classes have already been registered when instanciating the module
            //TODO we should have more control here, even possible dynamic class loading
            //TODO how to manage conflicts ? It looks like JNDI.. oh no !
        }
    }

    public BaseReflector getReflector() {
        return reflector;
    }

    public void setReflector(BaseReflector reflector) {
        this.reflector = reflector;
    }

    private final static class EnclosingMethodInfo {
        private Class<?> enclosingClass;
        private String name;
        private String descriptor;

        private EnclosingMethodInfo(Object[] enclosingInfo) {
            if (enclosingInfo.length != 3) {
                throw new InternalError("Malformed enclosing method information");
            }
            try {
                // The array is expected to have three elements:

                // the immediately enclosing class
                enclosingClass = (Class<?>) enclosingInfo[0];
                assert (enclosingClass != null);

                // the immediately enclosing method or constructor's
                // name (can be null).
                name = (String) enclosingInfo[1];

                // the immediately enclosing method or constructor's
                // descriptor (null iff name is).
                descriptor = (String) enclosingInfo[2];
                assert ((name != null && descriptor != null) || name == descriptor);
            } catch (ClassCastException cce) {
                throw new InternalError("Invalid type in enclosing method information");
            }
        }

        boolean isPartial() {
            return enclosingClass == null || name == null || descriptor == null;
        }

        boolean isConstructor() { return !isPartial() && "<init>".equals(name); }

        boolean isMethod() { return !isPartial() && !isConstructor() && !"<clinit>".equals(name); }

        Class<?> getEnclosingClass() { return enclosingClass; }

        String getName() { return name; }

        String getDescriptor() { return descriptor; }
    }

    private static Class toClass(Type o) {
        if (o instanceof GenericArrayType) {
            throw new UnsupportedOperationException();
        }
        return (Class) o;
    }

    /**
     * If this {@code Class} object represents a local or anonymous
     * class within a constructor, returns a {@link
     * java.lang.reflect.Constructor Constructor} object representing
     * the immediately enclosing constructor of the underlying
     * class. Returns {@code null} otherwise.  In particular, this
     * method returns {@code null} if the underlying class is a local
     * or anonymous class immediately enclosed by a type declaration,
     * instance initializer or static initializer.
     *
     * @return the immediately enclosing constructor of the underlying class, if
     * that class is a local or anonymous class; otherwise {@code null}.
     * @since 1.5
     */
    public Constructor<?> getEnclosingConstructor() {
        return enclosingConstructor;
    }

    public void setEnclosingConstructor(Constructor<?> enclosingConstructor) {
        this.enclosingConstructor = enclosingConstructor;
    }

    /**
     * If the class or interface represented by this {@code Class} object
     * is a member of another class, returns the {@code Class} object
     * representing the class in which it was declared.  This method returns
     * null if this class or interface is not a member of any other class.  If
     * this {@code Class} object represents an array class, a primitive
     * type, or void,then this method returns null.
     *
     * @return the declaring class for this class
     * @since JDK1.1
     */
    public Class<?> getDeclaringClass() {
        return declaringClass;
    }

    public void setDeclaringClass(Class<?> declaringClass) {
        this.declaringClass = declaringClass;
    }

    /**
     * Returns the immediately enclosing class of the underlying
     * class.  If the underlying class is a top level class this
     * method returns {@code null}.
     *
     * @return the immediately enclosing class of the underlying class
     * @since 1.5
     */
    public Class<?> getEnclosingClass() {
        // There are five kinds of classes (or interfaces):
        // a) Top level classes
        // b) Nested classes (static member classes)
        // c) Inner classes (non-static member classes)
        // d) Local classes (named classes declared within a method)
        // e) Anonymous classes

        // JVM Spec 4.8.6: A class must have an EnclosingMethod
        // attribute if and only if it is a local class or an
        // anonymous class.
        EnclosingMethodInfo enclosingInfo = getEnclosingMethodInfo();

        if (enclosingInfo == null) {
            // This is a top level or a nested class or an inner class (a, b, or c)
            return getDeclaringClass();
        } else {
            Class<?> enclosingClass = enclosingInfo.getEnclosingClass();
            // This is a local class or an anonymous class (d or e)
            if (enclosingClass == this || enclosingClass == null) {
                throw new InternalError("Malformed enclosing method information");
            } else {
                return enclosingClass;
            }
        }
    }

    /**
     * Returns the simple name of the underlying class as given in the
     * source code. Returns an empty string if the underlying class is
     * anonymous.
     *
     * <p>The simple name of an array is the simple name of the
     * component type with "[]" appended.  In particular the simple
     * name of an array whose component type is anonymous is "[]".
     *
     * @return the simple name of the underlying class
     * @since 1.5
     */
    public String getSimpleName() {
        if (isArray()) {
            return getComponentType().getSimpleName() + "[]";
        }

        String simpleName = getSimpleBinaryName();
        if (simpleName == null) { // top level class
            simpleName = getName();
            return simpleName.substring(simpleName.lastIndexOf(".") + 1); // strip the package name
        }
        // According to JLS3 "Binary Compatibility" (13.1) the binary
        // name of non-package classes (not top level) is the binary
        // name of the immediately enclosing class followed by a '$' followed by:
        // (for nested and inner classes): the simple name.
        // (for local classes): 1 or more digits followed by the simple name.
        // (for anonymous classes): 1 or more digits.

        // Since getSimpleBinaryName() will strip the binary name of
        // the immediatly enclosing class, we are now looking at a
        // string that matches the regular expression "\$[0-9]*"
        // followed by a simple name (considering the simple of an
        // anonymous class to be the empty string).

        // Remove leading "\$[0-9]*" from the name
        int length = simpleName.length();
        if (length < 1 || simpleName.charAt(0) != '$') {
            throw new InternalError("Malformed class name");
        }
        int index = 1;
        while (index < length && isAsciiDigit(simpleName.charAt(index))) {
            index++;
        }
        // Eventually, this is the empty string iff this is an anonymous class
        return simpleName.substring(index);
    }

    /**
     * Character.isDigit answers {@code true} to some non-ascii
     * digits.  This one does not.
     */
    private static boolean isAsciiDigit(char c) {
        return '0' <= c && c <= '9';
    }

    /**
     * Returns the canonical name of the underlying class as
     * defined by the Java Language Specification.  Returns null if
     * the underlying class does not have a canonical name (i.e., if
     * it is a local or anonymous class or an array whose component
     * type does not have a canonical name).
     *
     * @return the canonical name of the underlying class if it exists, and
     * {@code null} otherwise.
     * @since 1.5
     */
    public String getCanonicalName() {
        if (isArray()) {
            String canonicalName = getComponentType().getCanonicalName();
            if (canonicalName != null) {
                return canonicalName + "[]";
            } else {
                return null;
            }
        }
        if (isLocalOrAnonymousClass()) {
            return null;
        }
        Class<?> enclosingClass = getEnclosingClass();
        if (enclosingClass == null) { // top level class
            return getName();
        } else {
            String enclosingName = enclosingClass.getCanonicalName();
            if (enclosingName == null) {
                return null;
            }
            return enclosingName + "." + getSimpleName();
        }
    }

    /**
     * Returns {@code true} if and only if the underlying class
     * is an anonymous class.
     *
     * @return {@code true} if and only if this class is an anonymous class.
     * @since 1.5
     */
    public boolean isAnonymousClass() {
        return "".equals(getSimpleName());
    }

    /**
     * Returns {@code true} if and only if the underlying class
     * is a local class.
     *
     * @return {@code true} if and only if this class is a local class.
     * @since 1.5
     */
    public boolean isLocalClass() {
        return isLocalOrAnonymousClass() && !isAnonymousClass();
    }

    /**
     * Returns {@code true} if and only if the underlying class
     * is a member class.
     *
     * @return {@code true} if and only if this class is a member class.
     * @since 1.5
     */
    public boolean isMemberClass() {
        return getSimpleBinaryName() != null && !isLocalOrAnonymousClass();
    }

    /**
     * Returns the "simple binary name" of the underlying class, i.e.,
     * the binary name without the leading enclosing class name.
     * Returns {@code null} if the underlying class is a top level
     * class.
     */
    private String getSimpleBinaryName() {
        Class<?> enclosingClass = getEnclosingClass();
        if (enclosingClass == null) // top level class
        {
            return null;
        }
        // Otherwise, strip the enclosing class' name
        try {
            return getName().substring(enclosingClass.getName().length());
        } catch (IndexOutOfBoundsException ex) {
            throw new InternalError("Malformed class name");
        }
    }

    /**
     * Returns {@code true} if this is a local class or an anonymous
     * class.  Returns {@code false} otherwise.
     */
    private boolean isLocalOrAnonymousClass() {
        // JVM Spec 4.8.6: A class must have an EnclosingMethod
        // attribute if and only if it is a local class or an
        // anonymous class.
        return getEnclosingMethodInfo() != null;
    }

    /**
     * Returns an array containing {@code Class} objects representing all
     * the public classes and interfaces that are members of the class
     * represented by this {@code Class} object.  This includes public
     * class and interface members inherited from superclasses and public class
     * and interface members declared by the class.  This method returns an
     * array of length 0 if this {@code Class} object has no public member
     * classes or interfaces.  This method also returns an array of length 0 if
     * this {@code Class} object represents a primitive type, an array
     * class, or void.
     *
     * @return the array of {@code Class} objects representing the public
     * members of this class
     * @throws SecurityException If a security manager, <i>s</i>, is present and any of the
     * following conditions is met:
     *
     * <ul>
     *
     * <li> invocation of
     * {@link SecurityManager#checkMemberAccess
     * s.checkMemberAccess(this, Member.PUBLIC)} method
     * denies access to the classes within this class
     *
     * <li> the caller's class loader is not the same as or an
     * ancestor of the class loader for the current class and
     * invocation of {@link SecurityManager#checkPackageAccess
     * s.checkPackageAccess()} denies access to the package
     * of this class
     *
     * </ul>
     * @since JDK1.1
     */
    public Class<?>[] getClasses() {
        return classes;
    }

    public void setClasses(Class<?>[] classes) {
        this.classes = classes;
    }

    /**
     * Returns an array containing {@code Field} objects reflecting all
     * the accessible public fields of the class or interface represented by
     * this {@code Class} object.  The elements in the array returned are
     * not sorted and are not in any particular order.  This method returns an
     * array of length 0 if the class or interface has no accessible public
     * fields, or if it represents an array class, a primitive type, or void.
     *
     * <p> Specifically, if this {@code Class} object represents a class,
     * this method returns the public fields of this class and of all its
     * superclasses.  If this {@code Class} object represents an
     * interface, this method returns the fields of this interface and of all
     * its superinterfaces.
     *
     * <p> The implicit length field for array class is not reflected by this
     * method. User code should use the methods of class {@code Array} to
     * manipulate arrays.
     *
     * <p> See <em>The Java Language Specification</em>, sections 8.2 and 8.3.
     *
     * @return the array of {@code Field} objects representing the
     * public fields
     * @throws SecurityException If a security manager, <i>s</i>, is present and any of the
     * following conditions is met:
     *
     * <ul>
     *
     * <li> invocation of
     * {@link SecurityManager#checkMemberAccess
     * s.checkMemberAccess(this, Member.PUBLIC)} denies
     * access to the fields within this class
     *
     * <li> the caller's class loader is not the same as or an
     * ancestor of the class loader for the current class and
     * invocation of {@link SecurityManager#checkPackageAccess
     * s.checkPackageAccess()} denies access to the package
     * of this class
     *
     * </ul>
     * @since JDK1.1
     */
    public Field[] getFields() throws SecurityException {
        return fields.toArray(new Field[fields.size()]);
    }

    public void setFields(List<Field> fields) {
        this.fields = fields;
    }

    /**
     * Returns an array containing {@code Method} objects reflecting all
     * the public <em>member</em> methods of the class or interface represented
     * by this {@code Class} object, including those declared by the class
     * or interface and those inherited from superclasses and
     * superinterfaces.  Array classes return all the (public) member methods
     * inherited from the {@code Object} class.  The elements in the array
     * returned are not sorted and are not in any particular order.  This
     * method returns an array of length 0 if this {@code Class} object
     * represents a class or interface that has no public member methods, or if
     * this {@code Class} object represents a primitive type or void.
     *
     * <p> The class initialization method {@code <clinit>} is not
     * included in the returned array. If the class declares multiple public
     * member methods with the same parameter types, they are all included in
     * the returned array.
     *
     * <p> See <em>The Java Language Specification</em>, sections 8.2 and 8.4.
     *
     * @return the array of {@code Method} objects representing the
     * public methods of this class
     * @throws SecurityException If a security manager, <i>s</i>, is present and any of the
     * following conditions is met:
     *
     * <ul>
     *
     * <li> invocation of
     * {@link SecurityManager#checkMemberAccess
     * s.checkMemberAccess(this, Member.PUBLIC)} denies
     * access to the methods within this class
     *
     * <li> the caller's class loader is not the same as or an
     * ancestor of the class loader for the current class and
     * invocation of {@link SecurityManager#checkPackageAccess
     * s.checkPackageAccess()} denies access to the package
     * of this class
     *
     * </ul>
     * @since JDK1.1
     */
    public List<Method> getMethods() {
        return methods;
    }

    public void setMethods(List<Method> methods) {
        this.methods = methods;
    }

    /**
     * Returns an array containing {@code Constructor} objects reflecting
     * all the public constructors of the class represented by this
     * {@code Class} object.  An array of length 0 is returned if the
     * class has no public constructors, or if the class is an array class, or
     * if the class reflects a primitive type or void.
     *
     * Note that while this method returns an array of {@code
     * Constructor<T>} objects (that is an array of constructors from
     * this class), the return type of this method is {@code
     * Constructor<?>[]} and <em>not</em> {@code Constructor<T>[]} as
     * might be expected.  This less informative return type is
     * necessary since after being returned from this method, the
     * array could be modified to hold {@code Constructor} objects for
     * different classes, which would violate the type guarantees of
     * {@code Constructor<T>[]}.
     *
     * @return the array of {@code Constructor} objects representing the
     * public constructors of this class
     * @throws SecurityException If a security manager, <i>s</i>, is present and any of the
     * following conditions is met:
     *
     * <ul>
     *
     * <li> invocation of
     * {@link SecurityManager#checkMemberAccess
     * s.checkMemberAccess(this, Member.PUBLIC)} denies
     * access to the constructors within this class
     *
     * <li> the caller's class loader is not the same as or an
     * ancestor of the class loader for the current class and
     * invocation of {@link SecurityManager#checkPackageAccess
     * s.checkPackageAccess()} denies access to the package
     * of this class
     *
     * </ul>
     * @since JDK1.1
     */
    public List<Constructor<?>> getConstructors() throws SecurityException {
        return constructors;
    }

    /**
     * Returns a {@code Field} object that reflects the specified public
     * member field of the class or interface represented by this
     * {@code Class} object. The {@code name} parameter is a
     * {@code String} specifying the simple name of the desired field.
     *
     * <p> The field to be reflected is determined by the algorithm that
     * follows.  Let C be the class represented by this object:
     * <OL>
     * <LI> If C declares a public field with the name specified, that is the
     * field to be reflected.</LI>
     * <LI> If no field was found in step 1 above, this algorithm is applied
     * recursively to each direct superinterface of C. The direct
     * superinterfaces are searched in the order they were declared.</LI>
     * <LI> If no field was found in steps 1 and 2 above, and C has a
     * superclass S, then this algorithm is invoked recursively upon S.
     * If C has no superclass, then a {@code NoSuchFieldException}
     * is thrown.</LI>
     * </OL>
     *
     * <p> See <em>The Java Language Specification</em>, sections 8.2 and 8.3.
     *
     * @param name the field name
     * @return the {@code Field} object of this class specified by
     * {@code name}
     * @throws NoSuchFieldException if a field with the specified name is
     * not found.
     * @throws NullPointerException if {@code name} is {@code null}
     * @throws SecurityException If a security manager, <i>s</i>, is present and any of the
     * following conditions is met:
     *
     * <ul>
     *
     * <li> invocation of
     * {@link SecurityManager#checkMemberAccess
     * s.checkMemberAccess(this, Member.PUBLIC)} denies
     * access to the field
     *
     * <li> the caller's class loader is not the same as or an
     * ancestor of the class loader for the current class and
     * invocation of {@link SecurityManager#checkPackageAccess
     * s.checkPackageAccess()} denies access to the package
     * of this class
     *
     * </ul>
     * @since JDK1.1
     */
    public Field getField(String name)
        throws NoSuchFieldException {
        for (Field field : fields) {
            if (field.getName().equals(name)) {
                return field;
            }
        }
        throw new NoSuchFieldException(name);
    }

    /**
     * Returns a {@code Method} object that reflects the specified public
     * member method of the class or interface represented by this
     * {@code Class} object. The {@code name} parameter is a
     * {@code String} specifying the simple name of the desired method. The
     * {@code parameterTypes} parameter is an array of {@code Class}
     * objects that identify the method's formal parameter types, in declared
     * order. If {@code parameterTypes} is {@code null}, it is
     * treated as if it were an empty array.
     *
     * <p> If the {@code name} is "{@code <init>};"or "{@code <clinit>}" a
     * {@code NoSuchMethodException} is raised. Otherwise, the method to
     * be reflected is determined by the algorithm that follows.  Let C be the
     * class represented by this object:
     * <OL>
     * <LI> C is searched for any <I>matching methods</I>. If no matching
     * method is found, the algorithm of step 1 is invoked recursively on
     * the superclass of C.</LI>
     * <LI> If no method was found in step 1 above, the superinterfaces of C
     * are searched for a matching method. If any such method is found, it
     * is reflected.</LI>
     * </OL>
     *
     * To find a matching method in a class C:&nbsp; If C declares exactly one
     * public method with the specified name and exactly the same formal
     * parameter types, that is the method reflected. If more than one such
     * method is found in C, and one of these methods has a return type that is
     * more specific than any of the others, that method is reflected;
     * otherwise one of the methods is chosen arbitrarily.
     *
     * <p>Note that there may be more than one matching method in a
     * class because while the Java language forbids a class to
     * declare multiple methods with the same signature but different
     * return types, the Java virtual machine does not.  This
     * increased flexibility in the virtual machine can be used to
     * implement various language features.  For example, covariant
     * returns can be implemented with {@linkplain
     * java.lang.reflect.Method#isBridge bridge methods}; the bridge
     * method and the method being overridden would have the same
     * signature but different return types.
     *
     * <p> See <em>The Java Language Specification</em>, sections 8.2 and 8.4.
     *
     * @param name the name of the method
     * @param parameterTypes the list of parameters
     * @return the {@code Method} object that matches the specified
     * {@code name} and {@code parameterTypes}
     * @throws NoSuchMethodException if a matching method is not found
     * or if the name is "&lt;init&gt;"or "&lt;clinit&gt;".
     * @throws NullPointerException if {@code name} is {@code null}
     * @throws SecurityException If a security manager, <i>s</i>, is present and any of the
     * following conditions is met:
     *
     * <ul>
     *
     * <li> invocation of
     * {@link SecurityManager#checkMemberAccess
     * s.checkMemberAccess(this, Member.PUBLIC)} denies
     * access to the method
     *
     * <li> the caller's class loader is not the same as or an
     * ancestor of the class loader for the current class and
     * invocation of {@link SecurityManager#checkPackageAccess
     * s.checkPackageAccess()} denies access to the package
     * of this class
     *
     * </ul>
     * @since JDK1.1
     */
    public Method getMethod(String name, Class<?>... parameterTypes)
        throws NoSuchMethodException {
        for (Method method : methods) {
            if (method.getName().equals(name)
                && equalParams(method.getParameterTypes(), parameterTypes)) {
                return method;
            }
        }
        throw new NoSuchMethodException(getName() + "." + name + argumentTypesToString(parameterTypes));
    }

    private boolean equalParams(Class<?>[] parameterTypes, Class<?>[] parameterTypes1) {
        if (parameterTypes == null && parameterTypes1 != null) {
            return false;
        }
        if (parameterTypes1 == null && parameterTypes != null) {
            return false;
        }
        if (parameterTypes.length != parameterTypes1.length) {
            return false;
        }

        for (int i = 0; i < parameterTypes.length; i++) {
            if (parameterTypes[i].equals(parameterTypes1[i])) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns a {@code Constructor} object that reflects the specified
     * public constructor of the class represented by this {@code Class}
     * object. The {@code parameterTypes} parameter is an array of
     * {@code Class} objects that identify the constructor's formal
     * parameter types, in declared order.
     *
     * If this {@code Class} object represents an inner class
     * declared in a non-static context, the formal parameter types
     * include the explicit enclosing instance as the first parameter.
     *
     * <p> The constructor to reflect is the public constructor of the class
     * represented by this {@code Class} object whose formal parameter
     * types match those specified by {@code parameterTypes}.
     *
     * @param parameterTypes the parameter array
     * @return the {@code Constructor} object of the public constructor that
     * matches the specified {@code parameterTypes}
     * @throws NoSuchMethodException if a matching method is not found.
     * @throws SecurityException If a security manager, <i>s</i>, is present and any of the
     * following conditions is met:
     *
     * <ul>
     *
     * <li> invocation of
     * {@link SecurityManager#checkMemberAccess
     * s.checkMemberAccess(this, Member.PUBLIC)} denies
     * access to the constructor
     *
     * <li> the caller's class loader is not the same as or an
     * ancestor of the class loader for the current class and
     * invocation of {@link SecurityManager#checkPackageAccess
     * s.checkPackageAccess()} denies access to the package
     * of this class
     *
     * </ul>
     * @since JDK1.1
     */
    public Constructor<T> getConstructor(Class<?>... parameterTypes)
        throws NoSuchMethodException {
        for (Constructor constructor : constructors) {
            if (constructor.getName().equals(name)
                && equalParams(constructor.getParameterTypes(), parameterTypes)) {
                return constructor;
            }
        }
        throw new NoSuchMethodException(getName() + "." + name + argumentTypesToString(parameterTypes));
    }

    /**
     * Returns an array of {@code Class} objects reflecting all the
     * classes and interfaces declared as members of the class represented by
     * this {@code Class} object. This includes public, protected, default
     * (package) access, and private classes and interfaces declared by the
     * class, but excludes inherited classes and interfaces.  This method
     * returns an array of length 0 if the class declares no classes or
     * interfaces as members, or if this {@code Class} object represents a
     * primitive type, an array class, or void.
     *
     * @return the array of {@code Class} objects representing all the
     * declared members of this class
     * @throws SecurityException If a security manager, <i>s</i>, is present and any of the
     * following conditions is met:
     *
     * <ul>
     *
     * <li> invocation of
     * {@link SecurityManager#checkMemberAccess
     * s.checkMemberAccess(this, Member.DECLARED)} denies
     * access to the declared classes within this class
     *
     * <li> the caller's class loader is not the same as or an
     * ancestor of the class loader for the current class and
     * invocation of {@link SecurityManager#checkPackageAccess
     * s.checkPackageAccess()} denies access to the package
     * of this class
     *
     * </ul>
     * @since JDK1.1
     */
    public Class<?>[] getDeclaredClasses() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns an array of {@code Field} objects reflecting all the fields
     * declared by the class or interface represented by this
     * {@code Class} object. This includes public, protected, default
     * (package) access, and private fields, but excludes inherited fields.
     * The elements in the array returned are not sorted and are not in any
     * particular order.  This method returns an array of length 0 if the class
     * or interface declares no fields, or if this {@code Class} object
     * represents a primitive type, an array class, or void.
     *
     * <p> See <em>The Java Language Specification</em>, sections 8.2 and 8.3.
     *
     * @return the array of {@code Field} objects representing all the
     * declared fields of this class
     * @throws SecurityException If a security manager, <i>s</i>, is present and any of the
     * following conditions is met:
     *
     * <ul>
     *
     * <li> invocation of
     * {@link SecurityManager#checkMemberAccess
     * s.checkMemberAccess(this, Member.DECLARED)} denies
     * access to the declared fields within this class
     *
     * <li> the caller's class loader is not the same as or an
     * ancestor of the class loader for the current class and
     * invocation of {@link SecurityManager#checkPackageAccess
     * s.checkPackageAccess()} denies access to the package
     * of this class
     *
     * </ul>
     * @since JDK1.1
     */
    public Field[] getDeclaredFields() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns an array of {@code Method} objects reflecting all the
     * methods declared by the class or interface represented by this
     * {@code Class} object. This includes public, protected, default
     * (package) access, and private methods, but excludes inherited methods.
     * The elements in the array returned are not sorted and are not in any
     * particular order.  This method returns an array of length 0 if the class
     * or interface declares no methods, or if this {@code Class} object
     * represents a primitive type, an array class, or void.  The class
     * initialization method {@code <clinit>} is not included in the
     * returned array. If the class declares multiple public member methods
     * with the same parameter types, they are all included in the returned
     * array.
     *
     * <p> See <em>The Java Language Specification</em>, section 8.2.
     *
     * @return the array of {@code Method} objects representing all the
     * declared methods of this class
     * @throws SecurityException If a security manager, <i>s</i>, is present and any of the
     * following conditions is met:
     *
     * <ul>
     *
     * <li> invocation of
     * {@link SecurityManager#checkMemberAccess
     * s.checkMemberAccess(this, Member.DECLARED)} denies
     * access to the declared methods within this class
     *
     * <li> the caller's class loader is not the same as or an
     * ancestor of the class loader for the current class and
     * invocation of {@link SecurityManager#checkPackageAccess
     * s.checkPackageAccess()} denies access to the package
     * of this class
     *
     * </ul>
     * @since JDK1.1
     */
    public Method[] getDeclaredMethods() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns an array of {@code Constructor} objects reflecting all the
     * constructors declared by the class represented by this
     * {@code Class} object. These are public, protected, default
     * (package) access, and private constructors.  The elements in the array
     * returned are not sorted and are not in any particular order.  If the
     * class has a default constructor, it is included in the returned array.
     * This method returns an array of length 0 if this {@code Class}
     * object represents an interface, a primitive type, an array class, or
     * void.
     *
     * <p> See <em>The Java Language Specification</em>, section 8.2.
     *
     * @return the array of {@code Constructor} objects representing all the
     * declared constructors of this class
     * @throws SecurityException If a security manager, <i>s</i>, is present and any of the
     * following conditions is met:
     *
     * <ul>
     *
     * <li> invocation of
     * {@link SecurityManager#checkMemberAccess
     * s.checkMemberAccess(this, Member.DECLARED)} denies
     * access to the declared constructors within this class
     *
     * <li> the caller's class loader is not the same as or an
     * ancestor of the class loader for the current class and
     * invocation of {@link SecurityManager#checkPackageAccess
     * s.checkPackageAccess()} denies access to the package
     * of this class
     *
     * </ul>
     * @since JDK1.1
     */
    public Constructor<?>[] getDeclaredConstructors() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns a {@code Field} object that reflects the specified declared
     * field of the class or interface represented by this {@code Class}
     * object. The {@code name} parameter is a {@code String} that
     * specifies the simple name of the desired field.  Note that this method
     * will not reflect the {@code length} field of an array class.
     *
     * @param name the name of the field
     * @return the {@code Field} object for the specified field in this
     * class
     * @throws NoSuchFieldException if a field with the specified name is
     * not found.
     * @throws NullPointerException if {@code name} is {@code null}
     * @throws SecurityException If a security manager, <i>s</i>, is present and any of the
     * following conditions is met:
     *
     * <ul>
     *
     * <li> invocation of
     * {@link SecurityManager#checkMemberAccess
     * s.checkMemberAccess(this, Member.DECLARED)} denies
     * access to the declared field
     *
     * <li> the caller's class loader is not the same as or an
     * ancestor of the class loader for the current class and
     * invocation of {@link SecurityManager#checkPackageAccess
     * s.checkPackageAccess()} denies access to the package
     * of this class
     *
     * </ul>
     * @since JDK1.1
     */
    public Field getDeclaredField(String name)
        throws NoSuchFieldException {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns a {@code Method} object that reflects the specified
     * declared method of the class or interface represented by this
     * {@code Class} object. The {@code name} parameter is a
     * {@code String} that specifies the simple name of the desired
     * method, and the {@code parameterTypes} parameter is an array of
     * {@code Class} objects that identify the method's formal parameter
     * types, in declared order.  If more than one method with the same
     * parameter types is declared in a class, and one of these methods has a
     * return type that is more specific than any of the others, that method is
     * returned; otherwise one of the methods is chosen arbitrarily.  If the
     * name is "&lt;init&gt;"or "&lt;clinit&gt;" a {@code NoSuchMethodException}
     * is raised.
     *
     * @param name the name of the method
     * @param parameterTypes the parameter array
     * @return the {@code Method} object for the method of this class
     * matching the specified name and parameters
     * @throws NoSuchMethodException if a matching method is not found.
     * @throws NullPointerException if {@code name} is {@code null}
     * @throws SecurityException If a security manager, <i>s</i>, is present and any of the
     * following conditions is met:
     *
     * <ul>
     *
     * <li> invocation of
     * {@link SecurityManager#checkMemberAccess
     * s.checkMemberAccess(this, Member.DECLARED)} denies
     * access to the declared method
     *
     * <li> the caller's class loader is not the same as or an
     * ancestor of the class loader for the current class and
     * invocation of {@link SecurityManager#checkPackageAccess
     * s.checkPackageAccess()} denies access to the package
     * of this class
     *
     * </ul>
     * @since JDK1.1
     */
    public Method getDeclaredMethod(String name, Class<?>... parameterTypes)
        throws NoSuchMethodException {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns a {@code Constructor} object that reflects the specified
     * constructor of the class or interface represented by this
     * {@code Class} object.  The {@code parameterTypes} parameter is
     * an array of {@code Class} objects that identify the constructor's
     * formal parameter types, in declared order.
     *
     * If this {@code Class} object represents an inner class
     * declared in a non-static context, the formal parameter types
     * include the explicit enclosing instance as the first parameter.
     *
     * @param parameterTypes the parameter array
     * @return The {@code Constructor} object for the constructor with the
     * specified parameter list
     * @throws NoSuchMethodException if a matching method is not found.
     * @throws SecurityException If a security manager, <i>s</i>, is present and any of the
     * following conditions is met:
     *
     * <ul>
     *
     * <li> invocation of
     * {@link SecurityManager#checkMemberAccess
     * s.checkMemberAccess(this, Member.DECLARED)} denies
     * access to the declared constructor
     *
     * <li> the caller's class loader is not the same as or an
     * ancestor of the class loader for the current class and
     * invocation of {@link SecurityManager#checkPackageAccess
     * s.checkPackageAccess()} denies access to the package
     * of this class
     *
     * </ul>
     * @since JDK1.1
     */
    public Constructor<T> getDeclaredConstructor(Class<?>... parameterTypes)
        throws NoSuchMethodException {
        throw new UnsupportedOperationException();
    }

    /*
     * Return the Virtual Machine's Class object for the named
     * primitive type.
     */
    static Class getPrimitiveClass(String name) {
        throw new UnsupportedOperationException();
    }

    // Generic signature handling
    private String getGenericSignature() {
        throw new UnsupportedOperationException();
    }

    private static boolean arrayContentsEq(Object[] a1, Object[] a2) {
        if (a1 == null) {
            return a2 == null || a2.length == 0;
        }

        if (a2 == null) {
            return a1.length == 0;
        }

        if (a1.length != a2.length) {
            return false;
        }

        for (int i = 0; i < a1.length; i++) {
            if (a1[i] != a2[i]) {
                return false;
            }
        }

        return true;
    }

    private static String argumentTypesToString(Class[] argTypes) {
        StringBuilder buf = new StringBuilder();
        buf.append("(");
        if (argTypes != null) {
            for (int i = 0; i < argTypes.length; i++) {
                if (i > 0) {
                    buf.append(", ");
                }
                Class c = argTypes[i];
                buf.append((c == null) ? "null" : c.getName());
            }
        }
        buf.append(")");
        return buf.toString();
    }

    /**
     * Returns true if and only if this class was declared as an enum in the
     * source code.
     *
     * @return true if and only if this class was declared as an enum in the
     * source code
     * @since 1.5
     */
    public boolean isEnum() {
        // An enum must both directly extend java.lang.Enum and have
        // the ENUM bit set; classes for specialized enum constants
        // don't do the former.
        return (this.getModifiers() & ENUM) != 0 &&
            this.getSuperclass().equals(java.lang.Enum.class);
    }

    /**
     * Casts this {@code Class} object to represent a subclass of the class
     * represented by the specified class object.  Checks that that the cast
     * is valid, and throws a {@code ClassCastException} if it is not.  If
     * this method succeeds, it always returns a reference to this class object.
     *
     * <p>This method is useful when a client needs to "narrow" the type of
     * a {@code Class} object to pass it to an API that restricts the
     * {@code Class} objects that it is willing to accept.  A cast would
     * generate a compile-time warning, as the correctness of the cast
     * could not be checked at runtime (because generic types are implemented
     * by erasure).
     *
     * @return this {@code Class} object, cast to represent a subclass of
     * the specified class object.
     * @throws ClassCastException if this {@code Class} object does not
     * represent a subclass of the specified class (here "subclass" includes
     * the class itself).
     * @since 1.5
     */
    public <U> Class<? extends U> asSubclass(Class<U> clazz) {
        if (clazz.isAssignableFrom(this)) {
            return (Class<? extends U>) this;
        } else {
            throw new ClassCastException(this.toString());
        }
    }

    /**
     * @throws NullPointerException {@inheritDoc}
     * @since 1.5
     */
    public <A extends org.reflection_no_reflection.Annotation> A getAnnotation(Class<A> annotationClass) {
        if (annotationClass == null) {
            throw new NullPointerException();
        }

        for (org.reflection_no_reflection.Annotation annotation : annotationList) {
            if (annotation.annotationType().equals(annotationClass)) {
                return (A) annotation;
            }
        }

        return null;
    }

    /**
     * @throws NullPointerException {@inheritDoc}
     * @since 1.5
     */
    public boolean isAnnotationPresent(
        Class<? extends org.reflection_no_reflection.Annotation> annotationClass) {
        if (annotationClass == null) {
            throw new NullPointerException();
        }

        return getAnnotation(annotationClass) != null;
    }

    private static Annotation[] EMPTY_ANNOTATIONS_ARRAY = new Annotation[0];

    public <T extends Annotation> T getAnnotation(java.lang.Class<T> annotationClass) {
        //TODO only works at runtime, doesn't make sense at compile time.
        return null;
    }

    /**
     * @since 1.5
     */
    public Annotation[] getAnnotations() {
        return annotationList.toArray(EMPTY_ANNOTATIONS_ARRAY);
    }

    public void setAnnotations(List<org.reflection_no_reflection.Annotation> annotations) {
        this.annotationList = annotations;
    }

    /**
     * @since 1.5
     */
    public Annotation[] getDeclaredAnnotations() {
        throw new UnsupportedOperationException();
    }

    // Annotation types cache their internal (AnnotationType) form

    private AnnotationType annotationType;

    void setAnnotationType(AnnotationType type) {
        annotationType = type;
    }

    AnnotationType getAnnotationType() {
        return annotationType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass() || o.getClass() == java.lang.Class.class) {
            return false;
        }

        final String oName;
        if (o instanceof java.lang.Class) {
            oName = ((java.lang.Class) o).getName();
        } else {
            oName = ((Class) o).getName();
        }

        return name.equals(oName);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
