package net.thucydides.core;

import net.serenitybdd.core.Serenity;
import net.serenitybdd.core.SessionMap;
import net.thucydides.model.steps.StepListener;
import org.openqa.selenium.firefox.FirefoxProfile;

/**
 * @deprecated Use Serenity class instead
 *
 * A utility class that provides services to initialize web testing and reporting-related fields in arbitrary objects.
 */

@Deprecated
public class Thucydides {

    public static void initialize(final Object testCase) {
        Serenity.initialize(testCase);
    }

    /**
     * Initialize Thucydides-related fields in the specified object.
     * This includes managed WebDriver instances,
     *
     * @param testCase any object (testcase or other) containing injectable Thucydides components
     */
    public static void initializeWithNoStepListener(final Object testCase) {
        Serenity.initializeWithNoStepListener(testCase);
    }

    /**
     * Instantiates the @ManagedPages-annotated Pages instance using current WebDriver.
     *
     * @param testCase any object (testcase or other) containing injectable Thucydides components
     */
    public static void injectScenarioStepsInto(final Object testCase) {
        Serenity.injectScenarioStepsInto(testCase);
    }

    /**
     * Indicate that the test run using this object is finished, and reports can be generated.
     */
    public static void done() {
        Serenity.done();
    }

    public static String getCurrentSessionID() {
        return Serenity.getCurrentSessionID();
    }

    public static StepListener getStepListener() {
        return Serenity.getStepListener();
    }

    public static void initializeTestSession() {
        Serenity.initializeTestSession();
    }

    public static SessionMap<Object, Object> getCurrentSession() {
        return Serenity.getCurrentSession();
    }

    public static void pendingStep(String reason) {
        Serenity.pendingStep(reason);
    }

    public static void ignoredStep(String reason) {
        Serenity.ignoredStep(reason);
    }

    public static void takeScreenshot() {
        Serenity.takeScreenshot();
    }

    /**
     * @return The current working directory name is used as a default project key if no other key is provided.
     */
    public static String getDefaultProjectKey() {
        return Serenity.getDefaultProjectKey();
    }

    public static void useFirefoxProfile(FirefoxProfile profile) {
        Serenity.useFirefoxProfile(profile);
    }

    public static FirefoxProfile getFirefoxProfile() {
        return Serenity.getFirefoxProfile();
    }

}
