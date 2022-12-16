package net.serenitybdd.screenplay.rest.examples;

import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.rest.abilities.CallAnApi;
import net.serenitybdd.screenplay.rest.interactions.Delete;
import net.serenitybdd.screenplay.rest.interactions.Get;
import net.serenitybdd.screenplay.rest.interactions.Post;
import net.serenitybdd.screenplay.rest.interactions.Put;
import net.serenitybdd.screenplay.rest.questions.ResponseConsequence;
import net.serenitybdd.screenplay.rest.questions.RestQuestionBuilder;
import net.serenitybdd.screenplay.rest.questions.TheResponse;
import net.thucydides.core.annotations.Step;
import org.junit.Test;
import org.junit.runner.RunWith;

import static net.serenitybdd.screenplay.GivenWhenThen.seeThat;
import static net.serenitybdd.screenplay.Tasks.instrumented;
import static net.serenitybdd.screenplay.rest.questions.ResponseConsequence.seeThatResponse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;

@RunWith(SerenityRunner.class)
public class WhenInteractingWithAnAPIUsingScreenplay {

    Actor sam = Actor.named("Sam the supervisor").whoCan(CallAnApi.at("https://reqres.in"));

    @Test
    public void list_all_users() {

        Actor sam = Actor.named("Sam the supervisor").whoCan(CallAnApi.at("https://reqres.in"));

        sam.attemptsTo(
                FetchUser.withId(1),
                fetchUserWithId(1),
                Get.resource("/api/users")
        );

        sam.should(
                ResponseConsequence.seeThatResponse("all the expected users should be returned",
                                response -> response.statusCode(200)
                                                    .body("data.first_name",
                                                            hasItems("George", "Janet", "Emma")))
        );
    }

    public Task listAllUsers() {
        return Task.where("{0} lists all users",
                Get.resource("/api/users")
        );
    }

    public Task fetchUserWithId(int userId) {
        return Task.where("{0} fetch user with id #userId",
                Get.resource("/api/users")
        ).with("userId").of(userId);
    }

    public static  class FetchUser implements Task {

        private final int userId;

        public FetchUser(int userId) {
            this.userId = userId;
        }

        public static FetchUser withId(int userId) {
            return instrumented(FetchUser.class, userId);
        }

        @Override
        @Step("{0} fetches the user with id #userId")
        public <T extends Actor> void performAs(T actor) {
            actor.attemptsTo(
                    Get.resource("/api/users/" + userId)
            );
        }
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

    static class TheUser {
        public static Question<String> nameForUserWithId(Long userId) {
            return new RestQuestionBuilder<String>().about("user name")
                    .to("/api/users/{userId}")
                    .with(request -> request.pathParam("userId", userId))
                    .returning(response -> response.path("data.first_name"));
        }

        public static Question<Integer> totalPagesForPage(Integer page) {
            return new RestQuestionBuilder<Integer>().about("user name")
                    .to("/api/users")
                    .withQueryParameters("page", page)
                    .returning(response -> response.path("total_pages"));
        }

    }

    static class Echo {
        public static Question<String> queryParameters(String arg) {
            return new RestQuestionBuilder<String>()
                    .to("/get")
                    .withQueryParameters("foo1", "bar1", "foo2", arg)
                    .returning(response -> response.path("args.foo2"));
        }

        public static Question<String> pathParameters(String arg) {
            return new RestQuestionBuilder<String>()
                    .to("/mock/rest/{path1}/{path2}")
                    .withPathParameters("path1", "85cefea040e1a761f9ddfd2b921d05e2", "path2", arg)
                    .returning(response -> response.path("message"));
        }
    }

    @Test
    public void question_an_individual_user() {

        sam.should(
                seeThat(TheUser.nameForUserWithId(1L), equalTo("George"))
        );
    }

    @Test
    public void question_an_individual_user_using_a_predicate() {

        sam.should(
                seeThat("names should match", TheUser.nameForUserWithId(1L), name -> name.equalsIgnoreCase("George"))
        );
    }

    @Test
    public void question_via_path_params() {

        Actor lucas = Actor.named("Lucas").whoCan(CallAnApi.at("https://extendsclass.com"));

        lucas.should(
                seeThat(Echo.pathParameters("test1"), equalTo("success"))
        );
    }

    @Test
    public void question_via_query_param() {

        sam.should(
                seeThat(TheUser.totalPagesForPage(2), equalTo(2))
        );
    }

    @Test
    public void question_about_status() {
        sam.attemptsTo(
                Get.resource("/api/users/1")
        );

        sam.should(
                seeThat(TheResponse.statusCode(), equalTo(200))
        );
    }

    @Test
    public void question_via_query_params() {

        Actor lucas = Actor.named("Lucas").whoCan(CallAnApi.at("https://postman-echo.com"));

        lucas.should(
                seeThat(Echo.queryParameters("bar2"), equalTo("bar2"))
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
                seeThatResponse(response -> response.statusCode(not(equalTo(200))))
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
                Put.to("/api/users/1")
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
                                .body("{\"email\": \"eve.holt@reqres.in\",\"password\": \"cityslicka\"}")
                        )
        );

        String token = SerenityRest.lastResponse().jsonPath().get("token");

        assertThat(token).isEqualTo("QpwL5tke4Pnpja7X4");
    }
}
