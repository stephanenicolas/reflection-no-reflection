package org.reflection_no_reflection.weaver

import com.android.build.gradle.AppPlugin
import com.android.build.gradle.LibraryPlugin
import com.darylteo.gradle.javassist.tasks.TransformationTask;
import com.github.stephanenicolas.morpheus.AbstractMorpheusPlugin;
import javassist.build.IClassTransformer;
import org.gradle.api.Project
import org.gradle.api.file.FileCollection
import org.gradle.api.plugins.PluginCollection
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.compile.JavaCompile

import java.util.logging.Logger;

/**
 * @author SNI
 */
public class NoReflectionPlugin extends AbstractMorpheusPlugin {
  @Override
  public void apply(Project project) {
    def extension = getExtension()
    def pluginExtension = getPluginExtension()
    if (extension && pluginExtension) {
      project.extensions.create(extension, pluginExtension)
    }

    final def log = project.logger
    final String LOG_TAG = this.getClass().getName()

    final def variants

    configure(project)

    log.debug(LOG_TAG, "Transforming classes.")

    JavaCompile javaCompile = project.tasks.findByName('compileJava')
    FileCollection classpathFileCollection = javaCompile.classpath

    for (IClassTransformer transformer : getTransformers(project)) {
      String transformerClassName = transformer.getClass().getSimpleName()
      String transformationDir = "${project.buildDir}/intermediates/transformations/transform${transformerClassName}"

      def transformTask = "transform${transformerClassName}"
      project.task(transformTask, type: TransformationTask) {
        description = "Transform a file using ${transformerClassName}"
        destinationDir = project.file(transformationDir)
        from("${javaCompile.destinationDir.path}")
        transformation = transformer
        classpath = classpathFileCollection
        outputs.upToDateWhen {
          false
        }
        eachFile {
          log.debug(LOG_TAG, "Transformed:" + it.path)
        }
      }

      project.tasks.getByName(transformTask).mustRunAfter(javaCompile)
      def copyTransformedTask = "copyTransformed${transformerClassName}"
      project.task(copyTransformedTask, type: Copy) {
        description = "Copy transformed file to build dir for ${transformerClassName}"
        from(transformationDir)
        into("${javaCompile.destinationDir.path}")
        outputs.upToDateWhen {
          false
        }
        eachFile {
          log.debug(LOG_TAG, "Copied into build:" + it.path)
        }
      }
      project.tasks.getByName(copyTransformedTask).mustRunAfter(project.tasks.getByName(transformTask))
      project.tasks.getByName('install').dependsOn(transformTask, copyTransformedTask)
      project.tasks.getByName('test').dependsOn(transformTask, copyTransformedTask)
    }
  }

  @Override
  public IClassTransformer[] getTransformers(Project project) {
    return new NoReflectionWeaver(project.noreflection.debug);
  }

  @Override
  protected void configure(Project project) {}

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

  @Override
  protected void ensureProjectIsAndroidAppOrLib(PluginCollection<AppPlugin> hasApp, PluginCollection<LibraryPlugin> hasLib) {
    //do nothing
  }
}
