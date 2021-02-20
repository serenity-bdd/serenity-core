package net.serenitybdd.rest.utils;


import net.thucydides.core.guice.Injectors;
import net.thucydides.core.util.EnvironmentVariables;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static net.thucydides.core.ThucydidesSystemProperty.SERENITY_DISABLE_REST_CALLS_AFTER_FAILURES;
import static net.thucydides.core.steps.StepEventBus.getEventBus;


/**
 * User: YamStranger
 * Date: 4/6/16
 * Time: 7:51 AM
 */
public class RestExecutionHelper {
    private static Set<String> classWithDryCleanEnabled =
            Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());

    public static boolean restCallsAreDisabled() {

        EnvironmentVariables environmentVariables = Injectors.getInjector().getInstance(EnvironmentVariables.class);

        if (!SERENITY_DISABLE_REST_CALLS_AFTER_FAILURES.booleanFrom(environmentVariables, true)) {
            return false;
        }

        return isEnabledDryCleanOnlyForCurrentClass()
                || getEventBus().isDryRun()
                || getEventBus().currentTestIsSuspended();
    }


    /**
     * Using of this method allowed to enable DryClean mode for particular class.
     * During executing of methods restCallsAreDisabled and restCallsAreDisabled stacktrace will be
     * analysed to find out if this class included.
     * Be careful - until even for one class enabled dry clean - performance will be decreased.
     *
     * @param object some object where methods will be executed, basically it should be test
     */
    public static void enableDryRunForClass(final Object object) {
        enableDryRunForClass(object.getClass());
    }

    public static void enableDryRunForClass(final Class classOfObject) {
        classWithDryCleanEnabled.add(classOfObject.getName());
    }

    public static void disableDryRunForClass(final Object object) {
        enableDryRunForClass(object.getClass());
    }

    public static void disableDryRunForClass(final Class classOfObject) {
        classWithDryCleanEnabled.remove(classOfObject.getName());
    }

    private static boolean isEnabledDryCleanOnlyForCurrentClass() {
        for (final StackTraceElement element : Thread.currentThread().getStackTrace()) {
            if (classWithDryCleanEnabled.contains(element.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
