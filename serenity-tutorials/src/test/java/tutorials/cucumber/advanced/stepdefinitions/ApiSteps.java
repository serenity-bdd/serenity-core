package tutorials.cucumber.advanced.stepdefinitions;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import net.serenitybdd.rest.SerenityRest;

import static net.serenitybdd.rest.SerenityRest.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

/**
 * Step definitions for API testing with Cucumber.
 * Uses SerenityRest directly for simpler configuration.
 */
public class ApiSteps {

    private static final String BASE_URL = "https://jsonplaceholder.typicode.com";

    @Given("the API is available")
    public void theApiIsAvailable() {
        // Configure base URI for all subsequent requests
        System.out.println("[STEP] API configured at " + BASE_URL);
    }

    @When("user {int} is requested")
    public void userIsRequested(int userId) {
        given()
            .baseUri(BASE_URL)
        .when()
            .get("/users/" + userId);

        System.out.println("[STEP] Requested user " + userId);
    }

    @When("a post titled {string} is created")
    public void aPostTitledIsCreated(String title) {
        given()
            .baseUri(BASE_URL)
            .contentType("application/json")
            .body(String.format("""
                {
                    "title": "%s",
                    "body": "Post created by Cucumber test",
                    "userId": 1
                }
                """, title))
        .when()
            .post("/posts");

        System.out.println("[STEP] Created post: " + title);
    }

    @Then("the user name should be {string}")
    public void theUserNameShouldBe(String expectedName) {
        SerenityRest.lastResponse()
            .then()
            .body("name", equalTo(expectedName));

        System.out.println("[STEP] Verified user name is " + expectedName);
    }

    @Then("the post should be created successfully")
    public void thePostShouldBeCreatedSuccessfully() {
        SerenityRest.lastResponse()
            .then()
            .statusCode(201)
            .body("id", notNullValue());

        System.out.println("[STEP] Verified post was created");
    }

    @Then("the response status should be {int}")
    public void responseStatusShouldBe(int expectedStatus) {
        int actualStatus = SerenityRest.lastResponse().getStatusCode();
        org.assertj.core.api.Assertions.assertThat(actualStatus)
            .as("Response status should be %d", expectedStatus)
            .isEqualTo(expectedStatus);

        System.out.println("[STEP] Verified response status is " + expectedStatus);
    }
}
