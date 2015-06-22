package butterknife;

import org.reflection_no_reflection.Class;
import org.reflection_no_reflection.Field;
import org.reflection_no_reflection.runtime.Module;

public final class ButterKnife {
  private ButterKnife() {
    throw new AssertionError("No instances.");
  }

  public static void bind(Object target) {


    try {

        //Class.loadModule(new org.reflection_no_reflection.runtime.Module());
      System.err.println("class: " + target.getClass().getCanonicalName());
      final Class<?> classFoo = Class.forName(target.getClass().getCanonicalName());
      for(Field field  : classFoo.getDeclaredFields()) {
        System.err.println("fields: " + field.getDeclaredAnnotations());
      }

    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }

    System.err.println("Binding activity: " + target);
  }
}
