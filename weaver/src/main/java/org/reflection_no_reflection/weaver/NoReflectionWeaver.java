package org.reflection_no_reflection.weaver;

import com.github.stephanenicolas.afterburner.AfterBurner;
import com.github.stephanenicolas.afterburner.exception.AfterBurnerImpossibleException;
import com.github.stephanenicolas.morpheus.commons.JavassistUtils;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.build.IClassTransformer;
import javassist.build.JavassistBuildException;
import javassist.bytecode.AccessFlag;
import lombok.extern.slf4j.Slf4j;

import static com.github.stephanenicolas.morpheus.commons.JavassistUtils.isActivity;
import static com.github.stephanenicolas.morpheus.commons.JavassistUtils.isApplication;
import static com.github.stephanenicolas.morpheus.commons.JavassistUtils.isBroadCastReceiver;
import static com.github.stephanenicolas.morpheus.commons.JavassistUtils.isContentProvider;
import static com.github.stephanenicolas.morpheus.commons.JavassistUtils.isFragment;
import static com.github.stephanenicolas.morpheus.commons.JavassistUtils.isService;
import static com.github.stephanenicolas.morpheus.commons.JavassistUtils.isSupportFragment;
import static com.github.stephanenicolas.morpheus.commons.JavassistUtils.isView;

/**
 * A class transformer to inject logging byte code for all life cycle methods.
 *
 * @author SNI
 */
@Slf4j
public class NoReflectionWeaver implements IClassTransformer {

  private AfterBurner afterBurner = new AfterBurner();
  private boolean debug;

  public NoReflectionWeaver(boolean debug) {
    this.debug = debug;
  }

  @Override
  public boolean shouldTransform(CtClass candidateClass) throws JavassistBuildException {
    try {
      return !JavassistUtils.getAllInjectedFieldsForAnnotation(candidateClass, javax.inject.Inject.class).isEmpty() || !JavassistUtils.getAllInjectedFieldsForAnnotation(candidateClass, com.google.inject.Inject.class).isEmpty();
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
      fieldList.addAll(JavassistUtils.getAllInjectedFieldsForAnnotation(classToTransform, javax.inject.Inject.class));
      fieldList.addAll(JavassistUtils.getAllInjectedFieldsForAnnotation(classToTransform, com.google.inject.Inject.class));
      createPublicAccessorMethods(fieldList);
    } catch (Exception e) {
      logMoreIfDebug("Transformation failed for class " + classToTransformName, e);
      throw new JavassistBuildException(e);
    }
    log.info("Transformation successful for " + classToTransformName);
  }

  private void createPublicAccessorMethods(List<CtField> fieldList) {
    //TODO
  }

  private void logMoreIfDebug(String message, Exception e) {
    if (debug) {
      log.debug(message, e);
    } else {
      log.info(message);
    }
  }
}
