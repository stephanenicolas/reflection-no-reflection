package org.reflection_no_reflection.weaver;

import com.github.stephanenicolas.morpheus.AbstractMorpheusPlugin;
import javassist.build.IClassTransformer;
import org.gradle.api.Project;

/**
 * @author SNI
 */
public class NoReflectionPlugin extends AbstractMorpheusPlugin {

  @Override
  public IClassTransformer[] getTransformers(Project project) {
    return new NoReflectionPlugin(project.noreflection.debug);
  }

  @Override
  protected void configure(Project project) {
  }

  @Override
  protected Class getPluginExtension() {
    NoReflectionPluginExtension
  }

  @Override
  protected String getExtension() {
    "noreflection"
  }

  @Override
  public boolean skipVariant(def variant) {
    return false
  }
}
