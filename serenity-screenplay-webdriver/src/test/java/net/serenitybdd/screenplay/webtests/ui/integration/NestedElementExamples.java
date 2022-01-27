package net.serenitybdd.screenplay.webtests.ui.integration;

import io.github.bonigarcia.wdm.WebDriverManager;
import net.serenitybdd.core.steps.WebDriverScenarios;
import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.screenplay.actions.Click;
import net.serenitybdd.screenplay.actions.Open;
import net.serenitybdd.screenplay.annotations.CastMember;
import net.serenitybdd.screenplay.questions.Text;
import net.serenitybdd.screenplay.ui.Button;
import net.serenitybdd.screenplay.ui.PageElement;
import net.thucydides.core.annotations.Managed;
import net.thucydides.core.annotations.SingleBrowser;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Working with nested elements
 */
@RunWith(SerenityRunner.class)
@SingleBrowser
public class NestedElementExamples extends WebDriverScenarios {

    @Managed(driver = "chrome", options = "--headless")
    WebDriver driver;

    @BeforeClass
    public static void setupDriver() {
        WebDriverManager.chromedriver().setup();
    }

    @Before
    public void openBrowser() {
        openUrl("classpath:/sample-web-site/screenplay/ui-elements/forms/nested-elements.html");
    }

    @Test
    public void clickingOnAButtonElement() {
        $(Button.called("Submit").inside("#section1")).click();
        assertThat($("#result").getText()).isEqualTo("Section 1");
    }

    @Test
    public void clickingOnAButtonElementFurtherDownThePage() {
        $(Button.called("Submit").inside("#section2")).click();
        assertThat($("#result").getText()).isEqualTo("Section 2");
    }

    @Test
    public void findingAnElementContainingText() {
        assertThat($(PageElement.containingText("Section 1")).getText()).isEqualTo("Section 1");
    }

    @Test
    public void findingAnElementContainingTextAndMatchingASelector() {
        assertThat($(PageElement.containingText(".section","Section 1")).getText()).contains("Section 1");
        assertThat($(PageElement.containingText(".section","Section 2")).getText()).contains("Section 2");
    }


    @Test
    public void findingAnElementContainingTextAndMatchingASelectorWithFluentDSL() {
        $(Button.called("Add to cart").inside(PageElement.called("item").containingText("Item 2"))).click();;
        assertThat($("#result").getText()).isEqualTo("Item 2");
    }

    @Test
    public void findingAnElementUsingContainingText() {
        $(Button.called("Submit").inside(PageElement.containingText(".section","Section 2"))).click();
        assertThat($("#result").getText()).isEqualTo("Section 2");
    }

}
