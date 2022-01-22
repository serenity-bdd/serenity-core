package net.serenitybdd.screenplay.webtests.ui.integration;

import io.github.bonigarcia.wdm.WebDriverManager;
import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.screenplay.actions.Click;
import net.serenitybdd.screenplay.actions.Open;
import net.serenitybdd.screenplay.annotations.CastMember;
import net.serenitybdd.screenplay.ui.Button;
import net.serenitybdd.screenplay.ui.Image;
import net.thucydides.core.annotations.Managed;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;

/**
 * Working with HTML images
 */
@RunWith(SerenityRunner.class)
public class ImageExamples {

    @Managed(driver = "chrome", options = "--headless")
    WebDriver driver;

    @CastMember(name = "Sarah")
    Actor sarah;

    @BeforeClass
    public static void setupDriver() {
        WebDriverManager.chromedriver().setup();
    }

    @Before
    public void openBrowser() {
        sarah.attemptsTo(
                Open.url("classpath:/sample-web-site/screenplay/ui-elements/elements/images.html")
        );
    }

    @Test
    public void findingAnImageByAltText() {
        sarah.attemptsTo(
                Click.on(Image.withAltText("Girl in a jacket"))
        );
    }

    @Test
    public void findingAnImageBySrc() {
        sarah.attemptsTo(
                Click.on(Image.withSrc("img_girl.jpg"))
        );
    }

    @Test
    public void findingAnImageBySrcEndingWith() {
        sarah.attemptsTo(
                Click.on(Image.withSrcEndingWith("girl.jpg"))
        );
    }

    @Test
    public void findingAnImageBySrcStartingWith() {
        sarah.attemptsTo(
                Click.on(Image.withSrcStartingWith("img_"))
        );
    }
}
