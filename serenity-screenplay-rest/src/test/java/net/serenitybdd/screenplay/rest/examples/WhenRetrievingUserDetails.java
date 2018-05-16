package net.serenitybdd.screenplay.rest.examples;

import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.rest.abiities.CallAnApi;
import net.serenitybdd.screenplay.rest.interactions.Delete;
import net.serenitybdd.screenplay.rest.interactions.Get;
import net.serenitybdd.screenplay.rest.interactions.Post;
import net.serenitybdd.screenplay.rest.interactions.Put;
import org.junit.Test;
import org.junit.runner.RunWith;

import static net.serenitybdd.screenplay.rest.questions.ResponseConsequence.seeThatResponse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;

@RunWith(SerenityRunner.class)
public class WhenRetrievingUserDetails {

    Actor sam = Actor.named("Sam the supervisor").whoCan(CallAnApi.at("https://reqres.in"));

    @Test
    public void list_all_users() {

        sam.attemptsTo(
                Get.resource("/api/users")
        );

        sam.should(
                seeThatResponse(response -> response.statusCode(200)),
                seeThatResponse(response -> response.body("data.first_name", hasItems("George", "Janet", "Emma")))
        );
    }

    @Test
    public void find_an_individual_user() {

        sam.attemptsTo(
                Get.resource("/api/users/1")
        );

        sam.should(
                seeThatResponse(response -> response.body("data.first_name", equalTo("George")))
        );
    }

    @Test
    public void using_query_parameters() {

        sam.attemptsTo(
                Get.resource("/api/users/{id}").with( request -> request.pathParam("id", 1))
        );

        sam.should(
                seeThatResponse(response -> response.body("data.first_name", equalTo("George")))
        );
    }

    @Test
    public void user_not_found() {

        sam.attemptsTo(
                Get.resource("/api/users/99")
        );

        sam.should(
                seeThatResponse(response -> response.statusCode(404))
        );
    }

    @Test
    public void add_a_new_user() {

        sam.attemptsTo(
                Post.to("/api/users")
                        .with(request -> request.header("Content-Type", "application/json")
                                                .body("{\"firstName\": \"joe\",\"lastName\": \"smith\"}")
                        )
        );

        sam.should(
                seeThatResponse(response -> response.statusCode(201))
        );
    }

    @Test
    public void update_a_user() {

        sam.attemptsTo(
                Put.to("/api/users")
                        .with(request -> request.header("Content-Type", "application/json")
                                .body("{\"firstName\": \"jack\",\"lastName\": \"smith\"}")
                        )
        );

        sam.should(
                seeThatResponse(response -> response.statusCode(200))
        );
    }

    @Test
    public void delete_a_user() {

        sam.attemptsTo(
                Delete.from("/api/users/1")
        );

        sam.should(
                seeThatResponse(response -> response.statusCode(204))
        );
    }

    @Test
    public void login() {

        sam.attemptsTo(
                Post.to("/api/login")
                        .with(request -> request.header("Content-Type", "application/json")
                                .body("{\"email\": \"peter@klaven\",\"password\": \"cityslicka\"}")
                        )
        );

        String token = SerenityRest.lastResponse().jsonPath().get("token");
        assertThat(token).isEqualTo("QpwL5tke4Pnpja7X");
    }
}
