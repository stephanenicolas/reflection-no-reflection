package org.reflection_no_reflection.plugin

import com.android.build.gradle.AppPlugin
import com.android.build.gradle.LibraryPlugin
import com.darylteo.gradle.javassist.tasks.TransformationTask
import com.github.stephanenicolas.morpheus.AbstractMorpheusPlugin
import javassist.build.IClassTransformer
import org.gradle.api.Project
import org.gradle.api.file.FileCollection
import org.gradle.api.plugins.PluginCollection
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.compile.JavaCompile
import org.reflection_no_reflection.weaver.NoReflectionWeaver

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

      def compiledAnnotationProcessorDirName = "${project.buildDir}/intermediates/annotation-processor-only-classes/";
      def compiledAnnotationProcessorDir = project.file(compiledAnnotationProcessorDirName)
      def copyCompiledAnnotationProcessorTask = "copyCompiledAnnotationProcessorTask"
      project.task(copyCompiledAnnotationProcessorTask, type: Copy) {
        description = "Copy compiled annotation processor"
        from(compiledAnnotationProcessorDir)
        into("${javaCompile.destinationDir.path}")
        outputs.upToDateWhen {
          false
        }
        eachFile {
          log.debug(LOG_TAG, "Copied into build:" + it.path)
        }
      }

      def generatedAnnotationProcessorDirName = "${project.buildDir}/intermediates/annotation-processor-only/";
      def generatedAnnotationProcessorDir = project.file(generatedAnnotationProcessorDirName)
      boolean canCreateAnnotationCompileDir = generatedAnnotationProcessorDir.exists() ? true : generatedAnnotationProcessorDir.mkdirs()
      if (!canCreateAnnotationCompileDir) {
        throw new RuntimeException("Impossible to create target dir for annotation processor only.");
      }

      JavaCompile javaCompileBeforeWeave = project.task('compileJavaBeforeWeave', type: JavaCompile)
      javaCompileBeforeWeave.options.compilerArgs += javaCompile.options.compilerArgs
      javaCompileBeforeWeave.options.bootClasspath = javaCompile.options.bootClasspath
      javaCompileBeforeWeave.options.listFiles = javaCompile.options.listFiles
      javaCompileBeforeWeave.options.debug = javaCompile.options.debug
      javaCompileBeforeWeave.options.debugOptions = javaCompile.options.debugOptions
      javaCompileBeforeWeave.options.encoding = javaCompile.options.encoding
      javaCompileBeforeWeave.source = javaCompile.source
      javaCompileBeforeWeave.destinationDir = javaCompile.destinationDir
      javaCompileBeforeWeave.classpath = javaCompile.classpath
      javaCompileBeforeWeave.options.compilerArgs += '-proc:none'

      JavaCompile javaCompileAfterWeave = project.task('compileJavaAfterWeave', type: JavaCompile)
      javaCompileAfterWeave.options.compilerArgs += javaCompile.options.compilerArgs
      javaCompileAfterWeave.options.bootClasspath = javaCompile.options.bootClasspath
      javaCompileAfterWeave.options.debug = javaCompile.options.debug
      javaCompileAfterWeave.options.debugOptions = javaCompile.options.debugOptions
      javaCompileAfterWeave.options.encoding = javaCompile.options.encoding
      javaCompileAfterWeave.source = project.fileTree(generatedAnnotationProcessorDirName)
      javaCompileAfterWeave.destinationDir = project.file("${project.buildDir}/intermediates/annotation-processor-only-classes/")
      javaCompileAfterWeave.classpath = javaCompile.classpath
      javaCompileAfterWeave.classpath += project.files("${project.buildDir}/classes/main/")
      javaCompileAfterWeave.options.compilerArgs += '-proc:none'

      println "destdir " + project.files(generatedAnnotationProcessorDirName).asPath
      javaCompile.options.compilerArgs += '-proc:only'
      javaCompile.options.compilerArgs += ['-s', project.file(generatedAnnotationProcessorDirName)]

      project.tasks.getByName(transformTask).mustRunAfter(javaCompileBeforeWeave)
      project.tasks.getByName('compileJava').dependsOn(transformTask, javaCompileBeforeWeave)
      project.tasks.getByName('compileJavaAfterWeave').dependsOn('compileJava')
      project.tasks.getByName(copyTransformedTask).mustRunAfter(project.tasks.getByName('compileJava'))
      project.tasks.getByName(copyCompiledAnnotationProcessorTask).mustRunAfter(project.tasks.getByName('compileJavaAfterWeave'))
      project.tasks.getByName('classes').dependsOn(copyTransformedTask, 'compileJavaAfterWeave', copyCompiledAnnotationProcessorTask)
    }
  }

  @Override
  public IClassTransformer[] getTransformers(Project project) {
    return new NoReflectionWeaver(project.noreflection.debug, project.noreflection.annotationClasses);
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
