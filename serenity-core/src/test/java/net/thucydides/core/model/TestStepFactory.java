package net.thucydides.core.model;

import net.thucydides.model.domain.TestResult;
import net.thucydides.model.domain.TestStep;
import net.thucydides.model.screenshots.ScreenshotAndHtmlSource;

import java.io.File;

import static net.thucydides.model.domain.TestResult.*;

public class TestStepFactory {
    public static TestStep forASuccessfulTestStepCalled(String description) {
        return createNewTestStep(description, SUCCESS);
    }
    
    public static TestStep forASuccessfulNestedTestStepCalled(String description) {
        return createNewNestedTestSteps(description, SUCCESS);
    }

    public static TestStep forABrokenTestStepCalled(String description, Throwable assertionError) {
        return createNewTestStep(description, FAILURE, assertionError);
    }

    public static TestStep forASkippedTestStepCalled(String description) {
        return createNewTestStep(description, SKIPPED);
    }

    public static TestStep forAnIgnoredTestStepCalled(String description) {
        return createNewTestStep(description, IGNORED);
    }
    
    public static TestStep forAPendingTestStepCalled(String description) {
        return createNewTestStep(description, PENDING);
    }

    public static TestStep createNewTestStep(String description, TestResult result, Throwable assertionError) {
        TestStep step = new TestStep(description);
        step.failedWith(assertionError);
        return step;
    }

    public static TestStep createNewTestStep(String description, TestResult result) {
        TestStep step = new TestStep(description);
        step.addScreenshot(new ScreenshotAndHtmlSource(new File(description + ".png"), new File(description + ".html")));
        step.setResult(result);
        step.setDuration(100);
        return step;

    }
    

    public static TestStep createNewNestedTestSteps(String description, TestResult result) {
        TestStep step =  new TestStep(description);
        TestStep child1 = new TestStep(description);
        TestStep child2 = new TestStep(description);

        child1.addScreenshot(new ScreenshotAndHtmlSource(new File(description + ".png"), new File(description + ".html")));
        child1.setResult(result);
        child1.setDuration(100);

        child2.addScreenshot(new ScreenshotAndHtmlSource(new File(description + ".png"), new File(description + ".html")));
        child2.setResult(result);
        child2.setDuration(100);

        step.addChildStep(child1);
        step.addChildStep(child2);

        return step;
    }

}
