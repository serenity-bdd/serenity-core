package net.serenitybdd.screenplay.webtests.ui;

import io.github.bonigarcia.wdm.WebDriverManager;
import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.screenplay.actions.Open;
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
public class ElementExamples {

    @Managed(driver = "chrome", options = "--headless")
    WebDriver driver;

    Actor sarah = Actor.named("Sarah");

    @BeforeClass
    public static void setupDriver() {
        WebDriverManager.chromedriver().setup();
    }

    @Before
    public void openBrowser() {
        sarah.can(BrowseTheWeb.with(driver));

        sarah.attemptsTo(
                Open.url("classpath:/sample-web-site/screenplay/ui-elements/elements/elements.html")
        );
    }

    @Test
    public void findingAnElementWithAGivenId() {
        assertThat(Visibility.of(PageElement.called("container")).answeredBy(sarah)).isTrue();
    }

    @Test
    public void findingAnElementWithAGivenDataTestValue() {
        assertThat(Visibility.of(PageElement.called("the-container")).answeredBy(sarah)).isTrue();
    }

    @Test
    public void findingAnElementWithACSSSelector() {
        assertThat(Visibility.of(PageElement.locatedBy(".a-container").describedAs("the container")).answeredBy(sarah)).isTrue();
    }

    @Test
    public void findingAnElementWithAClass() {
        assertThat(Visibility.of(PageElement.called("a-container")).answeredBy(sarah)).isTrue();
    }

    @Test
    public void findingAnElementWithACSSSelectorContainingText() {
        assertThat(Text.of(PageElement.locatedBy(".item").containingText("Item 1")).answeredBy(sarah)).contains("Item 1");
    }

    @Test
    public void findingAnElementWithAClassContainingText() {
        assertThat(Text.of(PageElement.called("item").containingText("Item 1")).answeredBy(sarah)).contains("Item 1");
    }
}
