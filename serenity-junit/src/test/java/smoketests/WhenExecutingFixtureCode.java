package smoketests;

import net.serenitybdd.annotations.DefaultUrl;
import net.serenitybdd.annotations.Managed;
import net.serenitybdd.annotations.Step;
import net.serenitybdd.annotations.Steps;
import net.serenitybdd.core.Serenity;
import net.serenitybdd.core.pages.PageObject;
import net.serenitybdd.junit.runners.AbstractTestStepRunnerTest;
import net.thucydides.core.steps.StepEventBus;
import net.thucydides.junit.rules.DisableThucydidesHistoryRule;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.openqa.selenium.WebDriver;

import static org.assertj.core.api.Assertions.assertThat;

public class WhenExecutingFixtureCode extends AbstractTestStepRunnerTest {


    @Rule
    public DisableThucydidesHistoryRule disableThucydidesHistoryRule = new DisableThucydidesHistoryRule();

    @Before
    public void createATestableDriverFactory() throws Exception {
        MockitoAnnotations.initMocks(this);
        StepEventBus.getParallelEventBus().clear();
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

        @Managed(driver = "chrome", options="--headless")
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

        @Managed(driver = "chrome", options="--headless")
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

        @Managed(driver = "chrome", options = "--headless")
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
}
