package net.thucydides.core.steps;


import net.thucydides.model.domain.TestOutcome;
import net.thucydides.model.domain.TestStep;
import net.thucydides.model.domain.stacktrace.FailureCause;
import org.openqa.selenium.WebDriver;

import java.util.List;
import java.util.Optional;

/**
 * Represents a class that monitors step results and can report on test outcomes.
 */
public interface StepPublisher {

    /**
     * A step listener should be able to return a set of test results at the end of the test run.
     */
    List<TestOutcome> getTestOutcomes();

    /**
     * Used to update the webdriver driver for screenshots if a listener is reused between scenarios.
     */
    void setDriver(final WebDriver driver);

    /**
     * The currently-used WebDriver instance for these tests.
     */
    WebDriver getDriver();

    /**
     *  Should return true if a step failure has been logged.
     *  We need to share this information if multiple step libraries are used.
     */
    boolean aStepHasFailed();

    Optional<TestStep> firstFailingStep();
    /**
     * If a test failed, what was the error.
     */
    FailureCause getTestFailureCause();
}
