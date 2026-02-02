package net.serenitybdd.screenplay.playwright.interactions;

import net.serenitybdd.annotations.Step;
import net.serenitybdd.core.Serenity;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.playwright.interactions.CaptureConsoleMessages.CapturedConsoleMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Check for console errors and warnings, optionally failing the test if any are found.
 *
 * <p>This interaction provides a convenient way to verify that no JavaScript errors
 * or warnings occurred during a test, which is a common quality check for web applications.</p>
 *
 * <p>Sample usage:</p>
 * <pre>
 *     // Setup: Start capturing console messages
 *     actor.attemptsTo(CaptureConsoleMessages.duringTest());
 *
 *     // ... perform actions ...
 *
 *     // Fail the test if any console errors occurred
 *     actor.attemptsTo(CheckConsole.forErrors());
 *
 *     // Fail if any errors OR warnings occurred
 *     actor.attemptsTo(CheckConsole.forErrorsAndWarnings());
 *
 *     // Just report to Serenity without failing the test
 *     actor.attemptsTo(CheckConsole.forErrors().andReportOnly());
 * </pre>
 *
 * @see CaptureConsoleMessages
 * @see ReportConsoleMessages
 */
public class CheckConsole implements Performable {

    private static final String CONSOLE_MESSAGES_KEY = "playwright.consoleMessages";

    private final boolean includeErrors;
    private final boolean includeWarnings;
    private boolean reportOnly = false;

    private CheckConsole(boolean includeErrors, boolean includeWarnings) {
        this.includeErrors = includeErrors;
        this.includeWarnings = includeWarnings;
    }

    /**
     * Check for console errors. Fails the test if any errors are found.
     */
    public static CheckConsole forErrors() {
        return new CheckConsole(true, false);
    }

    /**
     * Check for console warnings. Fails the test if any warnings are found.
     */
    public static CheckConsole forWarnings() {
        return new CheckConsole(false, true);
    }

    /**
     * Check for both console errors and warnings. Fails the test if any are found.
     */
    public static CheckConsole forErrorsAndWarnings() {
        return new CheckConsole(true, true);
    }

    /**
     * Report any found messages to Serenity but don't fail the test.
     * Use this when you want to document console output without enforcing it.
     */
    public CheckConsole andReportOnly() {
        this.reportOnly = true;
        return this;
    }

    @Override
    @Step("{0} checks console for #messageTypeDescription")
    public <T extends Actor> void performAs(T actor) {
        List<CapturedConsoleMessage> allMessages = actor.recall(CONSOLE_MESSAGES_KEY);

        if (allMessages == null || allMessages.isEmpty()) {
            return;
        }

        List<CapturedConsoleMessage> matchingMessages = allMessages.stream()
            .filter(this::matchesFilter)
            .collect(Collectors.toList());

        if (matchingMessages.isEmpty()) {
            return;
        }

        // Build report content
        String report = buildReport(matchingMessages);

        // Always attach to Serenity report
        try {
            Serenity.recordReportData()
                .asEvidence()
                .withTitle("Console " + getMessageTypeDescription())
                .andContents(report);
        } catch (Exception e) {
            System.err.println("Failed to attach console check to report: " + e.getMessage());
        }

        // Fail the test unless reportOnly mode is enabled
        if (!reportOnly) {
            String errorSummary = buildErrorSummary(matchingMessages);
            throw new ConsoleMessagesFoundException(errorSummary);
        }
    }

    private boolean matchesFilter(CapturedConsoleMessage message) {
        if (includeErrors && message.isError()) {
            return true;
        }
        if (includeWarnings && message.isWarning()) {
            return true;
        }
        return false;
    }

    private String buildReport(List<CapturedConsoleMessage> messages) {
        StringBuilder report = new StringBuilder();
        report.append("# Console ").append(getMessageTypeDescription()).append("\n\n");
        report.append("**").append(messages.size()).append(" message(s) found**\n\n");

        for (CapturedConsoleMessage msg : messages) {
            report.append("- **[").append(msg.getType().toUpperCase()).append("]** ")
                .append(msg.getText());
            if (msg.getLocation() != null && !msg.getLocation().isEmpty()) {
                report.append("\n  - Source: `").append(msg.getLocation()).append("`");
            }
            report.append("\n");
        }

        return report.toString();
    }

    private String buildErrorSummary(List<CapturedConsoleMessage> messages) {
        StringBuilder summary = new StringBuilder();
        summary.append("Console ").append(getMessageTypeDescription().toLowerCase())
            .append(" found (").append(messages.size()).append("):\n");

        int maxToShow = Math.min(messages.size(), 5);
        for (int i = 0; i < maxToShow; i++) {
            CapturedConsoleMessage msg = messages.get(i);
            summary.append("  - [").append(msg.getType().toUpperCase()).append("] ")
                .append(truncate(msg.getText(), 100)).append("\n");
        }

        if (messages.size() > maxToShow) {
            summary.append("  ... and ").append(messages.size() - maxToShow).append(" more\n");
        }

        return summary.toString();
    }

    private String truncate(String text, int maxLength) {
        if (text == null) {
            return "";
        }
        if (text.length() <= maxLength) {
            return text;
        }
        return text.substring(0, maxLength - 3) + "...";
    }

    private String getMessageTypeDescription() {
        if (includeErrors && includeWarnings) {
            return "Errors & Warnings";
        } else if (includeErrors) {
            return "Errors";
        } else if (includeWarnings) {
            return "Warnings";
        }
        return "Messages";
    }

    // Used by @Step annotation
    public String getMessageTypeDescription_forStep() {
        return getMessageTypeDescription().toLowerCase();
    }

    /**
     * Exception thrown when console messages are found and reportOnly is false.
     * This exception extends RuntimeException to allow it to be caught by test code
     * when testing the CheckConsole interaction itself.
     */
    public static class ConsoleMessagesFoundException extends RuntimeException {
        public ConsoleMessagesFoundException(String message) {
            super(message);
        }
    }
}
