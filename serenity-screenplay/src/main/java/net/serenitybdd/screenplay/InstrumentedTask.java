package net.serenitybdd.screenplay;

import com.rits.cloning.Cloner;
import net.serenitybdd.core.steps.Instrumented;

public class InstrumentedTask {

    public static <T extends Performable> T of(T task) {
        if (isInstrumented(task)) {
            return task;
        }
        return (T) instrumentedCopyOf(task, task.getClass());
    }

    private static Performable instrumentedCopyOf(Performable task, Class taskClass) {
        Performable instrumentedTask = (Performable) Instrumented.instanceOf(taskClass).newInstance();
        Cloner cloner = new Cloner();
        cloner.copyPropertiesOfInheritedClass(task, instrumentedTask);
        return instrumentedTask;
    }

    private static boolean isInstrumented(Performable task) {
        return task.getClass().getSimpleName().contains("EnhancerByCGLIB");
    }
}
