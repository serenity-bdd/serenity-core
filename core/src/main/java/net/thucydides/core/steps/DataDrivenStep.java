package net.thucydides.core.steps;

/**
 * Keep track of whether the current thread is running a data-driven step or not.
 */
public class DataDrivenStep {
    private static ThreadLocal<Boolean> dataDrivenStepInProgress = new ThreadLocal<Boolean>();

    public static void startDataDrivenStep() {
        dataDrivenStepInProgress.set(true);
    }

    public static void endDataDrivenStep() {
        dataDrivenStepInProgress.set(false);
    }

    public static boolean inProgress() {
        if (dataDrivenStepInProgress.get() == null) {
            return false;
        } else {
            return dataDrivenStepInProgress.get();
        }
    }
}
