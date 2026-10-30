package api;

import net.serenitybdd.junit5.SerenityJUnit5Extension;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.rest.abilities.CallAnApi;
import net.serenitybdd.screenplay.rest.questions.LastResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static net.serenitybdd.screenplay.GivenWhenThen.seeThat;
import static org.hamcrest.Matchers.equalTo;

@ExtendWith(SerenityJUnit5Extension.class)
class UsersApiTest {

    Actor alice;

    @BeforeEach
    void setUp() {
        alice = Actor.named("Alice").whoCan(CallAnApi.at("https://jsonplaceholder.typicode.com"));
    }

    @Test
    void shouldFindUserOne() {
        alice.attemptsTo(UsersApi.fetchUser(1));
        alice.should(
                seeThat("the status code", actor -> LastResponse.received().answeredBy(actor).statusCode(), equalTo(200)),
                seeThat("the user name", actor -> LastResponse.received().answeredBy(actor).path("name"), equalTo("Leanne Graham")));
    }

    @Test
    void shouldCreateAPost() {
        alice.attemptsTo(UsersApi.createPost("Hello"));
        alice.should(seeThat("the status code", actor -> LastResponse.received().answeredBy(actor).statusCode(), equalTo(201)));
    }
}
