package net.serenitybdd.screenplay.webtests.integration.actions;

import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.screenplay.actions.SendKeys;
import net.serenitybdd.screenplay.questions.Value;
import net.serenitybdd.screenplay.targets.Target;
import net.serenitybdd.screenplay.webtests.integration.ScreenplayInteractionTestBase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SerenityRunner.class)
public class SendKeysTest extends ScreenplayInteractionTestBase {

    private final static Target INPUT_FIELD = Target.the("Last name field").locatedBy("#lastName");

    @Test
    public void enterAValueInAFieldUsingATarget() {

        assertThat(dina.asksFor(Value.of(INPUT_FIELD))).isEqualTo("Name:");

        dina.attemptsTo(SendKeys.of("Sarah-Jane").into(INPUT_FIELD));

        assertThat(dina.asksFor(Value.of(INPUT_FIELD))).isEqualTo("Name:Sarah-Jane");
    }

    @Test
    public void enterAValueInAFieldUsingASelector() {

        assertThat(dina.asksFor(Value.of("#lastName"))).isEqualTo("Name:");

        dina.attemptsTo(SendKeys.of("Sarah-Jane").into("#lastName"));

        assertThat(dina.asksFor(Value.of(INPUT_FIELD))).isEqualTo("Name:Sarah-Jane");
    }

    @Test
    public void enterAValueInAFieldUsingAByLocator() {

        assertThat(dina.asksFor(Value.of(By.id("lastName")))).isEqualTo("Name:");

        dina.attemptsTo(SendKeys.of("Sarah-Jane").into(By.id("lastName")));

        assertThat(dina.asksFor(Value.of(INPUT_FIELD))).isEqualTo("Name:Sarah-Jane");
    }

}
