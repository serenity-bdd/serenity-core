package tutorials.rest.basic;

import net.serenitybdd.junit5.SerenityJUnit5Extension;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import tutorials.rest.model.Post;

import static net.serenitybdd.rest.SerenityRest.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Basic REST API operations using SerenityRest.
 * Uses JSONPlaceholder (https://jsonplaceholder.typicode.com) for testing.
 */
@ExtendWith(SerenityJUnit5Extension.class)
@DisplayName("Basic REST Operations")
class WhenPerformingBasicRestOperations {

    private static final String BASE_URL = "https://jsonplaceholder.typicode.com";

    @Nested
    @DisplayName("GET Requests")
    class GetRequests {

        @Test
        @DisplayName("Should fetch a single user by ID")
        void shouldFetchSingleUser() {
            given()
                .baseUri(BASE_URL)
            .when()
                .get("/users/1")
            .then()
                .statusCode(200)
                .body("id", equalTo(1))
                .body("name", equalTo("Leanne Graham"))
                .body("email", equalTo("Sincere@april.biz"));
        }

        @Test
        @DisplayName("Should fetch all users")
        void shouldFetchAllUsers() {
            given()
                .baseUri(BASE_URL)
            .when()
                .get("/users")
            .then()
                .statusCode(200)
                .body("size()", equalTo(10))
                .body("name", hasItems("Leanne Graham", "Ervin Howell"));
        }

        @Test
        @DisplayName("Should filter posts by userId query parameter")
        void shouldFilterPostsByUserId() {
            given()
                .baseUri(BASE_URL)
                .queryParam("userId", 1)
            .when()
                .get("/posts")
            .then()
                .statusCode(200)
                .body("size()", greaterThan(0))
                .body("userId", everyItem(equalTo(1)));
        }

        @Test
        @DisplayName("Should return 404 for non-existent resource")
        void shouldReturn404ForNonExistentResource() {
            given()
                .baseUri(BASE_URL)
            .when()
                .get("/users/99999")
            .then()
                .statusCode(404);
        }
    }

    @Nested
    @DisplayName("POST Requests")
    class PostRequests {

        @Test
        @DisplayName("Should create a new post with JSON string")
        void shouldCreateNewPostWithJsonString() {
            String requestBody = """
                {
                    "title": "My New Post",
                    "body": "This is the content of my post",
                    "userId": 1
                }
                """;

            given()
                .baseUri(BASE_URL)
                .contentType("application/json")
                .body(requestBody)
            .when()
                .post("/posts")
            .then()
                .statusCode(201)
                .body("title", equalTo("My New Post"))
                .body("id", notNullValue());
        }

        @Test
        @DisplayName("Should create a new post using POJO")
        void shouldCreateNewPostUsingPojo() {
            Post newPost = new Post("POJO Title", "Content from POJO", 1);

            Post createdPost = given()
                .baseUri(BASE_URL)
                .contentType("application/json")
                .body(newPost)
            .when()
                .post("/posts")
            .then()
                .statusCode(201)
                .extract()
                .as(Post.class);

            assertThat(createdPost.getTitle()).isEqualTo("POJO Title");
            assertThat(createdPost.getId()).isNotNull();
        }
    }

    @Nested
    @DisplayName("PUT and PATCH Requests")
    class UpdateRequests {

        @Test
        @DisplayName("Should update entire post with PUT")
        void shouldUpdateEntirePostWithPut() {
            Post updatedPost = new Post("Updated Title", "Updated Body", 1);

            given()
                .baseUri(BASE_URL)
                .contentType("application/json")
                .body(updatedPost)
            .when()
                .put("/posts/1")
            .then()
                .statusCode(200)
                .body("title", equalTo("Updated Title"))
                .body("body", equalTo("Updated Body"));
        }

        @Test
        @DisplayName("Should partially update post with PATCH")
        void shouldPartiallyUpdatePostWithPatch() {
            given()
                .baseUri(BASE_URL)
                .contentType("application/json")
                .body("{\"title\": \"Patched Title\"}")
            .when()
                .patch("/posts/1")
            .then()
                .statusCode(200)
                .body("title", equalTo("Patched Title"))
                .body("body", notNullValue()); // Original body preserved
        }
    }

    @Nested
    @DisplayName("DELETE Requests")
    class DeleteRequests {

        @Test
        @DisplayName("Should delete a post")
        void shouldDeletePost() {
            given()
                .baseUri(BASE_URL)
            .when()
                .delete("/posts/1")
            .then()
                .statusCode(200);
        }
    }

    @Nested
    @DisplayName("Data-Driven Tests")
    class DataDrivenTests {

        @ParameterizedTest
        @ValueSource(ints = {1, 2, 3, 4, 5})
        @DisplayName("Should fetch user by ID")
        void shouldFetchUserById(int userId) {
            given()
                .baseUri(BASE_URL)
            .when()
                .get("/users/" + userId)
            .then()
                .statusCode(200)
                .body("id", equalTo(userId));
        }

        @ParameterizedTest
        @CsvSource({
            "1, Leanne Graham, Sincere@april.biz",
            "2, Ervin Howell, Shanna@melissa.tv",
            "3, Clementine Bauch, Nathan@yesenia.net"
        })
        @DisplayName("Should verify user details")
        void shouldVerifyUserDetails(int id, String name, String email) {
            given()
                .baseUri(BASE_URL)
            .when()
                .get("/users/" + id)
            .then()
                .statusCode(200)
                .body("name", equalTo(name))
                .body("email", equalTo(email));
        }
    }

    @Nested
    @DisplayName("Response Extraction")
    class ResponseExtraction {

        @Test
        @DisplayName("Should extract response as POJO")
        void shouldExtractResponseAsPojo() {
            Post post = given()
                .baseUri(BASE_URL)
            .when()
                .get("/posts/1")
            .then()
                .statusCode(200)
                .extract()
                .as(Post.class);

            assertThat(post.getId()).isEqualTo(1);
            assertThat(post.getUserId()).isEqualTo(1);
            assertThat(post.getTitle()).isNotEmpty();
        }

        @Test
        @DisplayName("Should extract specific field from response")
        void shouldExtractSpecificField() {
            String title = given()
                .baseUri(BASE_URL)
            .when()
                .get("/posts/1")
            .then()
                .statusCode(200)
                .extract()
                .path("title");

            assertThat(title).isNotEmpty();
        }
    }
}
