package net.serenitybdd.screenplay.playwright.interactions.api;

import net.serenitybdd.junit5.SerenityJUnit5Extension;
import net.serenitybdd.model.rest.RestQuery;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;
import net.serenitybdd.screenplay.playwright.interactions.Open;
import net.thucydides.core.steps.StepEventBus;
import net.thucydides.model.domain.TestStep;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests to verify that Playwright API requests are recorded to Serenity reports,
 * similar to how RestAssured API calls are recorded.
 */
@ExtendWith(SerenityJUnit5Extension.class)
public class APIRequestRecordingTest {

    Actor alice;

    @BeforeEach
    void setup() {
        alice = Actor.named("Alice")
            .whoCan(BrowseTheWebWithPlaywright.usingTheDefaultConfiguration());

        // Initialize a page to get the browser context
        alice.attemptsTo(Open.url("about:blank"));
    }

    @Test
    @DisplayName("GET requests should be recorded for Serenity reports")
    void get_requests_should_be_recorded() {
        alice.attemptsTo(
            APIRequest.get("https://jsonplaceholder.typicode.com/posts/1")
        );

        // Verify the REST query was recorded
        RestQuery restQuery = findLastRestQuery();

        assertThat(restQuery).isNotNull();
        assertThat(restQuery.getMethod().name()).isEqualTo("GET");
        assertThat(restQuery.getPath()).contains("jsonplaceholder.typicode.com/posts/1");
        assertThat(restQuery.getStatusCode()).isEqualTo(200);
        assertThat(restQuery.getResponseBody()).contains("userId");
    }

    @Test
    @DisplayName("POST requests with body should be recorded with request content")
    void post_requests_should_record_body() {
        alice.attemptsTo(
            APIRequest.post("https://jsonplaceholder.typicode.com/posts")
                .withJsonBody(Map.of(
                    "title", "Test Post",
                    "body", "This is test content",
                    "userId", 1
                ))
        );

        RestQuery restQuery = findLastRestQuery();

        assertThat(restQuery).isNotNull();
        assertThat(restQuery.getMethod().name()).isEqualTo("POST");
        assertThat(restQuery.getContent()).contains("Test Post");
        assertThat(restQuery.getContent()).contains("This is test content");
        assertThat(restQuery.getContentType()).isEqualTo("application/json");
        assertThat(restQuery.getStatusCode()).isEqualTo(201);
    }

    @Test
    @DisplayName("Request headers should be recorded")
    void request_headers_should_be_recorded() {
        alice.attemptsTo(
            APIRequest.get("https://httpbin.org/headers")
                .withHeader("X-Test-Header", "test-value")
                .withHeader("X-Another", "another-value")
        );

        RestQuery restQuery = findLastRestQuery();

        assertThat(restQuery).isNotNull();
        assertThat(restQuery.getRequestHeaders()).contains("X-Test-Header");
        assertThat(restQuery.getRequestHeaders()).contains("test-value");
    }

    @Test
    @DisplayName("Response headers should be recorded")
    void response_headers_should_be_recorded() {
        alice.attemptsTo(
            APIRequest.get("https://jsonplaceholder.typicode.com/posts/1")
        );

        RestQuery restQuery = findLastRestQuery();

        assertThat(restQuery).isNotNull();
        assertThat(restQuery.getResponseHeaders()).isNotEmpty();
        assertThat(restQuery.getResponseHeaders().toLowerCase()).contains("content-type");
    }

    @Test
    @DisplayName("Error responses should be recorded")
    void error_responses_should_be_recorded() {
        alice.attemptsTo(
            APIRequest.get("https://jsonplaceholder.typicode.com/posts/99999")
        );

        RestQuery restQuery = findLastRestQuery();

        assertThat(restQuery).isNotNull();
        assertThat(restQuery.getStatusCode()).isEqualTo(404);
    }

    @Test
    @DisplayName("PUT requests should be recorded")
    void put_requests_should_be_recorded() {
        alice.attemptsTo(
            APIRequest.put("https://jsonplaceholder.typicode.com/posts/1")
                .withJsonBody(Map.of("title", "Updated"))
        );

        RestQuery restQuery = findLastRestQuery();

        assertThat(restQuery).isNotNull();
        assertThat(restQuery.getMethod().name()).isEqualTo("PUT");
        assertThat(restQuery.getStatusCode()).isEqualTo(200);
    }

    @Test
    @DisplayName("DELETE requests should be recorded")
    void delete_requests_should_be_recorded() {
        alice.attemptsTo(
            APIRequest.delete("https://jsonplaceholder.typicode.com/posts/1")
        );

        RestQuery restQuery = findLastRestQuery();

        assertThat(restQuery).isNotNull();
        assertThat(restQuery.getMethod().name()).isEqualTo("DELETE");
    }

    /**
     * Find the last REST query recorded in the current test's step hierarchy.
     */
    private RestQuery findLastRestQuery() {
        List<TestStep> steps = StepEventBus.getEventBus().getBaseStepListener()
            .getCurrentTestOutcome().getTestSteps();

        return findRestQueryInSteps(steps);
    }

    private RestQuery findRestQueryInSteps(List<TestStep> steps) {
        // Search from last to first to find the most recent REST query
        for (int i = steps.size() - 1; i >= 0; i--) {
            TestStep step = steps.get(i);
            if (step.hasRestQuery()) {
                return step.getRestQuery();
            }
            // Check nested steps
            if (step.hasChildren()) {
                RestQuery query = findRestQueryInSteps(step.getChildren());
                if (query != null) {
                    return query;
                }
            }
        }
        return null;
    }
}
