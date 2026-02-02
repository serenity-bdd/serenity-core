package net.serenitybdd.screenplay.playwright.interactions;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for CheckConsole interaction.
 *
 * Note: This test class intentionally does NOT use SerenityJUnit5Extension
 * because we need to test the exception-throwing behavior of CheckConsole,
 * and Serenity's extension intercepts exceptions before they can be caught
 * by test code.
 */
@DisplayName("CheckConsole")
public class CheckConsoleTest {

    Actor alice;

    @BeforeEach
    void setUp() {
        alice = Actor.named("Alice")
            .whoCan(BrowseTheWebWithPlaywright.usingTheDefaultConfiguration());
    }

    @AfterEach
    void cleanUp() {
        // Clean up Playwright resources since we're not using Serenity's extension
        if (alice != null) {
            BrowseTheWebWithPlaywright ability = alice.abilityTo(BrowseTheWebWithPlaywright.class);
            if (ability != null) {
                ability.tearDown();
            }
        }
    }

    @Nested
    @DisplayName("forErrors()")
    class ForErrors {

        @Test
        @DisplayName("Should pass when no errors are present")
        void shouldPassWhenNoErrors() {
            alice.attemptsTo(
                CaptureConsoleMessages.duringTest(),
                Open.url("about:blank"),
                ExecuteJavaScript.async("console.log('Just a log message')"),
                CheckConsole.forErrors()
            );
            // No exception thrown = test passes
        }

        @Test
        @DisplayName("Should fail when errors are present")
        void shouldFailWhenErrorsPresent() {
            alice.attemptsTo(
                CaptureConsoleMessages.duringTest(),
                Open.url("about:blank"),
                ExecuteJavaScript.async("console.error('Something went wrong')")
            );

            CheckConsole.ConsoleMessagesFoundException exception =
                org.junit.jupiter.api.Assertions.assertThrows(
                    CheckConsole.ConsoleMessagesFoundException.class,
                    () -> alice.attemptsTo(CheckConsole.forErrors())
                );

            assertThat(exception.getMessage()).contains("Something went wrong");
        }

        @Test
        @DisplayName("Should ignore warnings when checking for errors only")
        void shouldIgnoreWarningsWhenCheckingErrorsOnly() {
            alice.attemptsTo(
                CaptureConsoleMessages.duringTest(),
                Open.url("about:blank"),
                ExecuteJavaScript.async("console.warn('This is a warning')"),
                CheckConsole.forErrors()
            );
            // No exception thrown because warnings are ignored
        }
    }

    @Nested
    @DisplayName("forWarnings()")
    class ForWarnings {

        @Test
        @DisplayName("Should pass when no warnings are present")
        void shouldPassWhenNoWarnings() {
            alice.attemptsTo(
                CaptureConsoleMessages.duringTest(),
                Open.url("about:blank"),
                ExecuteJavaScript.async("console.log('Just a log message')"),
                CheckConsole.forWarnings()
            );
        }

        @Test
        @DisplayName("Should fail when warnings are present")
        void shouldFailWhenWarningsPresent() {
            alice.attemptsTo(
                CaptureConsoleMessages.duringTest(),
                Open.url("about:blank"),
                ExecuteJavaScript.async("console.warn('Deprecated API usage')")
            );

            try {
                alice.attemptsTo(CheckConsole.forWarnings());
                org.junit.jupiter.api.Assertions.fail("Expected ConsoleMessagesFoundException to be thrown");
            } catch (CheckConsole.ConsoleMessagesFoundException e) {
                assertThat(e.getMessage()).contains("Deprecated API usage");
            }
        }

        @Test
        @DisplayName("Should ignore errors when checking for warnings only")
        void shouldIgnoreErrorsWhenCheckingWarningsOnly() {
            alice.attemptsTo(
                CaptureConsoleMessages.duringTest(),
                Open.url("about:blank"),
                ExecuteJavaScript.async("console.error('This is an error')"),
                CheckConsole.forWarnings()
            );
            // No exception thrown because errors are ignored
        }
    }

