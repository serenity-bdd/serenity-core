package net.serenitybdd.screenplay;

import net.serenitybdd.screenplay.shopping.BitesTheBanana;
import net.serenitybdd.screenplay.shopping.ChewsTheBanana;
import net.serenitybdd.screenplay.shopping.PeelABanana;
import net.thucydides.core.steps.BaseStepListener;
import net.thucydides.core.steps.StepEventBus;
import net.thucydides.model.domain.TestResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Files;

public class WhenPerformingCompositeTasks {

    private File temporaryDirectory;
    private BaseStepListener listener;

    @BeforeEach
    public void setup() throws Exception {
        temporaryDirectory = Files.createTempDirectory("tmp").toFile();
        temporaryDirectory.deleteOnExit();
        listener = new BaseStepListener(temporaryDirectory);

        StepEventBus.getEventBus().clear();
        StepEventBus.getEventBus().registerListener(listener);
        StepEventBus.getEventBus().testStarted("some test");
    }

    @Test
    public void aCompositeTaskShouldExecuteAllOfTheNestedTasks() {
        // Given
        Performable eatsABanana = new PeelABanana().then(new BitesTheBanana()).then(new ChewsTheBanana());

        // When
        Actor.named("Eddie").attemptsTo(eatsABanana);

        // Then
        testPassed();
        testOutcomeContainsStep("Eddie peels a banana");
        testOutcomeContainsStep("Eddie bites the banana");
        testOutcomeContainsStep("Eddie chews the banana");
    }

    private void testPassed() {
        Assertions.assertEquals(TestResult.SUCCESS, listener.latestTestOutcome().get().getResult());
    }

    private void testOutcomeContainsStep(String expectedDescription) {
        Assertions.assertTrue(listener.latestTestOutcome().get().getTestSteps().stream()
                .anyMatch(step -> step.getDescription().equals(expectedDescription)));
    }

    private void testOutcomeContainsNoSteps() {
        Assertions.assertTrue(listener.latestTestOutcome().get().getTestSteps().isEmpty());
    }
}
