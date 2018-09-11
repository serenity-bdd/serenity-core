package net.serenitybdd.screenplay;

import com.rits.cloning.Cloner;
import net.serenitybdd.core.steps.Instrumented;
import net.thucydides.core.annotations.Step;

import java.lang.reflect.Method;
import java.util.Optional;

import static java.util.Arrays.stream;

public class InstrumentedTask {

    public static <T extends Performable> T of(T task) {
        if (isInstrumented(task) || !shouldInstrument(task)) {
            return task;
        }
        return (T) instrumentedCopyOf(task, task.getClass());
    }

    private static <T extends Performable> boolean shouldInstrument(T task) {

        Optional<Method> performAs = stream(task.getClass().getMethods())
                .filter(method -> method.getName().equals("performAs"))
                .findFirst();

        if (performAs.isPresent() && (performAs.get().getAnnotation(Step.class) != null)) {
            return true;
        }

        return false;
    }

    private static Performable instrumentedCopyOf(Performable task, Class taskClass) {

        Performable instrumentedTask = null;
        try {
            instrumentedTask = (Performable) Instrumented.instanceOf(taskClass).newInstance();
        } catch(IllegalArgumentException missingDefaultConstructor) {
            throw new TaskInstantiationException("Could not instantiate the class " + taskClass + " - does it have a default constructor?");
        }
        Cloner cloner = new Cloner();
        cloner.copyPropertiesOfInheritedClass(task, instrumentedTask);
        return instrumentedTask;
    }

    private static boolean isInstrumented(Performable task) {
        return task.getClass().getSimpleName().contains("EnhancerByCGLIB");
    }
}
