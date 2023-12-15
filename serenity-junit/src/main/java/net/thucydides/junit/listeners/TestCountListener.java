package net.thucydides.junit.listeners;

import net.thucydides.model.logging.LoggingLevel;
import net.thucydides.model.statistics.TestCount;
import net.thucydides.model.steps.StepListenerAdapter;
import net.thucydides.model.util.EnvironmentVariables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static net.thucydides.model.ThucydidesSystemProperty.SERENITY_DISPLAY_TEST_NUMBERS;

public class TestCountListener extends StepListenerAdapter {

    private final Logger logger;
    private final EnvironmentVariables environmentVariables;
    private final TestCount testCount;
    private final boolean reportTestCount;

    protected TestCountListener(EnvironmentVariables environmentVariables, Logger logger, TestCount testCount) {
        this.logger = logger;
        this.environmentVariables = environmentVariables;
        this.testCount = testCount;
        this.reportTestCount = SERENITY_DISPLAY_TEST_NUMBERS.booleanFrom(environmentVariables,false);
    }

    public TestCountListener(EnvironmentVariables environmentVariables, TestCount testCount) {
        this(environmentVariables, LoggerFactory.getLogger(""), testCount);
    }

    protected Logger getLogger() {
        return logger;
    }


    public void testStarted(String description) {
        int currentTestCount = testCount.getNextTest();
        if (reportTestCount && LoggingLevel.definedIn(environmentVariables).isAtLeast(LoggingLevel.VERBOSE)) {
            getLogger().info("TEST NUMBER: {}", currentTestCount);
        }
    }

    @Override
    public void testStarted(String description, String id) {
        testStarted(description);
    }

}
