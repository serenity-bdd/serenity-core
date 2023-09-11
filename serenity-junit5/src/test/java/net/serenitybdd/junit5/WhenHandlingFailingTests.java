package net.serenitybdd.junit5;

import net.serenitybdd.annotations.Step;
import net.serenitybdd.annotations.Steps;
import net.thucydides.core.steps.StepEventBus;
import net.thucydides.core.webdriver.WebDriverFactory;
import net.thucydides.model.domain.TestOutcome;
import net.thucydides.model.domain.TestResult;
import net.thucydides.model.environment.MockEnvironmentVariables;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;


class WhenHandlingFailingTests {

    MockEnvironmentVariables environmentVariables = new MockEnvironmentVariables();
    File temporaryDirectory;
    WebDriverFactory webDriverFactory;

    @BeforeEach
    void setup() throws Exception {
        temporaryDirectory = Files.createTempDirectory("tmp").toFile();
        temporaryDirectory.deleteOnExit();
        MockitoAnnotations.initMocks(this);
        environmentVariables = new MockEnvironmentVariables();
        webDriverFactory = new WebDriverFactory(environmentVariables);
        StepEventBus.getParallelEventBus().clear();
        StepEventBus.setNoCleanupForStickyBuses(true);
    }

    static class SomeSteps {
        @Step
        void myFailingStep() {
            throw new IllegalStateException();
        }

        @Step
        void myUnexpectedlyFailingStep() {
            throw new UnknownError();
        }
    }

    @ExtendWith(SerenityJUnit5Extension.class)
    static class ATestWithAnExpectedExceptionInAStep {
        @Steps
        SomeSteps mysteps;

        @Test
        void shouldThrowAnIllegalStateException() {
            assertThrows(IllegalStateException.class, mysteps::myFailingStep);
        }
    }

    @Test
    void shouldReportTestsWithAnExpectedExceptionInAStepAsPassing() {
        runTestForClass(ATestWithAnExpectedExceptionInAStep.class);
        assertEquals(TestResult.SUCCESS, getTestOutcomeFor("shouldThrowAnIllegalStateException").getResult());
    }

    @ExtendWith(SerenityJUnit5Extension.class)
    static class ATestWithAnExpectedException {
        @Steps
        SomeSteps mysteps;

        @Test
        void shouldThrowAnIllegalStateException() {
            assertThrows(IllegalStateException.class, () -> { mysteps.myFailingStep(); });
        }
    }

    @Test
    void shouldReportTestsWithAnExpectedExceptionAsPassing() {
        runTestForClass(ATestWithAnExpectedException.class);
        assertEquals(TestResult.SUCCESS, getTestOutcomeFor("shouldThrowAnIllegalStateException").getResult());
    }

    void runTestForClass(Class<?> testClass) {
        LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
                .selectors(selectClass(testClass))
                .build();
        LauncherFactory.create().execute(request);
    }

    static TestOutcome getTestOutcomeFor(String testName) {
        return StepEventBus.eventBusForTest(testName).get().getBaseStepListener().getTestOutcomes().get(0);
    }
}
