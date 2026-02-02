package net.serenitybdd.screenplay.playwright.interactions;

import net.serenitybdd.junit5.SerenityJUnit5Extension;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;
import net.serenitybdd.screenplay.playwright.questions.Text;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests for session state persistence - saving and restoring browser sessions.
 */
@ExtendWith(SerenityJUnit5Extension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SessionStateTest {

    Actor alice;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setup() {
        alice = Actor.named("Alice")
            .whoCan(BrowseTheWebWithPlaywright.usingTheDefaultConfiguration());
    }

    @Test
    @Order(1)
    @DisplayName("Should save session state to a file")
    void should_save_session_state_to_file() {
        Path statePath = tempDir.resolve("session-state.json");

        alice.attemptsTo(
            // Navigate to a page that sets cookies
            Open.url("https://the-internet.herokuapp.com/"),
            // Save the session state
            SaveSessionState.toPath(statePath)
        );

        // Verify the state file was created
        assertThat(Files.exists(statePath)).isTrue();

        // Verify the file contains JSON content
        try {
            String content = Files.readString(statePath);
            assertThat(content).contains("cookies");
            assertThat(content).contains("origins");
        } catch (Exception e) {
            Assertions.fail("Failed to read session state file: " + e.getMessage());
        }
    }

    @Test
    @Order(2)
    @DisplayName("Should save session state to default location with name")
    void should_save_session_state_to_default_location() {
        alice.attemptsTo(
            Open.url("https://the-internet.herokuapp.com/"),
            SaveSessionState.toFile("test-session")
        );

        // Verify the state file was created in default location
        Path expectedPath = Path.of("target/playwright/session-state/test-session.json");
        assertThat(Files.exists(expectedPath)).isTrue();
    }

    @Test
    @Order(3)
    @DisplayName("Should restore session state from a file")
    void should_restore_session_state_from_file() {
        Path statePath = tempDir.resolve("restore-test-state.json");

        // First, create a session and save its state
        Actor firstSession = Actor.named("FirstSession")
            .whoCan(BrowseTheWebWithPlaywright.usingTheDefaultConfiguration());

        firstSession.attemptsTo(
            Open.url("https://the-internet.herokuapp.com/"),
            SaveSessionState.toPath(statePath)
        );

        // Manually close the first session
        firstSession.wrapUp();

        // Create a new actor and restore the session
        Actor secondSession = Actor.named("SecondSession")
            .whoCan(BrowseTheWebWithPlaywright.usingTheDefaultConfiguration());

        secondSession.attemptsTo(
            RestoreSessionState.fromPath(statePath),
            Open.url("https://the-internet.herokuapp.com/")
        );

        // Verify the page loads correctly with restored session
        String title = secondSession.asksFor(Text.of("h1"));
        assertThat(title).contains("Welcome");

        secondSession.wrapUp();
    }

    @Test
    @Order(4)
    @DisplayName("Should preserve cookies when restoring session")
    void should_preserve_cookies_when_restoring_session() {
        Path statePath = tempDir.resolve("cookie-test-state.json");

        // Create a session with cookies
        Actor firstSession = Actor.named("CookieSession")
            .whoCan(BrowseTheWebWithPlaywright.usingTheDefaultConfiguration());

        firstSession.attemptsTo(
            Open.url("https://the-internet.herokuapp.com/login"),
            // Login to set session cookies
            Enter.theValue("tomsmith").into("#username"),
            Enter.theValue("SuperSecretPassword!").into("#password"),
            Click.on("button[type='submit']"),
            // Save the authenticated state
            SaveSessionState.toPath(statePath)
        );

        // Verify we're logged in
        String flashMessage = firstSession.asksFor(Text.of("#flash"));
        assertThat(flashMessage).contains("You logged into a secure area!");

        firstSession.wrapUp();

        // Restore session in new browser
        Actor secondSession = Actor.named("RestoredSession")
            .whoCan(BrowseTheWebWithPlaywright.usingTheDefaultConfiguration());

        secondSession.attemptsTo(
            RestoreSessionState.fromPath(statePath),
            // Navigate directly to secure area
            Open.url("https://the-internet.herokuapp.com/secure")
        );

        // Verify we're still logged in
        String secureAreaTitle = secondSession.asksFor(Text.of("h2"));
        assertThat(secureAreaTitle).contains("Secure Area");

        secondSession.wrapUp();
    }
}
