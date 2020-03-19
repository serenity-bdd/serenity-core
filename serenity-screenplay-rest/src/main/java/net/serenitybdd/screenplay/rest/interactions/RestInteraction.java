package net.serenitybdd.screenplay.rest.interactions;

import io.restassured.specification.RequestSpecification;
import net.serenitybdd.markers.DisableScreenshots;
import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Interaction;
import net.serenitybdd.screenplay.rest.questions.RestQueryFunction;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A base class for all Screenplay REST interactions, which gives direct access to the RestAssured API.
 */
public abstract class RestInteraction implements Interaction, DisableScreenshots {

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
        RequestSpecification requestSpecification = SerenityRest.given();
        for(Function<RequestSpecification, RequestSpecification> restConfiguration : restConfigurations) {
            requestSpecification = restConfiguration.apply(requestSpecification);
        }
        return requestSpecification;
    }
}
