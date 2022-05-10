package net.serenitybdd.screenplay.webtests.actions;

import io.github.bonigarcia.wdm.WebDriverManager;
import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.screenplay.actions.Clear;
import net.serenitybdd.screenplay.actions.Open;
import net.serenitybdd.screenplay.questions.Value;
import net.serenitybdd.screenplay.targets.Target;
import net.serenitybdd.screenplay.ui.PageElement;
import net.serenitybdd.screenplay.webtests.pages.HomePage;
import net.thucydides.core.annotations.Managed;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SerenityRunner.class)
public class ClearTest extends ScreenplayInteractionTestBase {

    private final static Target POPULATED_INPUT = PageElement.withNameOrId("populated-input");
    private final static Target POPULATED_CHECKBOX = PageElement.withNameOrId("populated-checkbox");

    @Test
    public void clearAnInputFieldUsingATarget() {

        assertThat(dina.asksFor(Value.of(POPULATED_INPUT))).isEqualTo("Populated");

        dina.attemptsTo(Clear.field(POPULATED_INPUT));

        assertThat(dina.asksFor(Value.of(POPULATED_INPUT))).isEmpty();
    }

    @Test
    public void clearAnInputFieldUsingASelector() {

        assertThat(dina.asksFor(Value.of(POPULATED_INPUT))).isEqualTo("Populated");

        dina.attemptsTo(Clear.field("#populated-input"));

        assertThat(dina.asksFor(Value.of(POPULATED_INPUT))).isEmpty();
    }

    @Test
    public void clearAnInputFieldUsingAByLocator() {

        assertThat(dina.asksFor(Value.of(POPULATED_INPUT))).isEqualTo("Populated");

        dina.attemptsTo(Clear.field(By.id("populated-input")));

        assertThat(dina.asksFor(Value.of(POPULATED_INPUT))).isEmpty();
    }
}
