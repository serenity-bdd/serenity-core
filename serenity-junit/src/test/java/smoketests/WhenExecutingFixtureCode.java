package smoketests;

import com.google.inject.Guice;
import com.google.inject.Injector;
import net.serenitybdd.core.Serenity;
import net.serenitybdd.core.pages.PageObject;
import net.serenitybdd.junit.runners.AbstractTestStepRunnerTest;
import net.serenitybdd.junit.runners.SerenityRunner;
import net.thucydides.core.annotations.DefaultUrl;
import net.thucydides.core.annotations.Managed;
import net.thucydides.core.annotations.Step;
import net.thucydides.core.annotations.Steps;
import net.thucydides.core.guice.webdriver.WebDriverModule;
import net.thucydides.core.steps.StepEventBus;
import net.thucydides.junit.rules.DisableThucydidesHistoryRule;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.InitializationError;
import org.mockito.MockitoAnnotations;
import org.openqa.selenium.WebDriver;

import static org.assertj.core.api.Assertions.assertThat;

public class WhenExecutingFixtureCode extends AbstractTestStepRunnerTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Rule
    public DisableThucydidesHistoryRule disableThucydidesHistoryRule = new DisableThucydidesHistoryRule();

    Injector injector;

    @Before
    public void createATestableDriverFactory() throws Exception {
        MockitoAnnotations.initMocks(this);
        injector = Guice.createInjector(new WebDriverModule());
        StepEventBus.getEventBus().clear();
    }

    @DefaultUrl("classpath:static-site/index.html")
    public static class SimplePage extends PageObject {
    }

    public static class StepLibary {

        SimplePage simplePage;

        @Step
        public void openHomePage() {
            simplePage.open();
        }

        @Step
        public void titleShouldBe(String expectedTitle) {
            assertThat(simplePage.getTitle()).isEqualTo(expectedTitle);
        }

        @Step
        public void recordTitle() {
            pageTitleInTheAfterMethod = simplePage.getTitle();
        }

        @Step
        public void recordTitleAfterReactivation() {
            Serenity.webdriver().reenableDrivers();
            pageTitleInTheAfterMethod = simplePage.getTitle();
        }
    }

    public static String pageTitleInTheAfterMethod;

    public static class ATestWithAFailingStepAndAReactivatedDriver {

        @Managed(driver = "htmlunit")
        WebDriver driver;

        @Steps
        StepLibary stepLibary;

        @Test
        public void whenAStepFails() {
            stepLibary.openHomePage();
            stepLibary.titleShouldBe("Wrong title");
            stepLibary.recordTitleAfterReactivation();
        }
    }


    public static class ATestWithAFailingStepWhereWeReactivateTheDriver {

        @Managed(driver = "htmlunit")
        WebDriver driver;

        @Steps
        StepLibary stepLibary;

        @Test
        public void whenAStepFails() {
            stepLibary.openHomePage();
            stepLibary.titleShouldBe("Wrong title");
        }

        @After
        public void theDriverShouldBeDisabled() {
            pageTitleInTheAfterMethod = stepLibary.simplePage.getTitle();
        }
    }

    public static class ATestWithAFailingStepWhereWeReactivateTheDriverAndUsingAStepMethod {

        @Managed(driver = "htmlunit", options = "--headless")
        WebDriver driver;

        @Steps
        StepLibary stepLibary;

        @Test
        public void whenAStepFails() {
            stepLibary.openHomePage();
            stepLibary.titleShouldBe("Wrong title");
        }

        @After
        public void theDriverShouldBeDisabled() {
            Serenity.webdriver().reenableDrivers();
            stepLibary.recordTitle();
        }
    }

    @Test
    public void the_driver_can_be_manually_enabled_after_a_step_fails() throws InitializationError {

        SerenityRunner runner = new SerenityRunner(ATestWithAFailingStepWhereWeReactivateTheDriver.class, injector);
        runner.run(new RunNotifier());

        assertThat(pageTitleInTheAfterMethod).isEqualTo("Thucydides Test Site");
    }


    @Test
    public void the_driver_can_be_manually_enabled_via_a_step_method_after_a_step_fails() throws InitializationError {

        SerenityRunner runner = new SerenityRunner(ATestWithAFailingStepWhereWeReactivateTheDriverAndUsingAStepMethod.class, injector);
        runner.run(new RunNotifier());

        assertThat(pageTitleInTheAfterMethod).isEqualTo("Thucydides Test Site");
    }

}
