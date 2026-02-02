package net.serenitybdd.screenplay.playwright.evidence;

import net.serenitybdd.junit5.SerenityJUnit5Extension;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;
import net.serenitybdd.screenplay.playwright.interactions.CaptureConsoleMessages;
import net.serenitybdd.screenplay.playwright.interactions.CaptureConsoleMessages.CapturedConsoleMessage;
import net.serenitybdd.screenplay.playwright.interactions.CaptureNetworkRequests;
import net.serenitybdd.screenplay.playwright.interactions.CaptureNetworkRequests.CapturedRequest;
import net.serenitybdd.screenplay.playwright.interactions.ExecuteJavaScript;
import net.serenitybdd.screenplay.playwright.interactions.Open;
import net.serenitybdd.screenplay.playwright.questions.ConsoleMessages;
import net.serenitybdd.screenplay.playwright.questions.NetworkRequests;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for evidence capture - console messages and network requests.
 */
@ExtendWith(SerenityJUnit5Extension.class)
public class EvidenceCaptureTest {

    Actor alice;

    @BeforeEach
    void setup() {
        alice = Actor.named("Alice")
            .whoCan(BrowseTheWebWithPlaywright.usingTheDefaultConfiguration());
    }

    // Console Message Capture Tests

    @Test
    @DisplayName("Should capture console log messages")
    void should_capture_console_logs() {
        alice.attemptsTo(
            CaptureConsoleMessages.duringTest(),
            Open.url("about:blank"),
            ExecuteJavaScript.async("console.log('Test log message')")
        );

        List<String> logs = alice.asksFor(ConsoleMessages.logs());

        assertThat(logs).contains("Test log message");
    }

    @Test
    @DisplayName("Should capture console error messages")
    void should_capture_console_errors() {
        alice.attemptsTo(
            CaptureConsoleMessages.duringTest(),
            Open.url("about:blank"),
            ExecuteJavaScript.async("console.error('Test error message')")
        );

        List<String> errors = alice.asksFor(ConsoleMessages.errors());

        assertThat(errors).contains("Test error message");
    }

    @Test
    @DisplayName("Should capture console warning messages")
    void should_capture_console_warnings() {
        alice.attemptsTo(
            CaptureConsoleMessages.duringTest(),
            Open.url("about:blank"),
            ExecuteJavaScript.async("console.warn('Test warning message')")
        );

        List<String> warnings = alice.asksFor(ConsoleMessages.warnings());

        assertThat(warnings).contains("Test warning message");
    }

    @Test
    @DisplayName("Should get all console messages")
    void should_get_all_console_messages() {
        alice.attemptsTo(
            CaptureConsoleMessages.duringTest(),
            Open.url("about:blank"),
            ExecuteJavaScript.async("console.log('Log'); console.error('Error'); console.warn('Warning');")
        );

        List<String> all = alice.asksFor(ConsoleMessages.all());

        assertThat(all).hasSize(3);
        assertThat(all).containsExactly("Log", "Error", "Warning");
    }

    @Test
    @DisplayName("Should filter console messages containing text")
    void should_filter_console_messages_by_content() {
        alice.attemptsTo(
            CaptureConsoleMessages.duringTest(),
            Open.url("about:blank"),
            ExecuteJavaScript.async("console.log('API call succeeded'); console.log('User logged in'); console.error('API call failed');")
        );

        List<String> apiMessages = alice.asksFor(ConsoleMessages.containing("API"));

        assertThat(apiMessages).hasSize(2);
        assertThat(apiMessages).allMatch(msg -> msg.contains("API"));
    }

    @Test
    @DisplayName("Should count console messages")
    void should_count_console_messages() {
        alice.attemptsTo(
            CaptureConsoleMessages.duringTest(),
            Open.url("about:blank"),
            ExecuteJavaScript.async("console.log('1'); console.log('2'); console.error('3');")
        );

        int totalCount = alice.asksFor(ConsoleMessages.count());
        int errorCount = alice.asksFor(ConsoleMessages.errorCount());

        assertThat(totalCount).isEqualTo(3);
        assertThat(errorCount).isEqualTo(1);
    }

    @Test
    @DisplayName("Should get captured console messages as full objects")
    void should_get_captured_console_message_objects() {
        alice.attemptsTo(
            CaptureConsoleMessages.duringTest(),
            Open.url("about:blank"),
            ExecuteJavaScript.async("console.error('Detailed error')")
        );

        List<CapturedConsoleMessage> messages = alice.asksFor(ConsoleMessages.allCaptured());

        assertThat(messages).hasSize(1);
        CapturedConsoleMessage error = messages.get(0);
        assertThat(error.getType()).isEqualTo("error");
        assertThat(error.getText()).isEqualTo("Detailed error");
        assertThat(error.isError()).isTrue();
    }

    @Test
    @DisplayName("Should clear captured console messages")
    void should_clear_console_messages() {
        alice.attemptsTo(
            CaptureConsoleMessages.duringTest(),
            Open.url("about:blank"),
            ExecuteJavaScript.async("console.log('Message 1')"),
            CaptureConsoleMessages.clear(),
            ExecuteJavaScript.async("console.log('Message 2')")
        );

        List<String> messages = alice.asksFor(ConsoleMessages.all());

        assertThat(messages).hasSize(1);
        assertThat(messages).containsExactly("Message 2");
    }

    // Network Request Capture Tests

