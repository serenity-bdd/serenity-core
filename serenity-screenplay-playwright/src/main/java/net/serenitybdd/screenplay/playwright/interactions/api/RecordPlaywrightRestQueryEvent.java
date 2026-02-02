package net.serenitybdd.screenplay.playwright.interactions.api;

import net.serenitybdd.model.rest.RestQuery;
import net.thucydides.core.steps.events.StepEventBusEventBase;

/**
 * Event to record a Playwright API request/response to the Serenity test step.
 *
 * <p>This event is published when using {@link APIRequest} to make API calls.
 * The request and response details will appear in the Serenity report alongside
 * the test steps, similar to how RestAssured API calls are recorded.</p>
 */
public class RecordPlaywrightRestQueryEvent extends StepEventBusEventBase {

    private final RestQuery restQuery;

    public RecordPlaywrightRestQueryEvent(RestQuery restQuery) {
        this.restQuery = restQuery;
    }

    @Override
    public void play() {
        getStepEventBus().getBaseStepListener().recordRestQuery(restQuery);
    }

    @Override
    public String toString() {
        return "EventBusEvent RECORD_PLAYWRIGHT_REST_QUERY_EVENT " + restQuery;
    }
}
