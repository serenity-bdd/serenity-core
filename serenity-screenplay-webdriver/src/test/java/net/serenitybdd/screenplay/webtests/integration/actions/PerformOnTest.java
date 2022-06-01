package net.serenitybdd.screenplay.webtests.integration.actions;

import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.screenplay.actions.PerformOn;
import net.serenitybdd.screenplay.actions.SetCheckbox;
import net.serenitybdd.screenplay.questions.SelectedStatus;
import net.serenitybdd.screenplay.targets.Target;
import net.serenitybdd.screenplay.ui.PageElement;
import net.serenitybdd.screenplay.webtests.integration.ScreenplayInteractionTestBase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SerenityRunner.class)
public class PerformOnTest extends ScreenplayInteractionTestBase {

    private final static Target CONDIMENTS = PageElement.withCSSClass("condiment");
    private final static Target SALT = PageElement.withNameOrId("salt");
    private final static Target PEPPER = PageElement.withNameOrId("pepper");
    private final static Target SAUCE = PageElement.withNameOrId("sauce");

    @Test
    public void performMultipleActionsUsingATarget() {

        dina.attemptsTo(
                PerformOn.eachMatching(CONDIMENTS, WebElementFacade::click)
        );

        assertThat(dina.asksFor(SelectedStatus.of(SALT))).isEqualTo(true);
        assertThat(dina.asksFor(SelectedStatus.of(PEPPER))).isEqualTo(true);
        assertThat(dina.asksFor(SelectedStatus.of(SAUCE))).isEqualTo(true);

    }

    @Test
    public void performMultipleActionsUsingALocator() {

        dina.attemptsTo(
                PerformOn.eachMatching(".condiment", WebElementFacade::click)
        );

        assertThat(dina.asksFor(SelectedStatus.of(SALT))).isEqualTo(true);
        assertThat(dina.asksFor(SelectedStatus.of(PEPPER))).isEqualTo(true);
        assertThat(dina.asksFor(SelectedStatus.of(SAUCE))).isEqualTo(true);

    }

    @Test
    public void performMultipleActionsUsingAByLocator() {

        dina.attemptsTo(
                PerformOn.eachMatching(By.cssSelector(".condiment"), WebElementFacade::click)
        );

        assertThat(dina.asksFor(SelectedStatus.of(SALT))).isEqualTo(true);
        assertThat(dina.asksFor(SelectedStatus.of(PEPPER))).isEqualTo(true);
        assertThat(dina.asksFor(SelectedStatus.of(SAUCE))).isEqualTo(true);

    }

    @Test
    public void performMultipleActionsUsingAPerformable() {

        dina.attemptsTo(
                PerformOn.eachMatching(".condiment", checkbox -> dina.attemptsTo(SetCheckbox.of(checkbox).toTrue()))
        );

        assertThat(dina.asksFor(SelectedStatus.of(SALT))).isEqualTo(true);
        assertThat(dina.asksFor(SelectedStatus.of(PEPPER))).isEqualTo(true);
        assertThat(dina.asksFor(SelectedStatus.of(SAUCE))).isEqualTo(true);

    }
}
