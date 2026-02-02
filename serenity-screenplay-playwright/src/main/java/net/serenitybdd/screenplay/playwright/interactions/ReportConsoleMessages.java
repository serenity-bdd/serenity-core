package net.serenitybdd.screenplay.playwright.interactions;

import net.serenitybdd.annotations.Step;
import net.serenitybdd.core.Serenity;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.playwright.interactions.CaptureConsoleMessages.CapturedConsoleMessage;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Report captured console messages to the Serenity report as evidence.
 *
 * <p>This interaction attaches console messages to the Serenity report, making
 * them visible in the test results. Use this to include browser console output
 * in your reports for debugging or verification purposes.</p>
 *
 * <p>Sample usage:</p>
 * <pre>
 *     // Capture console messages during the test
 *     actor.attemptsTo(CaptureConsoleMessages.duringTest());
 *
 *     // ... perform actions that may generate console output ...
 *
 *     // Report only errors and warnings to the Serenity report
 *     actor.attemptsTo(ReportConsoleMessages.errorsAndWarnings());
 *
 *     // Or report all messages
 *     actor.attemptsTo(ReportConsoleMessages.all());
 *
 *     // Or report only errors
 *     actor.attemptsTo(ReportConsoleMessages.errors());
 * </pre>
 *
 * @see CaptureConsoleMessages
 * @see net.serenitybdd.screenplay.playwright.questions.ConsoleMessages
 */
public class ReportConsoleMessages implements Performable {

    private static final String CONSOLE_MESSAGES_KEY = "playwright.consoleMessages";

    private final MessageFilter filter;
    private final String reportTitle;
    private final String stepDescription;

    private ReportConsoleMessages(MessageFilter filter, String reportTitle, String stepDescription) {
        this.filter = filter;
        this.reportTitle = reportTitle;
        this.stepDescription = stepDescription;
    }

    /**
     * Report all captured console messages to the Serenity report.
     */
    public static ReportConsoleMessages all() {
        return new ReportConsoleMessages(
            MessageFilter.ALL,
            "Console Messages",
            "reports all console messages"
        );
    }

    /**
     * Report only console errors to the Serenity report.
     */
    public static ReportConsoleMessages errors() {
        return new ReportConsoleMessages(
            MessageFilter.ERRORS_ONLY,
            "Console Errors",
            "reports console errors"
        );
    }

    /**
     * Report only console warnings to the Serenity report.
     */
    public static ReportConsoleMessages warnings() {
        return new ReportConsoleMessages(
            MessageFilter.WARNINGS_ONLY,
            "Console Warnings",
            "reports console warnings"
        );
    }

    /**
     * Report console errors and warnings to the Serenity report.
     * This is the recommended option for most use cases.
     */
    public static ReportConsoleMessages errorsAndWarnings() {
        return new ReportConsoleMessages(
            MessageFilter.ERRORS_AND_WARNINGS,
            "Console Errors & Warnings",
            "reports console errors and warnings"
        );
    }

    @Override
    @Step("{0} #stepDescription")
    public <T extends Actor> void performAs(T actor) {
        List<CapturedConsoleMessage> messages = actor.recall(CONSOLE_MESSAGES_KEY);

        if (messages == null || messages.isEmpty()) {
            return;
        }

        List<CapturedConsoleMessage> filteredMessages = messages.stream()
            .filter(filter.getPredicate())
            .collect(Collectors.toList());

        if (filteredMessages.isEmpty()) {
            return;
        }

        StringBuilder report = new StringBuilder();
        report.append("# ").append(reportTitle).append("\n\n");
        report.append("**").append(filteredMessages.size()).append(" message(s) captured**\n\n");

        for (CapturedConsoleMessage msg : filteredMessages) {
            String typeLabel = formatType(msg.getType());
            report.append("- **[").append(typeLabel).append("]** ").append(msg.getText());

            if (msg.getLocation() != null && !msg.getLocation().isEmpty()) {
                report.append("\n  - Source: `").append(msg.getLocation()).append("`");
            }
            report.append("\n");
        }

        try {
            Serenity.recordReportData()
                .asEvidence()
                .withTitle(reportTitle)
                .andContents(report.toString());
        } catch (Exception e) {
            // Log but don't fail the test due to reporting
            System.err.println("Failed to attach console messages to report: " + e.getMessage());
        }
    }

    private String formatType(String type) {
        if (type == null) {
            return "LOG";
        }
        return type.toUpperCase();
    }

    private enum MessageFilter {
        ALL(msg -> true),
        ERRORS_ONLY(CapturedConsoleMessage::isError),
        WARNINGS_ONLY(CapturedConsoleMessage::isWarning),
        ERRORS_AND_WARNINGS(msg -> msg.isError() || msg.isWarning());

        private final Predicate<CapturedConsoleMessage> predicate;

        MessageFilter(Predicate<CapturedConsoleMessage> predicate) {
            this.predicate = predicate;
        }

        public Predicate<CapturedConsoleMessage> getPredicate() {
            return predicate;
        }
    }
}
