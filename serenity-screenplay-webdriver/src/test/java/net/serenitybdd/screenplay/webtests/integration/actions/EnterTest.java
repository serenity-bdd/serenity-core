package net.serenitybdd.screenplay.webtests.integration.actions;

import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.screenplay.actions.Enter;
import net.serenitybdd.screenplay.actions.Hit;
import net.serenitybdd.screenplay.questions.Value;
import net.serenitybdd.screenplay.targets.Target;
import net.serenitybdd.screenplay.webtests.integration.ScreenplayInteractionTestBase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SerenityRunner.class)
public class EnterTest extends ScreenplayInteractionTestBase {

    private final static Target INPUT_FIELD = Target.the("First name field").locatedBy("#firstName");

    @Test
    public void enterAValueInAFieldUsingATarget() {

        assertThat(dina.asksFor(Value.of(INPUT_FIELD))).isEqualTo("Jo Grant");

        dina.attemptsTo(Enter.theValue("Sarah-Jane").into(INPUT_FIELD));

        assertThat(dina.asksFor(Value.of(INPUT_FIELD))).isEqualTo("Sarah-Jane");
    }

    @Test
    public void enterAValueInAFieldUsingASelector() {

        assertThat(dina.asksFor(Value.of("#firstName"))).isEqualTo("Jo Grant");

        dina.attemptsTo(Enter.theValue("Sarah-Jane").into("#firstName"));

        assertThat(dina.asksFor(Value.of(INPUT_FIELD))).isEqualTo("Sarah-Jane");
    }

    @Test
    public void enterAValueInAFieldUsingAByLocator() {

        assertThat(dina.asksFor(Value.of(By.id("firstName")))).isEqualTo("Jo Grant");

        dina.attemptsTo(Enter.theValue("Sarah-Jane").into(By.id("firstName")));
        dina.attemptsTo(Hit.the(Keys.TAB).into(By.id("firstName")));
    }

}
