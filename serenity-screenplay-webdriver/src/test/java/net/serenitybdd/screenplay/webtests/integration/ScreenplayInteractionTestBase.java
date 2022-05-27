package net.serenitybdd.screenplay.webtests.integration;

import io.github.bonigarcia.wdm.WebDriverManager;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.screenplay.actions.Open;
import net.serenitybdd.screenplay.webtests.pages.HomePage;
import net.thucydides.core.annotations.Managed;
import org.junit.Before;
import org.junit.BeforeClass;
import org.openqa.selenium.WebDriver;

public abstract class ScreenplayInteractionTestBase {

    @Managed(driver = "chrome", options = "--headless")
    protected WebDriver driver;

    protected Actor dina;

    @BeforeClass
    public static void setupDriver() {
        WebDriverManager.chromedriver().setup();
    }

    @Before
    public void openSampleApp() {
        dina = new Actor("Dina");
        dina.can(BrowseTheWeb.with(driver));
        dina.attemptsTo(Open.browserOn().the(HomePage.class));
    }

}
