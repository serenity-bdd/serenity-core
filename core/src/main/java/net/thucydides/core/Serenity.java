package net.thucydides.core;

import net.thucydides.core.steps.StepListener;
import org.openqa.selenium.firefox.FirefoxProfile;

/**
 * A utility class that provides services to initialize web testing and reporting-related fields in arbitrary objects.
 * It wraps calls to the legacy Thucydides class.
 */

public class Serenity {

    public static void initialize(final Object testCase) {
        Thucydides.initialize(testCase);
    }

    /**
     * Initialize Thucydides-related fields in the specified object.
     * This includes managed WebDriver instances,
     *
     * @param testCase any object (testcase or other) containing injectable Thucydides components
     */
    public static void initializeWithNoStepListener(final Object testCase) {
        Thucydides.initializeWithNoStepListener(testCase);
    }

    /**
     * Instantiates the @ManagedPages-annotated Pages instance using current WebDriver.
     *
     * @param testCase any object (testcase or other) containing injectable Thucydides components
     */
    public static void injectScenarioStepsInto(final Object testCase) {
        Thucydides.injectScenarioStepsInto(testCase);
    }

    /**
     * Indicate that the test run using this object is finished, and reports can be generated.
     */
    public static void done() {
        Thucydides.done();
    }

    public static String getCurrentSessionID() {
        return Thucydides.getCurrentSessionID();
    }

    public static StepListener getStepListener() {
        return Thucydides.getStepListener();
    }

    public static void initializeTestSession() {
        Thucydides.initializeTestSession();
    }

    public static Object sessionVariableCalled(Object key) {
        return Thucydides.getCurrentSession().get(key);
    }

    public static SessionVariableSetter setSessionVariable(Object key) {
        return new SessionVariableSetter(key);
    }



    public static SessionMap<Object, Object> getCurrentSession() {
        return Thucydides.getCurrentSession();
    }

    public static void pendingStep(String reason) {
        Thucydides.pendingStep(reason);
    }

    public static void ignoredStep(String reason) {
        Thucydides.ignoredStep(reason);
    }

    public static void takeScreenshot() {
        Thucydides.takeScreenshot();
    }

    /**
     * @return The current working directory name is used as a default project key if no other key is provided.
     */
    public static String getDefaultProjectKey() {
        return Thucydides.getDefaultProjectKey();
    }

    public static void useFirefoxProfile(FirefoxProfile profile) {
        Thucydides.useFirefoxProfile(profile);
    }

    public static FirefoxProfile getFirefoxProfile() {
        return Thucydides.getFirefoxProfile();
    }

    public static class SessionVariableSetter {
        final Object key;

        public SessionVariableSetter(Object key) {
            this.key = key;
        }

        public void to(Object value) {
            Thucydides.getCurrentSession().put(key, value);
        }
    }
}
