package tutorials.rest.screenplay;

import net.serenitybdd.junit5.SerenityJUnit5Extension;
import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.rest.abilities.CallAnApi;
import net.serenitybdd.screenplay.rest.interactions.Get;
import net.serenitybdd.screenplay.rest.interactions.Post;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import tutorials.rest.questions.ThePost;
import tutorials.rest.questions.TheResponse;
import tutorials.rest.questions.TheUser;
import tutorials.rest.tasks.CreatePost;
import tutorials.rest.tasks.FetchUser;

import static net.serenitybdd.screenplay.rest.questions.ResponseConsequence.seeThatResponse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * REST API testing using the Screenplay pattern.
 * Demonstrates actors, tasks, and questions for API testing.
 */
@ExtendWith(SerenityJUnit5Extension.class)
@DisplayName("Screenplay REST API Tests")
class WhenUsingScreenplayForRestTests {

    private static final String BASE_URL = "https://jsonplaceholder.typicode.com";

    Actor alex;

    @BeforeEach
    void setUp() {
        alex = Actor.named("Alex the API tester")
                    .whoCan(CallAnApi.at(BASE_URL));
    }

    @Nested
    @DisplayName("Basic Screenplay Interactions")
    class BasicInteractions {

        @Test
        @DisplayName("Should fetch user using Get interaction")
        void shouldFetchUserUsingGetInteraction() {
            alex.attemptsTo(
                Get.resource("/users/1")
            );

            alex.should(
                seeThatResponse("User details are correct",
                    response -> response
                        .statusCode(200)
                        .body("name", equalTo("Leanne Graham"))
                )
            );
        }

        @Test
        @DisplayName("Should create post using Post interaction")
        void shouldCreatePostUsingPostInteraction() {
            alex.attemptsTo(
                Post.to("/posts")
                    .with(request -> request
                        .contentType("application/json")
                        .body(new tutorials.rest.model.Post("Screenplay Post", "Content", 1))
                    )
            );

            alex.should(
                seeThatResponse("Post was created",
                    response -> response
                        .statusCode(201)
                        .body("title", equalTo("Screenplay Post"))
                )
            );
        }
    }

    @Nested
    @DisplayName("Using Custom Tasks")
    class UsingCustomTasks {

        @Test
        @DisplayName("Should fetch user using custom FetchUser task")
        void shouldFetchUserUsingCustomTask() {
            alex.attemptsTo(
                FetchUser.withId(1)
            );

            alex.should(
                seeThatResponse("User was retrieved",
                    response -> response
                        .statusCode(200)
                        .body("name", notNullValue())
                )
            );
        }

        @Test
        @DisplayName("Should create post using custom CreatePost task")
        void shouldCreatePostUsingCustomTask() {
            alex.attemptsTo(
                CreatePost.withDetails("Custom Task Post", "Body content", 1)
            );

            alex.should(
                seeThatResponse("Post was created successfully",
                    response -> response.statusCode(201)
                )
            );
        }
    }

    @Nested
    @DisplayName("Using Questions")
    class UsingQuestions {

        @Test
        @DisplayName("Should query user details using Questions")
        void shouldQueryUserDetailsUsingQuestions() {
            alex.attemptsTo(
                FetchUser.withId(1)
            );

            String userName = alex.asksFor(TheUser.name());
            String userEmail = alex.asksFor(TheUser.email());

            assertThat(userName).isEqualTo("Leanne Graham");
            assertThat(userEmail).isEqualTo("Sincere@april.biz");
        }

        @Test
        @DisplayName("Should query post details after creation")
        void shouldQueryPostDetailsAfterCreation() {
            alex.attemptsTo(
                CreatePost.withDetails("Question Test", "Testing questions", 2)
            );

            String title = alex.asksFor(ThePost.title());
            Integer postId = alex.asksFor(ThePost.id());

            assertThat(title).isEqualTo("Question Test");
            assertThat(postId).isNotNull();
        }

        @Test
        @DisplayName("Should use generic response questions")
        void shouldUseGenericResponseQuestions() {
            alex.attemptsTo(
                Get.resource("/users")
            );

            Integer statusCode = alex.asksFor(TheResponse.statusCode());
            Integer userCount = alex.asksFor(TheResponse.countOf(""));
            Long responseTime = alex.asksFor(TheResponse.timeInMillis());

            assertThat(statusCode).isEqualTo(200);
            assertThat(userCount).isEqualTo(10);
            assertThat(responseTime).isLessThan(10000L);
        }
    }

    @Nested
    @DisplayName("Chained API Calls")
    class ChainedApiCalls {

        @Test
        @DisplayName("Should create post and remember ID for subsequent calls")
        void shouldChainApiCalls() {
            // Create post and remember the ID
            alex.attemptsTo(
                Post.to("/posts")
                    .with(request -> request
                        .contentType("application/json")
                        .body(new tutorials.rest.model.Post("Chained Call", "Content", 1))
                    )
            );

            // Verify creation and remember the ID
            alex.should(
                seeThatResponse("Post should be created",
                    response -> response
                        .statusCode(201)
                        .body("title", equalTo("Chained Call"))
                )
            );

            Integer postId = SerenityRest.lastResponse().path("id");
            alex.remember("postId", postId);

            // Note: JSONPlaceholder doesn't persist data, so we can't fetch the created post.
            // In a real application, you would fetch the post:
            // alex.attemptsTo(Get.resource("/posts/{id}").with(...));
            // For this demo, we verify the ID was captured
            assertThat(alex.<Integer>recall("postId")).isNotNull();
        }

        @Test
        @DisplayName("Should fetch user then their posts")
        void shouldFetchUserThenTheirPosts() {
            // Get user
            alex.attemptsTo(FetchUser.withId(1));
            Integer userId = alex.asksFor(TheUser.id());

            // Get posts by that user
            alex.attemptsTo(
                Get.resource("/posts")
                   .with(request -> request.queryParam("userId", userId))
            );

            alex.should(
                seeThatResponse("User's posts are returned",
                    response -> response
                        .statusCode(200)
                        .body("size()", greaterThan(0))
                        .body("userId", everyItem(equalTo(userId)))
                )
            );
        }
    }
}