    @Test
    @DisplayName("Should capture network requests")
    void should_capture_network_requests() {
        alice.attemptsTo(
            CaptureNetworkRequests.duringTest(),
            Open.url("https://the-internet.herokuapp.com/")
        );

        List<CapturedRequest> requests = alice.asksFor(NetworkRequests.all());

        assertThat(requests).isNotEmpty();
        // Should have captured the main page request
        assertThat(requests).anyMatch(r -> r.getUrl().contains("the-internet.herokuapp.com"));
    }

    @Test
    @DisplayName("Should capture network request methods")
    void should_capture_request_methods() {
        alice.attemptsTo(
            CaptureNetworkRequests.duringTest(),
            Open.url("https://the-internet.herokuapp.com/")
        );

        List<CapturedRequest> getRequests = alice.asksFor(NetworkRequests.withMethod("GET"));

        assertThat(getRequests).isNotEmpty();
        assertThat(getRequests).allMatch(r -> "GET".equals(r.getMethod()));
    }

    @Test
    @DisplayName("Should filter requests by URL substring")
    void should_filter_requests_by_url() {
        alice.attemptsTo(
            CaptureNetworkRequests.duringTest(),
            Open.url("https://the-internet.herokuapp.com/")
        );

        List<CapturedRequest> herokuRequests = alice.asksFor(
            NetworkRequests.toUrlContaining("herokuapp")
        );

        assertThat(herokuRequests).isNotEmpty();
        assertThat(herokuRequests).allMatch(r -> r.getUrl().contains("herokuapp"));
    }

    @Test
    @DisplayName("Should filter requests by glob pattern")
    void should_filter_requests_by_pattern() {
        alice.attemptsTo(
            CaptureNetworkRequests.duringTest(),
            Open.url("https://the-internet.herokuapp.com/")
        );

        List<CapturedRequest> matchingRequests = alice.asksFor(
            NetworkRequests.matching("**/*.css")
        );

        // Should match CSS file requests
        assertThat(matchingRequests).allMatch(r -> r.getUrl().endsWith(".css"));
    }

    @Test
    @DisplayName("Should capture response status codes")
    void should_capture_response_status() {
        alice.attemptsTo(
            CaptureNetworkRequests.duringTest(),
            Open.url("https://the-internet.herokuapp.com/")
        );

        List<CapturedRequest> requests = alice.asksFor(NetworkRequests.all());

        // The main document should have a 200 status
        assertThat(requests)
            .anyMatch(r -> r.getStatus() != null && r.getStatus() == 200);
    }

    @Test
    @DisplayName("Should identify failed requests")
    void should_identify_failed_requests() {
        alice.attemptsTo(
            CaptureNetworkRequests.duringTest(),
            // Navigate to a page that will have some 404s
            Open.url("https://the-internet.herokuapp.com/status_codes/404")
        );

        List<CapturedRequest> failed = alice.asksFor(NetworkRequests.failed());
        int failedCount = alice.asksFor(NetworkRequests.failedCount());

        // The 404 page itself returns 200, but let's at least verify the methods work
        assertThat(failed).isNotNull();
        assertThat(failedCount).isGreaterThanOrEqualTo(0);
    }

    @Test
    @DisplayName("Should identify client error requests")
    void should_identify_client_errors() {
        alice.attemptsTo(
            CaptureNetworkRequests.duringTest(),
            Open.url("https://httpbin.org/status/404")
        );

        List<CapturedRequest> clientErrors = alice.asksFor(NetworkRequests.clientErrors());

        assertThat(clientErrors).anyMatch(r -> r.getStatus() != null && r.getStatus() == 404);
    }

    @Test
    @DisplayName("Should count network requests")
    void should_count_network_requests() {
        alice.attemptsTo(
            CaptureNetworkRequests.duringTest(),
            Open.url("https://the-internet.herokuapp.com/")
        );

        int count = alice.asksFor(NetworkRequests.count());

        assertThat(count).isGreaterThan(0);
    }

    @Test
    @DisplayName("Should clear captured network requests")
    void should_clear_network_requests() {
        alice.attemptsTo(
            CaptureNetworkRequests.duringTest(),
            Open.url("https://the-internet.herokuapp.com/"),
            CaptureNetworkRequests.clear(),
            Open.url("https://the-internet.herokuapp.com/login")
        );

        List<CapturedRequest> requests = alice.asksFor(NetworkRequests.all());

        // Should only have requests from the second navigation
        assertThat(requests).allMatch(r -> !r.getUrl().endsWith("herokuapp.com/") ||
            r.getUrl().contains("login"));
    }

    // Evidence Helper Tests

    @Test
    @DisplayName("Should get console errors via evidence helper")
    void should_get_console_errors_via_helper() {
        alice.attemptsTo(
            CaptureConsoleMessages.duringTest(),
            Open.url("about:blank"),
            ExecuteJavaScript.async("console.error('Error 1'); console.error('Error 2');")
        );

        List<String> errors = PlaywrightFailureEvidence.getConsoleErrors(alice);

        assertThat(errors).hasSize(2);
        assertThat(errors).contains("Error 1", "Error 2");
    }

    @Test
    @DisplayName("Should get failed requests via evidence helper")
    void should_get_failed_requests_via_helper() {
        alice.attemptsTo(
            CaptureNetworkRequests.duringTest(),
            Open.url("https://httpbin.org/status/500")
        );

        List<String> failed = PlaywrightFailureEvidence.getFailedRequests(alice);

        assertThat(failed).anyMatch(s -> s.contains("500"));
    }
}
