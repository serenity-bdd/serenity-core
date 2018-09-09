package net.serenitybdd.rest;

import io.restassured.response.ValidatableResponse;
import net.thucydides.core.steps.ExecutedStepDescription;
import net.thucydides.core.steps.StepEventBus;
import net.thucydides.core.steps.StepFailure;

import java.util.function.Consumer;

public class Ensure {

    /**
     * A helper method to make a RestAssured assertion appear as a separate step.
     * e.g
     *   Ensure.that("Commany name should be returned",
     *               response -> response.body("companyName", equalTo("Apple Inc.s")));
     *
     * @param description
     * @param check
     */
    public static void that(String description, Consumer<ValidatableResponse> check) {
        StepEventBus.getEventBus().stepStarted(ExecutedStepDescription.withTitle("Ensure that " + description));
        try {
            check.accept(SerenityRest.then());
        } catch(Throwable e) {
            StepEventBus.getEventBus().stepFailed(new StepFailure(ExecutedStepDescription.withTitle(description), e));
        } finally {
            StepEventBus.getEventBus().stepFinished();
        }
    }
}
