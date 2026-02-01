package net.serenitybdd.screenplay.playwright.interactions;

import com.microsoft.playwright.ConsoleMessage;
import com.microsoft.playwright.Page;
import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * Start capturing browser console messages for later analysis.
 *
 * <p>Console messages include logs, warnings, errors, and other output
 * from JavaScript running in the browser.</p>
 *
 * <p>Sample usage:</p>
 * <pre>
 *     // Start capturing all console messages
 *     actor.attemptsTo(CaptureConsoleMessages.duringTest());
 *
 *     // ... perform actions that generate console output ...
 *
 *     // Query captured messages
 *     List&lt;String&gt; errors = actor.asksFor(ConsoleMessages.errors());
 *     List&lt;String&gt; warnings = actor.asksFor(ConsoleMessages.warnings());
 *     List&lt;String&gt; allMessages = actor.asksFor(ConsoleMessages.all());
 *
 *     // Clear captured messages
 *     actor.attemptsTo(CaptureConsoleMessages.clear());
 * </pre>
 *
 * @see net.serenitybdd.screenplay.playwright.questions.ConsoleMessages
 */
public class CaptureConsoleMessages implements Performable {

    static final String CONSOLE_MESSAGES_KEY = "playwright.consoleMessages";
    static final String CONSOLE_LISTENER_KEY = "playwright.consoleListener";

    private final boolean clear;

    private CaptureConsoleMessages(boolean clear) {
        this.clear = clear;
    }

    /**
     * Start capturing console messages during the test.
     * Messages will be stored and can be queried using ConsoleMessages questions.
     */
    public static CaptureConsoleMessages duringTest() {
        return new CaptureConsoleMessages(false);
    }

    /**
     * Clear all previously captured console messages.
     */
    public static CaptureConsoleMessages clear() {
        return new CaptureConsoleMessages(true);
    }

    @Override
    @Step("{0} starts capturing console messages")
    public <T extends Actor> void performAs(T actor) {
        if (clear) {
            // Clear existing messages
            List<CapturedConsoleMessage> messages = actor.recall(CONSOLE_MESSAGES_KEY);
            if (messages != null) {
                messages.clear();
            }
            return;
        }

        // Check if listener is already registered
        Consumer<ConsoleMessage> existingListener = actor.recall(CONSOLE_LISTENER_KEY);
        if (existingListener != null) {
            // Already capturing
            return;
        }

        Page page = BrowseTheWebWithPlaywright.as(actor).getCurrentPage();

        // Use thread-safe list for console messages
        List<CapturedConsoleMessage> messages = new CopyOnWriteArrayList<>();
        actor.remember(CONSOLE_MESSAGES_KEY, messages);

        // Create and register the listener
        Consumer<ConsoleMessage> listener = consoleMessage -> {
            messages.add(new CapturedConsoleMessage(
                consoleMessage.type(),
                consoleMessage.text(),
                consoleMessage.location()
            ));
        };

        actor.remember(CONSOLE_LISTENER_KEY, listener);
        page.onConsoleMessage(listener);
    }

    /**
     * Get the key used to store console messages in actor's memory.
     */
    public static String getConsoleMessagesKey() {
        return CONSOLE_MESSAGES_KEY;
    }

    /**
     * Represents a captured console message with its type, text, and location.
     */
    public static class CapturedConsoleMessage {
        private final String type;
        private final String text;
        private final String location;

        public CapturedConsoleMessage(String type, String text, String location) {
            this.type = type;
            this.text = text;
            this.location = location;
        }

        /**
         * Get the message type (e.g., "log", "error", "warning", "info").
         */
        public String getType() {
            return type;
        }

        /**
         * Get the message text.
         */
        public String getText() {
            return text;
        }

        /**
         * Get the source location (URL and line number).
         */
        public String getLocation() {
            return location;
        }

        /**
         * Check if this is an error message.
         */
        public boolean isError() {
            return "error".equals(type);
        }

        /**
         * Check if this is a warning message.
         */
        public boolean isWarning() {
            return "warning".equals(type);
        }

        /**
         * Check if this is an info message.
         */
        public boolean isInfo() {
            return "info".equals(type);
        }

        /**
         * Check if this is a log message.
         */
        public boolean isLog() {
            return "log".equals(type);
        }

        @Override
        public String toString() {
            return "[" + type + "] " + text;
        }
    }
}
