package net.serenitybdd.screenplay.playwright.interactions.api;

import net.serenitybdd.junit5.SerenityJUnit5Extension;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;
import net.serenitybdd.screenplay.playwright.interactions.ManageCookies;
import net.serenitybdd.screenplay.playwright.interactions.Open;
import net.serenitybdd.screenplay.playwright.questions.api.LastAPIResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests for API testing integration using Playwright's APIRequestContext.
 */
@ExtendWith(SerenityJUnit5Extension.class)
public class APIRequestTest {

    Actor alice;

    @BeforeEach
    void setup() {
        alice = Actor.named("Alice")
            .whoCan(BrowseTheWebWithPlaywright.usingTheDefaultConfiguration());

        // Initialize a page to get the browser context
        alice.attemptsTo(Open.url("about:blank"));
    }

    @Test
    @DisplayName("Should make a GET request and retrieve response")
    void should_make_get_request() {
        alice.attemptsTo(
            APIRequest.get("https://jsonplaceholder.typicode.com/posts/1")
        );

        int statusCode = alice.asksFor(LastAPIResponse.statusCode());
        boolean isOk = alice.asksFor(LastAPIResponse.ok());
        String body = alice.asksFor(LastAPIResponse.body());

        assertThat(statusCode).isEqualTo(200);
        assertThat(isOk).isTrue();
        assertThat(body).contains("userId");
        assertThat(body).contains("title");
    }

    @Test
    @DisplayName("Should parse JSON response as Map")
    void should_parse_json_response_as_map() {
        alice.attemptsTo(
            APIRequest.get("https://jsonplaceholder.typicode.com/posts/1")
        );

        Map<String, Object> json = alice.asksFor(LastAPIResponse.jsonBody());

        assertThat(json).containsKey("userId");
        assertThat(json).containsKey("id");
        assertThat(json).containsKey("title");
        assertThat(json).containsKey("body");
        assertThat(json.get("id")).isEqualTo(1.0); // JSON numbers are doubles
    }

    @Test
    @DisplayName("Should parse JSON array response as List")
    void should_parse_json_array_response() {
        alice.attemptsTo(
            APIRequest.get("https://jsonplaceholder.typicode.com/posts?userId=1")
        );

        List<Map<String, Object>> posts = alice.asksFor(LastAPIResponse.jsonBodyAsList());

        assertThat(posts).isNotEmpty();
        assertThat(posts.get(0)).containsKey("title");
    }

    @Test
    @DisplayName("Should make a POST request with JSON body")
    void should_make_post_request_with_json_body() {
        alice.attemptsTo(
            APIRequest.post("https://jsonplaceholder.typicode.com/posts")
                .withJsonBody(Map.of(
                    "title", "Test Post",
                    "body", "This is a test",
                    "userId", 1
                ))
        );

        int statusCode = alice.asksFor(LastAPIResponse.statusCode());
        Map<String, Object> response = alice.asksFor(LastAPIResponse.jsonBody());

        assertThat(statusCode).isEqualTo(201);
        assertThat(response.get("title")).isEqualTo("Test Post");
        assertThat(response.get("id")).isNotNull();
    }

    @Test
    @DisplayName("Should make a PUT request")
    void should_make_put_request() {
        alice.attemptsTo(
            APIRequest.put("https://jsonplaceholder.typicode.com/posts/1")
                .withJsonBody(Map.of(
                    "id", 1,
                    "title", "Updated Title",
                    "body", "Updated body",
                    "userId", 1
                ))
        );

        int statusCode = alice.asksFor(LastAPIResponse.statusCode());
        Map<String, Object> response = alice.asksFor(LastAPIResponse.jsonBody());

        assertThat(statusCode).isEqualTo(200);
        assertThat(response.get("title")).isEqualTo("Updated Title");
    }

    @Test
    @DisplayName("Should make a PATCH request")
    void should_make_patch_request() {
        alice.attemptsTo(
            APIRequest.patch("https://jsonplaceholder.typicode.com/posts/1")
                .withJsonBody(Map.of("title", "Patched Title"))
        );

        int statusCode = alice.asksFor(LastAPIResponse.statusCode());
        Map<String, Object> response = alice.asksFor(LastAPIResponse.jsonBody());

        assertThat(statusCode).isEqualTo(200);
        assertThat(response.get("title")).isEqualTo("Patched Title");
    }

    @Test
    @DisplayName("Should make a DELETE request")
    void should_make_delete_request() {
        alice.attemptsTo(
            APIRequest.delete("https://jsonplaceholder.typicode.com/posts/1")
        );

        int statusCode = alice.asksFor(LastAPIResponse.statusCode());

        assertThat(statusCode).isEqualTo(200);
    }

