package net.serenitybdd.screenplay.playwright.questions;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.playwright.interactions.CaptureConsoleMessages;
import net.serenitybdd.screenplay.playwright.interactions.CaptureConsoleMessages.CapturedConsoleMessage;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Query captured browser console messages.
 *
 * <p>Before using these questions, you must start capturing console messages
 * using {@link CaptureConsoleMessages#duringTest()}.</p>
 *
 * <p>Sample usage:</p>
 * <pre>
 *     // Start capturing
 *     actor.attemptsTo(CaptureConsoleMessages.duringTest());
 *
 *     // ... perform actions ...
 *
 *     // Query messages
 *     List&lt;String&gt; allMessages = actor.asksFor(ConsoleMessages.all());
 *     List&lt;String&gt; errors = actor.asksFor(ConsoleMessages.errors());
 *     List&lt;String&gt; warnings = actor.asksFor(ConsoleMessages.warnings());
 *     List&lt;String&gt; logs = actor.asksFor(ConsoleMessages.logs());
 *
 *     // Check for specific content
 *     List&lt;String&gt; apiErrors = actor.asksFor(ConsoleMessages.containing("API error"));
 *
 *     // Get full message objects for detailed analysis
 *     List&lt;CapturedConsoleMessage&gt; messages = actor.asksFor(ConsoleMessages.allCaptured());
 * </pre>
 *
 * @see CaptureConsoleMessages
 */
public class ConsoleMessages {

    private ConsoleMessages() {
        // Factory class - prevent instantiation
    }

    /**
     * Get all captured console message texts.
     */
    public static Question<List<String>> all() {
        return new AllMessagesQuestion();
    }

    /**
     * Get all captured error message texts.
     */
    public static Question<List<String>> errors() {
        return new FilteredMessagesQuestion("error", "error");
    }

    /**
     * Get all captured warning message texts.
     */
    public static Question<List<String>> warnings() {
        return new FilteredMessagesQuestion("warning", "warning");
    }

    /**
     * Get all captured log message texts.
     */
    public static Question<List<String>> logs() {
        return new FilteredMessagesQuestion("log", "log");
    }

    /**
     * Get all captured info message texts.
     */
    public static Question<List<String>> info() {
        return new FilteredMessagesQuestion("info", "info");
    }

    /**
     * Get messages containing the specified text.
     *
     * @param substring The text to search for
     */
    public static Question<List<String>> containing(String substring) {
        return new ContainingMessagesQuestion(substring);
    }

    /**
     * Get all captured console messages as full objects.
     * Useful for accessing message type, location, and other details.
     */
    public static Question<List<CapturedConsoleMessage>> allCaptured() {
        return new AllCapturedMessagesQuestion();
    }

    /**
     * Get the count of captured messages.
     */
    public static Question<Integer> count() {
        return new MessageCountQuestion(null);
    }

    /**
     * Get the count of error messages.
     */
    public static Question<Integer> errorCount() {
        return new MessageCountQuestion("error");
    }

    private static List<CapturedConsoleMessage> getMessages(Actor actor) {
        List<CapturedConsoleMessage> messages = actor.recall(CaptureConsoleMessages.getConsoleMessagesKey());
        return messages != null ? messages : Collections.emptyList();
    }

    static class AllMessagesQuestion implements Question<List<String>> {
        @Override
        public List<String> answeredBy(Actor actor) {
            return getMessages(actor).stream()
                .map(CapturedConsoleMessage::getText)
                .collect(Collectors.toList());
        }

        @Override
        public String toString() {
            return "all console messages";
        }
    }

    static class FilteredMessagesQuestion implements Question<List<String>> {
        private final String type;
        private final String description;

        FilteredMessagesQuestion(String type, String description) {
            this.type = type;
            this.description = description;
        }

        @Override
        public List<String> answeredBy(Actor actor) {
            return getMessages(actor).stream()
                .filter(msg -> type.equals(msg.getType()))
                .map(CapturedConsoleMessage::getText)
                .collect(Collectors.toList());
        }

        @Override
        public String toString() {
            return "console " + description + " messages";
        }
    }

    static class ContainingMessagesQuestion implements Question<List<String>> {
        private final String substring;

        ContainingMessagesQuestion(String substring) {
            this.substring = substring;
        }

        @Override
        public List<String> answeredBy(Actor actor) {
            return getMessages(actor).stream()
                .map(CapturedConsoleMessage::getText)
                .filter(text -> text.contains(substring))
                .collect(Collectors.toList());
        }

        @Override
        public String toString() {
            return "console messages containing '" + substring + "'";
        }
    }

    static class AllCapturedMessagesQuestion implements Question<List<CapturedConsoleMessage>> {
        @Override
        public List<CapturedConsoleMessage> answeredBy(Actor actor) {
            return getMessages(actor);
        }

        @Override
        public String toString() {
            return "all captured console messages";
        }
    }

    static class MessageCountQuestion implements Question<Integer> {
        private final String type;

        MessageCountQuestion(String type) {
            this.type = type;
        }

        @Override
        public Integer answeredBy(Actor actor) {
            List<CapturedConsoleMessage> messages = getMessages(actor);
            if (type == null) {
                return messages.size();
            }
            return (int) messages.stream()
                .filter(msg -> type.equals(msg.getType()))
                .count();
        }

        @Override
        public String toString() {
            return type == null ? "console message count" : "console " + type + " count";
        }
    }
}
