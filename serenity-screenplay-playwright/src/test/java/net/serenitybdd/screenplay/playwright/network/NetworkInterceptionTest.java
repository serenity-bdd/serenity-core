package net.serenitybdd.screenplay.playwright.network;

import net.serenitybdd.junit5.SerenityJUnit5Extension;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;
import net.serenitybdd.screenplay.playwright.interactions.Click;
import net.serenitybdd.screenplay.playwright.interactions.Open;
import net.serenitybdd.screenplay.playwright.questions.Text;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for network interception capabilities.
 */
@ExtendWith(SerenityJUnit5Extension.class)
public class NetworkInterceptionTest {

    Actor alice;

    @BeforeEach
    void setup() {
        alice = Actor.named("Alice")
            .whoCan(BrowseTheWebWithPlaywright.usingTheDefaultConfiguration());
    }

    @Test
    void should_intercept_and_abort_request() {
        alice.attemptsTo(
            // Block image requests
            InterceptNetwork.forUrl("**/*.png").andAbort(),
            InterceptNetwork.forUrl("**/*.jpg").andAbort(),
            Open.url("https://the-internet.herokuapp.com/")
        );

        // Test passes if page loads without errors
        String title = alice.asksFor(Text.of("h1"));
        assertThat(title).contains("Welcome");
    }

    @Test
    void should_intercept_and_fulfill_with_custom_response() {
        alice.attemptsTo(
            // Mock a response
            InterceptNetwork.forUrl("**/status_codes/200")
                .andRespondWith("<html><body><h1>Mocked Response</h1></body></html>")
                .withContentType("text/html"),
            Open.url("https://the-internet.herokuapp.com/status_codes/200")
        );

        String text = alice.asksFor(Text.of("h1"));
        assertThat(text).isEqualTo("Mocked Response");
    }

    @Test
    void should_wait_for_specific_response() {
        // Wait for the response when navigating to a page
        alice.attemptsTo(
            WaitForResponse.containingUrl("login")
                .whilePerforming(Open.url("https://the-internet.herokuapp.com/login"))
        );

        // Page should be loaded
        String text = alice.asksFor(Text.of("h2"));
        assertThat(text).contains("Login");
    }

    @Test
    void should_continue_request_with_modification() {
        alice.attemptsTo(
            // Add a custom header to all requests
            InterceptNetwork.forUrl("**/*").andContinueWithHeader("X-Custom-Header", "test-value"),
            Open.url("https://the-internet.herokuapp.com/")
        );

        // Test passes if page loads successfully
        String title = alice.asksFor(Text.of("h1"));
        assertThat(title).isNotEmpty();
    }
}
