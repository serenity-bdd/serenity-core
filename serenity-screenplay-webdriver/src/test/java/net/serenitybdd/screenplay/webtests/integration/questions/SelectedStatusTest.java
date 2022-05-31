package net.serenitybdd.screenplay.webtests.integration.questions;

import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.screenplay.questions.SelectedStatus;
import net.serenitybdd.screenplay.questions.SelectedStatus;
import net.serenitybdd.screenplay.targets.Target;
import net.serenitybdd.screenplay.ui.PageElement;
import net.serenitybdd.screenplay.webtests.integration.ScreenplayInteractionTestBase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SerenityRunner.class)
public class SelectedStatusTest extends ScreenplayInteractionTestBase {

    private final static Target CHECKBOX = PageElement.locatedBy("#checked-checkbox");
    private final static Target CHECKBOXES = PageElement.locatedBy(".checkbox-field");

    @Test
    public void checkForTheSelectedStatusOfAFieldUsingATarget() {
        assertThat(dina.asksFor(SelectedStatus.of(CHECKBOX))).isTrue();
    }

    @Test
    public void checkForTheSelectedStatusOfAFieldUsingALocator() {
        assertThat(dina.asksFor(SelectedStatus.of("#checked-checkbox"))).isTrue();
    }

    @Test
    public void checkForTheSelectedStatusOfAFieldUsingAByLocator() {
        assertThat(dina.asksFor(SelectedStatus.of(By.id("checked-checkbox")))).isTrue();
    }


    @Test
    public void checkForTheSelectedStatusOfSeveralFieldsUsingATarget() {
        assertThat(dina.asksFor(SelectedStatus.ofEach(CHECKBOXES))).containsExactly(false,true);
    }

    @Test
    public void checkForTheSelectedStatusOfSeveralFieldsUsingALocator() {
        assertThat(dina.asksFor(SelectedStatus.ofEach(".checkbox-field"))).containsExactly(false,true);
    }

    @Test
    public void checkForTheSelectedStatusOfSeveralFieldsUsingAByLocator() {
        assertThat(dina.asksFor(SelectedStatus.ofEach(By.cssSelector(".checkbox-field")))).containsExactly(false,true);
    }


}
