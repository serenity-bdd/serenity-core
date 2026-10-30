package todo;

import net.serenitybdd.junit5.SerenityJUnit5Extension;
import net.serenitybdd.screenplay.actors.OnStage;
import net.serenitybdd.screenplay.playwright.actors.PlaywrightCast;
import net.serenitybdd.screenplay.playwright.assertions.Ensure;
import net.serenitybdd.screenplay.playwright.interactions.Check;
import net.serenitybdd.screenplay.playwright.interactions.Click;
import net.serenitybdd.screenplay.playwright.interactions.Open;
import net.serenitybdd.screenplay.playwright.interactions.SelectFromOptions;
import net.serenitybdd.screenplay.playwright.interactions.WaitFor;
import net.serenitybdd.screenplay.playwright.questions.Checked;
import net.serenitybdd.screenplay.playwright.questions.SelectedOptionText;
import net.serenitybdd.screenplay.playwright.questions.Value;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static net.serenitybdd.screenplay.GivenWhenThen.seeThat;
import static org.hamcrest.Matchers.equalTo;

/** Playwright interactions and questions for forms. Selectors are Playwright selector strings. */
@ExtendWith(SerenityJUnit5Extension.class)
class FormTest {

    // A self-contained page, so the example runs without a network or a site
    static final String FORM_PAGE = "data:text/html,"
            + "<select id='country'><option value='us'>United States</option><option value='ca'>Canada</option></select>"
            + "<input type='checkbox' id='terms'><button id='submit'>Send</button>";

    @BeforeEach
    void setTheStage() {
        OnStage.setTheStage(new PlaywrightCast());
    }

    @Test
    void shouldFillInAForm() {
        var ana = OnStage.theActorCalled("Ana");
        ana.attemptsTo(
                Open.url(FORM_PAGE),
                SelectFromOptions.byVisibleText("Canada").from("#country"),   // also byValue(..), byIndex(..)
                Check.the("#terms"),                                          // Uncheck.checkbox(..) to clear
                WaitFor.selector("#submit"),
                Click.on("#submit"),
                Ensure.that("#country").hasValue("ca"),
                Ensure.that("#terms").isChecked());
        ana.should(
                seeThat(SelectedOptionText.of("#country"), equalTo("Canada")),
                seeThat(Value.of("#country"), equalTo("ca")),
                seeThat(Checked.of("#terms")));
    }
}
