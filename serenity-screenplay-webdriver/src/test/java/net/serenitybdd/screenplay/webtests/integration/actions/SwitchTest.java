package net.serenitybdd.screenplay.webtests.integration.actions;

import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.screenplay.actions.*;
import net.serenitybdd.screenplay.questions.page.TheWebPage;
import net.serenitybdd.screenplay.targets.Target;
import net.serenitybdd.screenplay.ui.Link;
import net.serenitybdd.screenplay.webtests.pages.HomePage;
import net.thucydides.core.annotations.Managed;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SerenityRunner.class)
public class SwitchTest {

    @Managed(driver = "chrome", options = "--headless")
    WebDriver driver;

    Actor dina;

    private final static Target OPEN_IN_NEW_TAB = Link.withText("Open in new tab");
    private final static Target OPEN_IN_NEW_WINDOW = Link.withText("Open in new window");

    @Before
    public void openSampleApp() {
        dina = new Actor("Dina");
        dina.can(BrowseTheWeb.with(driver));
        dina.attemptsTo(Open.browserOn().the(HomePage.class));
    }

    @After
    public void closeWindows(){
        driver.quit();
    }

    @Test
    public void switchToNewWindow() {
        assertThat(dina.asksFor(TheWebPage.title())).isEqualTo("Sample web site");
        dina.attemptsTo(
                Click.on(OPEN_IN_NEW_TAB),
                Switch.toNewWindow()
        );
        assertThat(dina.asksFor(TheWebPage.title())).isEqualTo("My Profile Page");
    }

    @Test
    public void switchToNewWindowAndSwitchBack() {
        assertThat(dina.asksFor(TheWebPage.title())).isEqualTo("Sample web site");
        dina.attemptsTo(
                Click.on(OPEN_IN_NEW_TAB),
                Switch.toNewWindow()
        );
        assertThat(dina.asksFor(TheWebPage.title())).isEqualTo("My Profile Page");

        dina.attemptsTo(Switch.toTheOtherWindow());
        assertThat(dina.asksFor(TheWebPage.title())).isEqualTo("Sample web site");
    }

    @Test
    public void switchToAWindowByTitle() {
        assertThat(dina.asksFor(TheWebPage.title())).isEqualTo("Sample web site");
        dina.attemptsTo(
                Click.on(OPEN_IN_NEW_TAB),
                Switch.toNewWindow()
        );
        assertThat(dina.asksFor(TheWebPage.title())).isEqualTo("My Profile Page");

        dina.attemptsTo(Switch.toWindowTitled("Sample web site"));
        assertThat(dina.asksFor(TheWebPage.title())).isEqualTo("Sample web site");
    }

    @Test
    public void switchToAWindowByName() {
        assertThat(dina.asksFor(TheWebPage.title())).isEqualTo("Sample web site");

        String mainWindowHandle = BrowseTheWeb.as(dina).getDriver().getWindowHandle();

        dina.attemptsTo(
                Click.on(OPEN_IN_NEW_TAB),
                Switch.toTheOtherWindow()
        );
        assertThat(dina.asksFor(TheWebPage.title())).isEqualTo("My Profile Page");

        dina.attemptsTo(Switch.toWindow(mainWindowHandle));
        assertThat(dina.asksFor(TheWebPage.title())).isEqualTo("Sample web site");
    }

    @Test
    public void switchToANewWindow() {
        assertThat(dina.asksFor(TheWebPage.title())).isEqualTo("Sample web site");

        dina.attemptsTo(
                Click.on(OPEN_IN_NEW_WINDOW),
                Switch.toNewWindow()
        );
        assertThat(dina.asksFor(TheWebPage.title())).isEqualTo("My Profile Page");

        dina.attemptsTo(Switch.toTheOtherWindow());
        assertThat(dina.asksFor(TheWebPage.title())).isEqualTo("Sample web site");
    }

    @Test
    public void switchToAnAlert() {
        dina.attemptsTo(
                Click.on("#submit"),
                Switch.toAlert()
        );
        assertThat(dina.asksFor(HtmlAlert.text())).isEqualTo("Are you sure?");

        dina.attemptsTo(Switch.toAlert().andAccept());
        assertThat(dina.asksFor(TheWebPage.title())).isEqualTo("Sample web site");
    }

    @Test
    public void switchToAnAlertAndDismiss() {
        dina.attemptsTo(
                Click.on("#submit"),
                Switch.toAlert().andDismiss()
        );
        assertThat(dina.asksFor(TheWebPage.title())).isEqualTo("Sample web site");
    }

    @Test
    public void switchToAnAlertAndAccept() {
        dina.attemptsTo(
                Click.on("#submit"),
                Switch.toAlert().andAccept()
        );
        assertThat(dina.asksFor(TheWebPage.title())).isEqualTo("Sample web site");
    }
}
