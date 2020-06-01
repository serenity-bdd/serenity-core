package net.serenitybdd.screenplay;

import net.serenitybdd.core.steps.Instrumented;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.util.EnvironmentVariables;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static java.util.Arrays.stream;
import static net.thucydides.core.ThucydidesSystemProperty.MANUAL_TASK_INSTRUMENTATION;

public class InstrumentedTask {

    public static <T extends Performable> T of(T task) {
        EnvironmentVariables environmentVariables = Injectors.getInjector().getInstance(EnvironmentVariables.class);

        if (MANUAL_TASK_INSTRUMENTATION.booleanFrom(environmentVariables, false)) {
            return task;
        } else if(isInstrumented(task) || !shouldInstrument(task)) {
            return task;
        }
        return (T) instrumentedCopyOf(task, task.getClass());
    }

    static <T extends Performable> boolean shouldInstrument(T task) {
        Optional<Method> performAs = stream(task.getClass().getMethods())
                .filter(method -> method.getName().equals("performAs"))
                .findFirst();

        return performAs.isPresent() && defaultConstructorPresentFor(task.getClass());
    }

    private static boolean defaultConstructorPresentFor(Class<? extends Performable> taskClass) {

        return findAllConstructorsIn(taskClass).stream()
                         .anyMatch( constructor -> constructor.getParameterCount() == 0 );
    }

    private static List<Constructor<?>> findAllConstructorsIn(Class<? extends Performable> taskClass) {
        List<Constructor<?>> allConstructors = new ArrayList<>();

        allConstructors.addAll(Arrays.asList(taskClass.getConstructors()));
        allConstructors.addAll(Arrays.asList(taskClass.getDeclaredConstructors()));

        return allConstructors;
    }

    private static Performable instrumentedCopyOf(Performable task, Class taskClass) {

        Performable instrumentedTask = null;
        try {
            instrumentedTask = (Performable) Instrumented.instanceOf(taskClass).newInstance();
        } catch(IllegalArgumentException missingDefaultConstructor) {
            throw new TaskInstantiationException("Could not instantiate "
                                                 + taskClass
                                                + ". If you are not instrumenting a Task class explicitly you need to give the class a default constructor."
                                                + "A task class cannot be instrumented if it is final (so if you are writing in Kotlin, make sure the task class is 'open'.");
        }
        CopyNonNullProperties.from(task).to(instrumentedTask);
        return instrumentedTask;
    }

    static boolean isInstrumented(Performable task) {

        try {
            return task.getClass().getSimpleName().contains("ByteBuddy");
        } catch(NullPointerException ignore) {
            throw new TaskInstantiationException("Your Task class must have a public constructor.");
        }
    }
}