    @Test
    @DisplayName("Should add query parameters to request")
    void should_add_query_parameters() {
        alice.attemptsTo(
            APIRequest.get("https://jsonplaceholder.typicode.com/comments")
                .withQueryParam("postId", "1")
        );

        int statusCode = alice.asksFor(LastAPIResponse.statusCode());
        List<Map<String, Object>> comments = alice.asksFor(LastAPIResponse.jsonBodyAsList());

        assertThat(statusCode).isEqualTo(200);
        assertThat(comments).isNotEmpty();
        // All comments should be for postId 1
        assertThat(comments).allMatch(c -> c.get("postId").equals(1.0));
    }

    @Test
    @DisplayName("Should add custom headers to request")
    void should_add_custom_headers() {
        alice.attemptsTo(
            APIRequest.get("https://httpbin.org/headers")
                .withHeader("X-Custom-Header", "test-value")
                .withHeader("X-Another-Header", "another-value")
        );

        int statusCode = alice.asksFor(LastAPIResponse.statusCode());
        Map<String, Object> response = alice.asksFor(LastAPIResponse.jsonBody());

        assertThat(statusCode).isEqualTo(200);
        @SuppressWarnings("unchecked")
        Map<String, String> headers = (Map<String, String>) response.get("headers");
        assertThat(headers.get("X-Custom-Header")).isEqualTo("test-value");
    }

    @Test
    @DisplayName("Should retrieve response headers")
    void should_retrieve_response_headers() {
        alice.attemptsTo(
            APIRequest.get("https://jsonplaceholder.typicode.com/posts/1")
        );

        String contentType = alice.asksFor(LastAPIResponse.header("Content-Type"));
        Map<String, String> allHeaders = alice.asksFor(LastAPIResponse.headers());

        assertThat(contentType).contains("application/json");
        assertThat(allHeaders).isNotEmpty();
    }

    @Test
    @DisplayName("Should retrieve response URL after redirects")
    void should_retrieve_response_url() {
        alice.attemptsTo(
            APIRequest.get("https://jsonplaceholder.typicode.com/posts/1")
        );

        String url = alice.asksFor(LastAPIResponse.url());

        assertThat(url).contains("jsonplaceholder.typicode.com");
        assertThat(url).contains("posts/1");
    }

    @Test
    @DisplayName("Should handle 404 response")
    void should_handle_404_response() {
        alice.attemptsTo(
            APIRequest.get("https://jsonplaceholder.typicode.com/posts/99999")
        );

        int statusCode = alice.asksFor(LastAPIResponse.statusCode());
        boolean isOk = alice.asksFor(LastAPIResponse.ok());

        assertThat(statusCode).isEqualTo(404);
        assertThat(isOk).isFalse();
    }

    @Test
    @DisplayName("Should throw when querying without making request first")
    void should_throw_when_no_request_made() {
        Actor bob = Actor.named("Bob")
            .whoCan(BrowseTheWebWithPlaywright.usingTheDefaultConfiguration());

        bob.attemptsTo(Open.url("about:blank"));

        assertThatThrownBy(() -> bob.asksFor(LastAPIResponse.statusCode()))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("No API response found");

        bob.wrapUp();
    }

    @Test
    @DisplayName("Should use custom HTTP method via to() factory")
    void should_use_custom_http_method() {
        // Using to() factory method for standard methods works the same as direct factory methods
        alice.attemptsTo(
            APIRequest.to("POST", "https://jsonplaceholder.typicode.com/posts")
                .withJsonBody(Map.of("title", "Via to() method", "body", "test", "userId", 1))
        );

        int statusCode = alice.asksFor(LastAPIResponse.statusCode());
        Map<String, Object> response = alice.asksFor(LastAPIResponse.jsonBody());

        assertThat(statusCode).isEqualTo(201);
        assertThat(response.get("title")).isEqualTo("Via to() method");
    }

    @Test
    @DisplayName("Should share cookies with browser context")
    void should_share_cookies_with_browser() {
        // First, navigate to httpbin.org to establish the domain context
        alice.attemptsTo(
            Open.url("https://httpbin.org/html")
        );

        // Set a cookie programmatically (more reliable than using /cookies/set)
        alice.attemptsTo(
            ManageCookies.addCookie("testcookie", "testvalue").forDomain("httpbin.org")
        );

        // API request should include the cookie
        alice.attemptsTo(
            APIRequest.get("https://httpbin.org/cookies")
        );

        // Verify the response is OK before parsing
        Integer statusCode = alice.asksFor(LastAPIResponse.statusCode());
        assertThat(statusCode).isEqualTo(200);

        Map<String, Object> response = alice.asksFor(LastAPIResponse.jsonBody());
        @SuppressWarnings("unchecked")
        Map<String, String> cookies = (Map<String, String>) response.get("cookies");

        assertThat(cookies).containsKey("testcookie");
        assertThat(cookies.get("testcookie")).isEqualTo("testvalue");
    }
}
