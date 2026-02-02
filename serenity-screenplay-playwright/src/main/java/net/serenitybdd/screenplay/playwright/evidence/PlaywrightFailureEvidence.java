package net.serenitybdd.screenplay.playwright.evidence;

import com.microsoft.playwright.Page;
import net.serenitybdd.core.Serenity;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.playwright.interactions.CaptureConsoleMessages.CapturedConsoleMessage;
import net.serenitybdd.screenplay.playwright.interactions.CaptureNetworkRequests.CapturedRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Collects and attaches failure evidence to Serenity reports.
 *
 * <p>This class gathers diagnostic information when a test fails:</p>
 * <ul>
 *   <li>Console errors and warnings</li>
 *   <li>Failed network requests (4xx, 5xx, network errors)</li>
 *   <li>Current page URL and title</li>
 * </ul>
 *
 * <p>Evidence is automatically attached to the Serenity report for debugging.</p>
 */
public class PlaywrightFailureEvidence {

    private static final String CONSOLE_MESSAGES_KEY = "playwright.consoleMessages";
    private static final String NETWORK_REQUESTS_KEY = "playwright.networkRequests";

    private PlaywrightFailureEvidence() {
        // Utility class
    }

    /**
     * Collect and attach failure evidence from the actor's session.
     *
     * @param actor The actor whose session to collect evidence from
     * @param page The current Playwright page
     */
    public static void captureAndAttach(Actor actor, Page page) {
        StringBuilder evidence = new StringBuilder();
        evidence.append("# Playwright Failure Evidence\n\n");

        // Page info
        evidence.append("## Page Information\n");
        try {
            evidence.append("- **URL:** ").append(page.url()).append("\n");
            evidence.append("- **Title:** ").append(page.title()).append("\n");
        } catch (Exception e) {
            evidence.append("- Unable to capture page info: ").append(e.getMessage()).append("\n");
        }
        evidence.append("\n");

        // Console errors
        List<CapturedConsoleMessage> consoleMessages = actor.recall(CONSOLE_MESSAGES_KEY);
        if (consoleMessages != null && !consoleMessages.isEmpty()) {
            List<CapturedConsoleMessage> errors = consoleMessages.stream()
                .filter(msg -> "error".equals(msg.getType()) || "warning".equals(msg.getType()))
                .collect(Collectors.toList());

            if (!errors.isEmpty()) {
                evidence.append("## Console Errors & Warnings\n");
                for (CapturedConsoleMessage msg : errors) {
                    evidence.append("- **[").append(msg.getType().toUpperCase()).append("]** ")
                        .append(msg.getText());
                    if (msg.getLocation() != null && !msg.getLocation().isEmpty()) {
                        evidence.append(" (").append(msg.getLocation()).append(")");
                    }
                    evidence.append("\n");
                }
                evidence.append("\n");
            }
        }

        // Failed network requests
        List<CapturedRequest> networkRequests = actor.recall(NETWORK_REQUESTS_KEY);
        if (networkRequests != null && !networkRequests.isEmpty()) {
            List<CapturedRequest> failedRequests = networkRequests.stream()
                .filter(CapturedRequest::isFailed)
                .collect(Collectors.toList());

            if (!failedRequests.isEmpty()) {
                evidence.append("## Failed Network Requests\n");
                for (CapturedRequest req : failedRequests) {
                    evidence.append("- **").append(req.getMethod()).append("** `")
                        .append(truncateUrl(req.getUrl())).append("`");
                    if (req.getStatus() != null) {
                        evidence.append(" - ").append(req.getStatus()).append(" ").append(req.getStatusText());
                    } else if (req.getFailureText() != null) {
                        evidence.append(" - FAILED: ").append(req.getFailureText());
                    }
                    evidence.append("\n");
                }
                evidence.append("\n");
            }
        }

        // Only attach if we have meaningful evidence beyond page info
        String evidenceText = evidence.toString();
        if (evidenceText.contains("## Console") || evidenceText.contains("## Failed Network")) {
            try {
                Serenity.recordReportData()
                    .asEvidence()
                    .withTitle("Playwright Failure Evidence")
                    .andContents(evidenceText);
            } catch (Exception e) {
                // Log but don't fail the test due to evidence collection
                System.err.println("Failed to attach Playwright evidence: " + e.getMessage());
            }
        }
    }

    /**
     * Get a summary of captured console errors.
     *
     * @param actor The actor
     * @return List of error message strings
     */
    public static List<String> getConsoleErrors(Actor actor) {
        List<CapturedConsoleMessage> messages = actor.recall(CONSOLE_MESSAGES_KEY);
        if (messages == null) {
            return new ArrayList<>();
        }
        return messages.stream()
            .filter(msg -> "error".equals(msg.getType()))
            .map(CapturedConsoleMessage::getText)
            .collect(Collectors.toList());
    }

    /**
     * Get a summary of failed network requests.
     *
     * @param actor The actor
     * @return List of failed request descriptions
     */
    public static List<String> getFailedRequests(Actor actor) {
        List<CapturedRequest> requests = actor.recall(NETWORK_REQUESTS_KEY);
        if (requests == null) {
            return new ArrayList<>();
        }
        return requests.stream()
            .filter(CapturedRequest::isFailed)
            .map(CapturedRequest::toString)
            .collect(Collectors.toList());
    }

    private static String truncateUrl(String url) {
        if (url.length() > 100) {
            return url.substring(0, 97) + "...";
        }
        return url;
    }
}
