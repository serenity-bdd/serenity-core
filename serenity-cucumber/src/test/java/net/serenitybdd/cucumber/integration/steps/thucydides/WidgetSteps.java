package net.serenitybdd.cucumber.integration.steps.thucydides;

import net.thucydides.core.annotations.Step;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by john on 20/01/15.
 */
public class WidgetSteps {

    @Step
    public void shouldBeBilled(int billedPrice, int totalPrice) {
        assertThat(billedPrice).isEqualTo(totalPrice);
    }

}
