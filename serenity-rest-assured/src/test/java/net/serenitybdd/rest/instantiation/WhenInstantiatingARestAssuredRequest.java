package net.serenitybdd.rest.instantiation;

import io.restassured.RestAssured;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.RequestSender;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import net.serenitybdd.core.Serenity;
import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.rest.decorators.ResponseSpecificationDecorated;
import net.serenitybdd.rest.decorators.request.RequestSpecificationDecorated;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.*;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("When you use RestAssured with Serenity BDD")
class WhenInstantiatingARestAssuredRequest {

    @BeforeEach
    void resetRestListeners() {
        SerenityRest.reset();
    }

    @Test
    @DisplayName("the response for the static methods like expect(), when(), and with() should be wrapped")
    void serenityShouldWrapTheRestAssuredResponse() {
        SoftAssertions softly = new SoftAssertions();

        softly.assertThat(SerenityRest.expect()).isInstanceOf(ResponseSpecificationDecorated.class);
        softly.assertThat(SerenityRest.given()).isInstanceOf(RequestSpecificationDecorated.class);
        softly.assertThat(SerenityRest.when()).isInstanceOf(RequestSpecificationDecorated.class);
        softly.assertThat(SerenityRest.with()).isInstanceOf(RequestSpecificationDecorated.class);

        softly.assertAll();
    }

    @Nested
    @DisplayName("And you start with the given() keyword")
    class StartingWithTheGivenKeyword {
        @Test
        @DisplayName("the request and response should be wrapped")
        void serenityShouldWrapTheRestAssuredResponse() {
            RequestSpecification given = SerenityRest.given();
            ResponseSpecification response = given.response();

            assertThat(given).isInstanceOf(RequestSpecificationDecorated.class);
            assertThat(response).isInstanceOf(ResponseSpecificationDecorated.class);
        }

        @Test
        @DisplayName("it should return wrapped request and response if they initialised separately")
        void wrappedRequestAndResponse() {
            // GIVEN
            RequestSpecification request = SerenityRest.given();
            request = request.proxy(10);

            // WHEN
            RequestSpecification generated = SerenityRest.given(request);

            // THEN
            assertThat(generated).isInstanceOf(RequestSpecificationDecorated.class);
            // AND
            assertThat(((FilterableRequestSpecification) generated).getProxySpecification().getPort()).isEqualTo(10);
        }
    }


}
