package net.thucydides.core.steps;

import java.lang.reflect.Method;

import static net.thucydides.core.util.Inflector.inflection;
import static org.apache.commons.lang3.StringUtils.uncapitalize;

public class StepNamer {

    public static String nameFor(Method method, Object[] args) {

        if (isScreenplayPerformAs(method)) {
            return screenplayStepWithArgs(method, args[0]);
        }

        if ((args == null) || (args.length == 0)) {
            return method.getName();
        } else {
            return testNameWithArguments(method, args);
        }
    }

    private static String screenplayStepWithArgs(Method method, Object actor) {
        String taskName = inflection().humanize(inflection().underscore(method.getDeclaringClass().getSimpleName()));
        return inflection().capitalize(actor.toString()) + " " + uncapitalize(taskName);
    }

    private static boolean isScreenplayPerformAs(Method method) {
        return method.getName().equals("performAs") && method.getParameterCount() == 1;
    }

    private static String testNameWithArguments(final Method method,
                                         final Object[] args) {
        StringBuilder testName = new StringBuilder(method.getName());
        testName.append(": ");

        boolean isFirst = true;
        for (Object arg : args) {
            if (!isFirst) {
                testName.append(", ");
            }
            testName.append(StepArgumentWriter.readableFormOf(arg));
            isFirst = false;
        }
        return testName.toString();
    }

}
