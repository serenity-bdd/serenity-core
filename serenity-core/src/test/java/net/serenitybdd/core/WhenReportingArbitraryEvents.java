package net.serenitybdd.core;

import net.thucydides.core.steps.BaseStepListener;
import net.thucydides.core.steps.StepEventBus;
import org.assertj.core.util.Files;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

public class WhenReportingArbitraryEvents {

    File tempDir = Files.newTemporaryFile();

    @Before
    public void setupEventBus() {
        Serenity.initializeTestSession();
        StepEventBus.getEventBus().registerListener(new BaseStepListener(tempDir));
        StepEventBus.getEventBus().testStarted("Sample test");
    }

    @After
    public void cleanup() {
        StepEventBus.getEventBus().clear();
    }

    @Test
    public void reportedEventsShouldAppearAsNewSteps() {
        Serenity.reportThat("something happened",
                () -> {}
        );
        assertThat(stepName()).isEqualTo("something happened");
    }

    @Test
    public void failingAssertionsShouldBeReported() {
        boolean exceptionWasThrownAsExpected = false;
        try {
            Serenity.reportThat("something bad happened",
                    () -> assertThat(true).isFalse()
            );
        } catch(AssertionError expectedException) {
            exceptionWasThrownAsExpected = true;
            assertThat(stepName()).isEqualTo("something bad happened");
        }

        assertThat(exceptionWasThrownAsExpected).isTrue();
    }

    private String stepName() {
        return StepEventBus.getEventBus().getBaseStepListener().latestTestOutcome().get().getTestSteps().get(0).getDescription();
    }
}
