package todo;

import net.serenitybdd.junit5.SerenityJUnit5Extension;
import net.serenitybdd.screenplay.actors.OnStage;
import net.serenitybdd.screenplay.playwright.actors.PlaywrightCast;
import net.serenitybdd.screenplay.playwright.assertions.Ensure;
import net.serenitybdd.screenplay.playwright.interactions.Open;
import net.serenitybdd.screenplay.playwright.interactions.WaitFor;
import net.serenitybdd.screenplay.playwright.questions.Text;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static net.serenitybdd.screenplay.GivenWhenThen.seeThat;
import static org.hamcrest.Matchers.contains;

/**
 * A selector used by WaitFor, Click, Ensure.hasText and Text.of must match exactly ONE element (Playwright's strict mode).
 * For a list, wait on the first row, count with hasCount, read all texts with Text.ofEach, or pick one with ">> nth=N".
 */
@ExtendWith(SerenityJUnit5Extension.class)
@DisplayName("Reading a list on a page")
class ListTest {

    static final String LIST_PAGE = "data:text/html,<table><tbody id='rows'>"
            + "<tr class='row'><td class='title'>Fluent Python</td></tr>"
            + "<tr class='row'><td class='title'>Clean Code</td></tr>"
            + "<tr class='row'><td class='title'>Refactoring</td></tr></tbody></table>";

    @BeforeEach
    void setTheStage() {
        OnStage.setTheStage(new PlaywrightCast());
    }

    @Test
    @DisplayName("The rows of a list can be counted, read and picked by position")
    void rowsCanBeCountedReadAndPickedByPosition() {
        var ana = OnStage.theActorCalled("Ana");
        ana.attemptsTo(
                Open.url(LIST_PAGE),
                WaitFor.selector("#rows .row >> nth=0"),            // not "#rows .row": that matches 3 elements and fails
                Ensure.that("#rows .row").hasCount(3),              // hasCount is the check for "how many"
                Ensure.that("#rows .row >> nth=0 >> .title").hasText("Fluent Python"),
                Ensure.that("#rows .row >> nth=-1 >> .title").hasText("Refactoring"));
        ana.should(seeThat(Text.ofEach("#rows .title"), contains("Fluent Python", "Clean Code", "Refactoring")));
    }
}
