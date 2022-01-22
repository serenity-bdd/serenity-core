package net.serenitybdd.screenplay.webtests.ui.integration;

import io.github.bonigarcia.wdm.WebDriverManager;
import net.serenitybdd.core.steps.WebDriverScenarios;
import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.screenplay.actions.Open;
import net.serenitybdd.screenplay.annotations.CastMember;
import net.serenitybdd.screenplay.questions.Text;
import net.serenitybdd.screenplay.questions.Visibility;
import net.serenitybdd.screenplay.ui.PageElement;
import net.thucydides.core.annotations.Managed;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Working with general HTML elements
 */
@RunWith(SerenityRunner.class)
public class ElementExamples extends WebDriverScenarios {

    @Managed(driver = "chrome", options = "--headless")
    WebDriver driver;

    @Before
    public void openBrowser() {
        openUrl("classpath:/sample-web-site/screenplay/ui-elements/elements/elements.html");
    }

    @Test
    public void findingAnElementWithAGivenId() {
        $(PageElement.called("container")).shouldBeVisible();
    }

    @Test
    public void findingAnElementWithAGivenDataTestValue() {
        $(PageElement.called("the-container")).shouldBeVisible();
    }

    @Test
    public void findingAnElementWithACSSSelector() {
        $(PageElement.locatedBy(".a-container").describedAs("the container")).shouldBeVisible();
    }

    @Test
    public void findingAnElementWithAClass() {
        $(PageElement.called("a-container")).shouldBeVisible();
    }

    @Test
    public void findingAnElementWithACSSSelectorContainingText() {
        $(PageElement.locatedBy(".item").containingText("Item 1")).shouldContainText("Item 1");
    }

    @Test
    public void findingAnElementWithAClassContainingText() {
        $(PageElement.called("item").containingText("Item 1")).shouldContainText("Item 1");
    }
}
