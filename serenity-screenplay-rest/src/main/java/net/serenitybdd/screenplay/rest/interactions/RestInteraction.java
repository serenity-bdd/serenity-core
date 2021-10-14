package net.serenitybdd.screenplay.rest.interactions;

import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import net.serenitybdd.markers.CanBeSilent;
import net.serenitybdd.markers.DisableScreenshots;
import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.Interaction;
import net.serenitybdd.screenplay.rest.questions.RestQueryFunction;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * A base class for all Screenplay REST interactions, which gives direct access to the RestAssured API.
 */
public abstract class RestInteraction implements Interaction, DisableScreenshots, CanBeSilent {

    private static final ContentType DEFAULT_CONTENT_TYPE = ContentType.JSON;

    private boolean withReporting = true;

    List<Function<RequestSpecification, RequestSpecification>> restConfigurations = new ArrayList<>();

    public RestInteraction with(RestQueryFunction restConfiguration) {
        this.restConfigurations.add(restConfiguration);
        return this;
    }

    // Alias methods for Groovy
    public RestInteraction withRequest(RestQueryFunction restConfiguration) {
        return with(restConfiguration);
    }

    public RestInteraction with(List<RestQueryFunction> restConfigurations) {
        this.restConfigurations.addAll(restConfigurations);
        return this;
    }

    // Alias methods for Groovy
    public RestInteraction withRequest(List<RestQueryFunction> restConfigurations) {
        return with(restConfigurations);
    }

    protected RequestSpecification rest() {
        RequestSpecification requestSpecification = (withReporting) ? SerenityRest.given() : SerenityRest.givenWithNoReporting();
        requestSpecification.contentType(DEFAULT_CONTENT_TYPE);
        for (Function<RequestSpecification, RequestSpecification> restConfiguration : restConfigurations) {
            requestSpecification = restConfiguration.apply(requestSpecification);
        }
        return requestSpecification;
    }

    @Override
    public boolean isSilent() {
        return !withReporting;
    }

    public RestInteraction withNoReporting() {
        withReporting = false;
        return this;
    }
}
