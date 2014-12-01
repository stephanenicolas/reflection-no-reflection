package org.reflection_no_reflection.weaver;

import com.github.stephanenicolas.morpheus.commons.JavassistUtils;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.Modifier;
import javassist.NotFoundException;
import javassist.build.IClassTransformer;
import javassist.build.JavassistBuildException;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

/**
 * A class transformer to inject logging byte code for all life cycle methods.
 *
 * @author SNI
 */
@Slf4j
public class NoReflectionWeaver implements IClassTransformer {

    private boolean debug;
    private List<String> annotationClasses;

    public NoReflectionWeaver(boolean debug, @NonNull List<String> annotationClasses) {
        this.debug = debug;
        this.annotationClasses = annotationClasses;
    }

    @Override
    public boolean shouldTransform(CtClass candidateClass) throws JavassistBuildException {
        try {
            for (String annotationClass : annotationClasses) {
                if (!JavassistUtils.getAllInjectedFieldsForAnnotation(candidateClass, (Class<? extends Annotation>)Class.forName(annotationClass)).isEmpty()) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            logMoreIfDebug("Should transform filter failed for class " + candidateClass.getName(), e);
            throw new JavassistBuildException(e);
        }
    }

    @Override
    public void applyTransformations(CtClass classToTransform) throws JavassistBuildException {
        String classToTransformName = classToTransform.getName();
        try {
            log.info("Transforming " + classToTransformName);
            List<CtField> fieldList = new ArrayList<CtField>();
            for (String annotationClass : annotationClasses) {
                fieldList.addAll(JavassistUtils.getAllInjectedFieldsForAnnotation(classToTransform, (Class<? extends Annotation>)Class.forName(annotationClass)));
            }
            createPublicAccessorMethods(fieldList);
        } catch (Exception e) {
            logMoreIfDebug("Transformation failed for class " + classToTransformName, e);
            throw new JavassistBuildException(e);
        }
        log.info("Transformation successful for " + classToTransformName);
    }

    private void createPublicAccessorMethods(List<CtField> fieldList) throws CannotCompileException, NotFoundException {
        for (CtField ctField : fieldList) {
            if ((ctField.getModifiers() & Modifier.PUBLIC) == 0 ) {
                boolean isStatic = (ctField.getModifiers() & Modifier.STATIC) != 0;
                String nonStaticAssignStatement = "this." + ctField.getName() + " = " + ctField.getName() + ";";
                String staticAssignStatement = ctField.getDeclaringClass().getName() + "." + ctField.getName() + " = " + ctField.getName() + ";";
                String assignStatement = isStatic ? staticAssignStatement : nonStaticAssignStatement;
                String staticModifierString = "static";
                String modifierString = isStatic ? staticModifierString : "" ;
                String src = "public " + modifierString + " void __access_" + ctField.getName() + "("
                    + ctField.getType().getName() + " " + ctField.getName() + ") {\n"
                    + assignStatement
                    + "}\n";
                System.out.println(src);
                CtMethod setterMethod = CtNewMethod.make(src, ctField.getDeclaringClass());
                ctField.getDeclaringClass().addMethod(setterMethod);
                String nonStaticAccessStatement = "return this." + ctField.getName() + ";";
                String staticAccessStatement = "return " + ctField.getDeclaringClass().getName() + "." + ctField.getName() + ";";
                String accessStatement = isStatic ? staticAccessStatement : nonStaticAccessStatement;
                CtMethod getterMethod = CtNewMethod.make("public " + modifierString + " " + ctField.getType().getName() +" __access_" + ctField.getName() + "() {\n"
                                                             + accessStatement
                                                             + "}\n", ctField.getDeclaringClass());
                ctField.getDeclaringClass().addMethod(getterMethod);
            }
        }
    }

    private void logMoreIfDebug(String message, Exception e) {
        if (debug) {
            log.debug(message, e);
        } else {
            log.info(message);
        }
    }
}
