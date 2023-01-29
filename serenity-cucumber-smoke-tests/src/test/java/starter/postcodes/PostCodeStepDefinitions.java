package starter.postcodes;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import net.serenitybdd.rest.SerenityRest;
import net.thucydides.core.annotations.Steps;

import static net.serenitybdd.rest.SerenityRest.restAssuredThat;
import static org.hamcrest.Matchers.equalTo;

public class PostCodeStepDefinitions {

    @Steps
    PostCodeAPI postCodeAPI;

    @When("I look up a post code {word} for country code {word}")
    public void lookUpAPostCode(String postCode, String country) {
        postCodeAPI.fetchLocationByPostCodeAndCountry(postCode, country);
    }

    @Then("the resulting location should be {} in {}")
    public void theResultingLocationShouldBe(String placeName, String country) {
        restAssuredThat(response -> response.statusCode(200));
        restAssuredThat(response -> response.body(LocationResponse.COUNTRY, equalTo(country)));
        restAssuredThat(response -> response.body(LocationResponse.FIRST_PLACE_NAME, equalTo(placeName)));
    }
}