    @Nested
    @DisplayName("forErrorsAndWarnings()")
    class ForErrorsAndWarnings {

        @Test
        @DisplayName("Should pass when no errors or warnings are present")
        void shouldPassWhenNoErrorsOrWarnings() {
            alice.attemptsTo(
                CaptureConsoleMessages.duringTest(),
                Open.url("about:blank"),
                ExecuteJavaScript.async("console.log('Just a log message')"),
                CheckConsole.forErrorsAndWarnings()
            );
        }

        @Test
        @DisplayName("Should fail when errors are present")
        void shouldFailWhenErrorsPresent() {
            alice.attemptsTo(
                CaptureConsoleMessages.duringTest(),
                Open.url("about:blank"),
                ExecuteJavaScript.async("console.error('An error')")
            );

            try {
                alice.attemptsTo(CheckConsole.forErrorsAndWarnings());
                org.junit.jupiter.api.Assertions.fail("Expected ConsoleMessagesFoundException to be thrown");
            } catch (CheckConsole.ConsoleMessagesFoundException e) {
                assertThat(e.getMessage()).contains("error");
            }
        }

        @Test
        @DisplayName("Should fail when warnings are present")
        void shouldFailWhenWarningsPresent() {
            alice.attemptsTo(
                CaptureConsoleMessages.duringTest(),
                Open.url("about:blank"),
                ExecuteJavaScript.async("console.warn('A warning')")
            );

            try {
                alice.attemptsTo(CheckConsole.forErrorsAndWarnings());
                org.junit.jupiter.api.Assertions.fail("Expected ConsoleMessagesFoundException to be thrown");
            } catch (CheckConsole.ConsoleMessagesFoundException e) {
                assertThat(e.getMessage()).contains("warning");
            }
        }
    }

    @Nested
    @DisplayName("andReportOnly()")
    class AndReportOnly {

        @Test
        @DisplayName("Should not fail when errors are present in report-only mode")
        void shouldNotFailInReportOnlyMode() {
            alice.attemptsTo(
                CaptureConsoleMessages.duringTest(),
                Open.url("about:blank"),
                ExecuteJavaScript.async("console.error('An error that should not fail the test')"),
                CheckConsole.forErrors().andReportOnly()
            );
            // No exception thrown even though errors are present
        }

        @Test
        @DisplayName("Should not fail when warnings are present in report-only mode")
        void shouldNotFailWarningsInReportOnlyMode() {
            alice.attemptsTo(
                CaptureConsoleMessages.duringTest(),
                Open.url("about:blank"),
                ExecuteJavaScript.async("console.warn('A warning'); console.error('An error')"),
                CheckConsole.forErrorsAndWarnings().andReportOnly()
            );
            // No exception thrown
        }
    }

    @Nested
    @DisplayName("Edge cases")
    class EdgeCases {

        @Test
        @DisplayName("Should handle no console capture gracefully")
        void shouldHandleNoCaptureGracefully() {
            alice.attemptsTo(
                Open.url("about:blank"),
                // Note: CaptureConsoleMessages.duringTest() was NOT called
                CheckConsole.forErrors()
            );
            // Should not throw - just returns normally when no messages captured
        }

        @Test
        @DisplayName("Should include multiple errors in failure message")
        void shouldIncludeMultipleErrorsInFailureMessage() {
            alice.attemptsTo(
                CaptureConsoleMessages.duringTest(),
                Open.url("about:blank"),
                ExecuteJavaScript.async(
                    "console.error('First error');" +
                    "console.error('Second error');" +
                    "console.error('Third error');"
                )
            );

            try {
                alice.attemptsTo(CheckConsole.forErrors());
                org.junit.jupiter.api.Assertions.fail("Expected ConsoleMessagesFoundException to be thrown");
            } catch (CheckConsole.ConsoleMessagesFoundException e) {
                assertThat(e.getMessage()).contains("3");
                assertThat(e.getMessage()).contains("First error");
            }
        }
    }
}
