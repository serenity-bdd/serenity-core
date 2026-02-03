package tutorials.rest.authentication;

import net.serenitybdd.junit5.SerenityJUnit5Extension;
import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.rest.abilities.CallAnApi;
import net.serenitybdd.screenplay.rest.interactions.Get;
import net.serenitybdd.screenplay.rest.interactions.Put;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import tutorials.rest.model.Booking;
import tutorials.rest.tasks.Authenticate;
import tutorials.rest.tasks.CreateBooking;

import static net.serenitybdd.rest.SerenityRest.given;
import static net.serenitybdd.screenplay.rest.questions.ResponseConsequence.seeThatResponse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Demonstrates authentication patterns with REST APIs.
 * Uses Restful-Booker (https://restful-booker.herokuapp.com) which has real auth.
 */
@ExtendWith(SerenityJUnit5Extension.class)
@DisplayName("REST API Authentication")
class WhenAuthenticatingWithApis {

    private static final String BOOKER_URL = "https://restful-booker.herokuapp.com";

    @Nested
    @DisplayName("Token Authentication with SerenityRest")
    class TokenAuthWithSerenityRest {

        private String authToken;

        @BeforeEach
        void authenticate() {
            // Get authentication token
            authToken = given()
                .baseUri(BOOKER_URL)
                .contentType("application/json")
                .body("""
                    {
                        "username": "admin",
                        "password": "password123"
                    }
                    """)
            .when()
                .post("/auth")
            .then()
                .statusCode(200)
                .extract()
                .path("token");

            assertThat(authToken).isNotNull();
        }

        @Test
        @DisplayName("Should get auth token from API")
        void shouldGetAuthToken() {
            // Token was obtained in @BeforeEach
            assertThat(authToken).isNotEmpty();
        }

        @Test
        @DisplayName("Should create and update booking with token")
        void shouldCreateAndUpdateBookingWithToken() {
            // Create a booking (no auth required)
            int bookingId = given()
                .baseUri(BOOKER_URL)
                .contentType("application/json")
                .accept("application/json")
                .body("""
                    {
                        "firstname": "John",
                        "lastname": "Doe",
                        "totalprice": 150,
                        "depositpaid": true,
                        "bookingdates": {
                            "checkin": "2024-06-01",
                            "checkout": "2024-06-05"
                        },
                        "additionalneeds": "Breakfast"
                    }
                    """)
            .when()
                .post("/booking")
            .then()
                .statusCode(200)
                .extract()
                .path("bookingid");

            // Update booking using token (auth required)
            given()
                .baseUri(BOOKER_URL)
                .contentType("application/json")
                .accept("application/json")
                .cookie("token", authToken)
                .body("""
                    {
                        "firstname": "Jane",
                        "lastname": "Doe",
                        "totalprice": 200,
                        "depositpaid": true,
                        "bookingdates": {
                            "checkin": "2024-06-01",
                            "checkout": "2024-06-05"
                        },
                        "additionalneeds": "Dinner"
                    }
                    """)
            .when()
                .put("/booking/" + bookingId)
            .then()
                .statusCode(200)
                .body("firstname", equalTo("Jane"))
                .body("totalprice", equalTo(200));
        }

        @Test
        @DisplayName("Should delete booking with token")
        void shouldDeleteBookingWithToken() {
            // Create a booking first
            int bookingId = given()
                .baseUri(BOOKER_URL)
                .contentType("application/json")
                .accept("application/json")
                .body("""
                    {
                        "firstname": "Delete",
                        "lastname": "Me",
                        "totalprice": 100,
                        "depositpaid": false,
                        "bookingdates": {
                            "checkin": "2024-07-01",
                            "checkout": "2024-07-03"
                        }
                    }
                    """)
            .when()
                .post("/booking")
            .then()
                .statusCode(200)
                .extract()
                .path("bookingid");

            // Delete with token
            given()
                .baseUri(BOOKER_URL)
                .cookie("token", authToken)
            .when()
                .delete("/booking/" + bookingId)
            .then()
                .statusCode(201); // Restful-Booker returns 201 for successful delete
        }
    }

    @Nested
    @DisplayName("Token Authentication with Screenplay")
    class TokenAuthWithScreenplay {

        Actor admin;

        @BeforeEach
        void setUp() {
            admin = Actor.named("Hotel Admin")
                        .whoCan(CallAnApi.at(BOOKER_URL));
        }

        @Test
        @DisplayName("Should authenticate using custom Authenticate task")
        void shouldAuthenticateUsingTask() {
            admin.attemptsTo(
                Authenticate.asAdmin()
            );

            String token = admin.recall("authToken");
            assertThat(token).isNotNull();
        }

        @Test
        @DisplayName("Should create booking and use token from memory")
        void shouldCreateBookingAndUseTokenFromMemory() {
            // Authenticate and store token
            admin.attemptsTo(
                Authenticate.asAdmin()
            );

            // Create booking (stores bookingId in memory)
            admin.attemptsTo(
                CreateBooking.forGuest("Screenplay", "User")
            );

            Integer bookingId = admin.recall("bookingId");
            assertThat(bookingId).isNotNull();

            // Update using token from memory
            String token = admin.recall("authToken");
            admin.attemptsTo(
                Put.to("/booking/{id}")
                   .with(request -> request
                       .pathParam("id", bookingId)
                       .contentType("application/json")
                       .accept("application/json")
                       .cookie("token", token)
                       .body(new Booking(
                           "Updated", "Name", 300, true,
                           "2024-06-01", "2024-06-10", "Spa access"
                       ))
                   )
            );

            admin.should(
                seeThatResponse("Booking was updated",
                    response -> response
                        .statusCode(200)
                        .body("firstname", equalTo("Updated"))
                )
            );
        }

        @Test
        @DisplayName("Should fetch all bookings")
        void shouldFetchAllBookings() {
            admin.attemptsTo(
                Get.resource("/booking")
            );

            admin.should(
                seeThatResponse("Bookings are returned",
                    response -> response.statusCode(200)
                )
            );
        }
    }

    @Nested
    @DisplayName("Basic Authentication")
    class BasicAuth {

        @Test
        @DisplayName("Should authenticate with Basic Auth")
        void shouldAuthenticateWithBasicAuth() {
            // Using httpbin.org for basic auth testing
            given()
                .baseUri("https://httpbin.org")
                .auth().basic("user", "passwd")
            .when()
                .get("/basic-auth/user/passwd")
            .then()
                .statusCode(200)
                .body("authenticated", equalTo(true))
                .body("user", equalTo("user"));
        }

        @Test
        @DisplayName("Should fail with wrong credentials")
        void shouldFailWithWrongCredentials() {
            given()
                .baseUri("https://httpbin.org")
                .auth().basic("user", "wrongpassword")
            .when()
                .get("/basic-auth/user/passwd")
            .then()
                .statusCode(401);
        }
    }
}
