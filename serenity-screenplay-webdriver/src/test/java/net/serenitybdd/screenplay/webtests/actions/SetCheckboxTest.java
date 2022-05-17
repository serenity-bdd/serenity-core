package net.serenitybdd.screenplay.webtests.actions;

import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.screenplay.actions.Click;
import net.serenitybdd.screenplay.actions.SetCheckbox;
import net.serenitybdd.screenplay.questions.SelectedStatus;
import net.serenitybdd.screenplay.questions.Text;
import net.serenitybdd.screenplay.targets.Target;
import net.serenitybdd.screenplay.ui.PageElement;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SerenityRunner.class)
public class SetCheckboxTest extends ScreenplayInteractionTestBase {

    private final static Target CHECKBOX = PageElement.withNameOrId("checkbox");

    @Test
    public void setACheckboxUsingATarget() {

        assertThat(dina.asksFor(SelectedStatus.of(CHECKBOX))).isEqualTo(false);

        dina.attemptsTo(SetCheckbox.of(CHECKBOX).toTrue());

        assertThat(dina.asksFor(SelectedStatus.of(CHECKBOX))).isEqualTo(true);
    }

    @Test
    public void setACheckboxUsingATargetWhenTheCheckboxIsAlreadySet() {

        assertThat(dina.asksFor(SelectedStatus.of(CHECKBOX))).isEqualTo(false);

        dina.attemptsTo(SetCheckbox.of(CHECKBOX).toTrue());
        dina.attemptsTo(SetCheckbox.of(CHECKBOX).toTrue());

        assertThat(dina.asksFor(SelectedStatus.of(CHECKBOX))).isEqualTo(true);
    }


    @Test
    public void setACheckboxUsingASelector() {

        assertThat(dina.asksFor(SelectedStatus.of("#checkbox"))).isEqualTo(false);

        dina.attemptsTo(SetCheckbox.of("#checkbox").toTrue());

        assertThat(dina.asksFor(SelectedStatus.of("#checkbox"))).isEqualTo(true);
    }

    @Test
    public void setACheckboxUsingASelectorAfterWaitingForTheElementToBeEnabled() {

        assertThat(dina.asksFor(SelectedStatus.of("#checkbox"))).isEqualTo(false);

        dina.attemptsTo(SetCheckbox.of("#checkbox").toTrue().afterWaitingUntilEnabled());

        assertThat(dina.asksFor(SelectedStatus.of("#checkbox"))).isEqualTo(true);
    }

    @Test
    public void setACheckboxUsingASelectorWhenTheCheckboxIsAlreadySet() {

        assertThat(dina.asksFor(SelectedStatus.of("#checkbox"))).isEqualTo(false);

        dina.attemptsTo(SetCheckbox.of("#checkbox").toTrue());
        dina.attemptsTo(SetCheckbox.of("#checkbox").toTrue());

        assertThat(dina.asksFor(SelectedStatus.of("#checkbox"))).isEqualTo(true);
    }

    @Test
    public void setACheckboxUsingAnElement() {

        WebElementFacade checkbox = BrowseTheWeb.as(dina).find("#checkbox");

        dina.attemptsTo(SetCheckbox.of(checkbox).toTrue());

        assertThat(dina.asksFor(SelectedStatus.of("#checkbox"))).isEqualTo(true);
    }

    @Test
    public void setACheckboxUsingAnElementrWhenTheCheckboxIsAlreadySet() {

        WebElementFacade checkbox = BrowseTheWeb.as(dina).find("#checkbox");

        dina.attemptsTo(SetCheckbox.of(checkbox).toTrue());
        dina.attemptsTo(SetCheckbox.of(checkbox).toTrue());

        assertThat(dina.asksFor(SelectedStatus.of("#checkbox"))).isEqualTo(true);
    }


    @Test
    public void clearACheckboxUsingATarget() {

        dina.attemptsTo(SetCheckbox.of(CHECKBOX).toTrue());
        assertThat(dina.asksFor(SelectedStatus.of(CHECKBOX))).isEqualTo(true);

        dina.attemptsTo(SetCheckbox.of(CHECKBOX).toFalse());

        assertThat(dina.asksFor(SelectedStatus.of(CHECKBOX))).isEqualTo(false);
    }

    @Test
    public void clearACheckboxUsingATargetAfterItIsAlreadyCleared() {
        dina.attemptsTo(SetCheckbox.of(CHECKBOX).toTrue());
        assertThat(dina.asksFor(SelectedStatus.of(CHECKBOX))).isEqualTo(true);

        dina.attemptsTo(SetCheckbox.of(CHECKBOX).toFalse());
        dina.attemptsTo(SetCheckbox.of(CHECKBOX).toFalse());

        assertThat(dina.asksFor(SelectedStatus.of(CHECKBOX))).isEqualTo(false);
    }


    @Test
    public void clearACheckboxUsingAnElement() {

        WebElementFacade checkbox = BrowseTheWeb.as(dina).find("#checkbox");
        dina.attemptsTo(SetCheckbox.of(checkbox).toTrue());

        dina.attemptsTo(SetCheckbox.of(checkbox).toFalse());

        assertThat(dina.asksFor(SelectedStatus.of("#checkbox"))).isEqualTo(false);
    }

    @Test
    public void clearACheckboxUsingAnElementrWhenTheCheckboxIsAlreadySet() {

        WebElementFacade checkbox = BrowseTheWeb.as(dina).find("#checkbox");
        dina.attemptsTo(SetCheckbox.of(checkbox).toTrue());

        dina.attemptsTo(SetCheckbox.of(checkbox).toFalse());
        dina.attemptsTo(SetCheckbox.of(checkbox).toFalse());

        assertThat(dina.asksFor(SelectedStatus.of("#checkbox"))).isEqualTo(false);
    }

}
