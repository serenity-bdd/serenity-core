package net.serenitybdd.screenplay.webtests.integration.questions;

import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.screenplay.actions.Clear;
import net.serenitybdd.screenplay.questions.Absence;
import net.serenitybdd.screenplay.questions.Value;
import net.serenitybdd.screenplay.targets.Target;
import net.serenitybdd.screenplay.ui.PageElement;
import net.serenitybdd.screenplay.webtests.integration.ScreenplayInteractionTestBase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SerenityRunner.class)
public class AbsenceTest extends ScreenplayInteractionTestBase {

    private final static Target EXISTANT_FIELD = PageElement.withNameOrId("firstName");
    private final static Target NON_EXISTANT_FIELD = PageElement.withNameOrId("does-not-exist");
    private final static Target INVISIBLE_FIELD = PageElement.withNameOrId("is-not-displayed");
    private final static Target INVISIBLE_FIELDS = PageElement.withCSSClass("not-displayed");

    @Before
    public void setTimeout() {
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(0));
    }

    @Test
    public void checkForTheAbsenceOfAFieldUsingATarget() {
        assertThat(dina.asksFor(Absence.of(NON_EXISTANT_FIELD))).isTrue();
    }

    @Test
    public void checkForTheAbsenceOfAFieldUsingALocator() {
        assertThat(dina.asksFor(Absence.of("#does-not-exist"))).isTrue();
    }

    @Test
    public void checkForTheAbsenceOfAFieldUsingABy() {
        assertThat(dina.asksFor(Absence.of(By.id("does-not-exist")))).isTrue();
    }

    @Test
    public void checkForTheNonAbsenceOfAFieldUsingATarget() {
        assertThat(dina.asksFor(Absence.of(EXISTANT_FIELD))).isFalse();
    }

    @Test
    public void checkForTheAbsenceOfAFieldThatIsNotDisplayed() {
        assertThat(dina.asksFor(Absence.of(INVISIBLE_FIELD))).isTrue();
    }

    @Test
    public void checkForTheAbsenseOfSeveralElements() {
        assertThat(dina.asksFor(Absence.ofEach(INVISIBLE_FIELDS))).containsExactly(true, true);

    }

    @Test
    public void checkForTheAbsenseOfSeveralElementsByLocator() {
        assertThat(dina.asksFor(Absence.ofEach(By.cssSelector(".not-displayed")))).containsExactly(true, true);
    }

    @Test
    public void checkForTheAbsenseOfSeveralElementsByString() {
        assertThat(dina.asksFor(Absence.ofEach(".not-displayed"))).containsExactly(true, true);
    }

}
