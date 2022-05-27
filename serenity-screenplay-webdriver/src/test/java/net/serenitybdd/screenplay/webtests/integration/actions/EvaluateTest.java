package net.serenitybdd.screenplay.webtests.integration.actions;

import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.screenplay.actions.Evaluate;
import net.serenitybdd.screenplay.webtests.actions.LastScriptExecution;
import net.serenitybdd.screenplay.webtests.integration.ScreenplayInteractionTestBase;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SerenityRunner.class)
public class EvaluateTest extends ScreenplayInteractionTestBase {

    @Test
    public void evaluateASimpleJavascriptExpressionAsAnInteraction() {
        dina.attemptsTo(Evaluate.javascript("2+2"));
    }

    @Test
    public void lastScriptExecutionCanBeUsedToGetTheLastResult() {
        dina.attemptsTo(Evaluate.javascript("return 2+2"));
        Assertions.assertThat(dina.asksFor(LastScriptExecution.result().asString())).isEqualTo("4");
    }

    @Test
    public void scriptResultShouldBeClearedIfNoValueIsReturned() {
        dina.attemptsTo(Evaluate.javascript("2+2"));
        assertThat(dina.asksFor(LastScriptExecution.result())).isNull();
    }

    @Test
    public void evaluateAJavascriptExpressionAndReturnTheResult() {
        Long result = (Long) dina.asksFor(Evaluate.javascript("return 1 + 1").result());
        assertThat(result).isEqualTo(2);
    }

}
