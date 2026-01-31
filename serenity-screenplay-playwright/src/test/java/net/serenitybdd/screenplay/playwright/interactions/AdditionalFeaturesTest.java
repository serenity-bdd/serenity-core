package net.serenitybdd.screenplay.playwright.interactions;

import net.serenitybdd.junit5.SerenityJUnit5Extension;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;
import net.serenitybdd.screenplay.playwright.questions.FrameContent;
import net.serenitybdd.screenplay.playwright.questions.StorageValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for additional Playwright features.
 */
@ExtendWith(SerenityJUnit5Extension.class)
@DisplayName("Additional Playwright Features")
public class AdditionalFeaturesTest {

    Actor alice;

    @BeforeEach
    void setup() {
        alice = Actor.named("Alice")
            .whoCan(BrowseTheWebWithPlaywright.usingTheDefaultConfiguration());
    }

    @Nested
    @DisplayName("Focus Interactions")
    class FocusInteractions {

        @Test
        @DisplayName("Can focus on an element")
        void can_focus_on_element() {
            alice.attemptsTo(
                Open.url("https://the-internet.herokuapp.com/login"),
                Focus.on("#username")
            );

            // Verify focus via JavaScript
            Boolean focused = (Boolean) BrowseTheWebWithPlaywright.as(alice)
                .getCurrentPage()
                .evaluate("document.activeElement === document.querySelector('#username')");
            assertThat(focused).isTrue();
        }

        @Test
        @DisplayName("Can blur the active element")
        void can_blur_active_element() {
            alice.attemptsTo(
                Open.url("https://the-internet.herokuapp.com/login"),
                Focus.on("#username"),
                Focus.blur()
            );

            // Verify no input is focused
            Boolean inputFocused = (Boolean) BrowseTheWebWithPlaywright.as(alice)
                .getCurrentPage()
                .evaluate("document.activeElement.tagName === 'INPUT'");
            assertThat(inputFocused).isFalse();
        }
    }

    @Nested
    @DisplayName("Storage Management")
    class StorageManagement {

        @Test
        @DisplayName("Can set and get localStorage values")
        void can_manage_local_storage() {
            alice.attemptsTo(
                Open.url("https://the-internet.herokuapp.com/"),
                ManageStorage.localStorage().setItem("testKey", "testValue")
            );

            String value = alice.asksFor(StorageValue.fromLocalStorage("testKey"));
            assertThat(value).isEqualTo("testValue");
        }

        @Test
        @DisplayName("Can set and get sessionStorage values")
        void can_manage_session_storage() {
            alice.attemptsTo(
                Open.url("https://the-internet.herokuapp.com/"),
                ManageStorage.sessionStorage().setItem("sessionKey", "sessionValue")
            );

            String value = alice.asksFor(StorageValue.fromSessionStorage("sessionKey"));
            assertThat(value).isEqualTo("sessionValue");
        }

        @Test
        @DisplayName("Can clear localStorage")
        void can_clear_local_storage() {
            alice.attemptsTo(
                Open.url("https://the-internet.herokuapp.com/"),
                ManageStorage.localStorage().setItem("key1", "value1"),
                ManageStorage.localStorage().setItem("key2", "value2"),
                ManageStorage.localStorage().clear()
            );

            Map<String, String> items = alice.asksFor(StorageValue.allLocalStorageItems());
            assertThat(items).isEmpty();
        }

        @Test
        @DisplayName("Can get all storage items")
        void can_get_all_storage_items() {
            alice.attemptsTo(
                Open.url("https://the-internet.herokuapp.com/"),
                ManageStorage.localStorage().setItem("key1", "value1"),
                ManageStorage.localStorage().setItem("key2", "value2")
            );

            Map<String, String> items = alice.asksFor(StorageValue.allLocalStorageItems());
            assertThat(items).containsEntry("key1", "value1");
            assertThat(items).containsEntry("key2", "value2");
        }
    }

    @Nested
    @DisplayName("Frame Interactions")
    class FrameInteractions {

        @Test
        @DisplayName("Can check visibility in a frame")
        void can_check_visibility_in_frame() {
            alice.attemptsTo(Open.url("https://the-internet.herokuapp.com/nested_frames"));

            // Use nested frames page which has simpler, more reliable frames
            Boolean visible = alice.asksFor(
                FrameContent.inFrame("frame[name='frame-top']").isVisible("frameset"));
            // The top frame contains a frameset, which should be visible
            assertThat(visible).isTrue();
        }
    }

    @Nested
    @DisplayName("Screenshot Capture")
    class ScreenshotCapture {

        @TempDir
        Path tempDir;

        @Test
        @DisplayName("Can take a page screenshot")
        void can_take_page_screenshot() {
            Path screenshotPath = tempDir.resolve("page.png");

            alice.attemptsTo(
                Open.url("https://the-internet.herokuapp.com/"),
                TakeScreenshot.ofPage().saveTo(screenshotPath)
            );

            assertThat(screenshotPath.toFile()).exists();
            assertThat(screenshotPath.toFile().length()).isGreaterThan(0);
        }

        @Test
        @DisplayName("Can take a full page screenshot")
        void can_take_full_page_screenshot() {
            Path screenshotPath = tempDir.resolve("fullpage.png");

            alice.attemptsTo(
                Open.url("https://the-internet.herokuapp.com/"),
                TakeScreenshot.ofPage().fullPage().saveTo(screenshotPath)
            );

            assertThat(screenshotPath.toFile()).exists();
        }

        @Test
        @DisplayName("Can take an element screenshot")
        void can_take_element_screenshot() {
            Path screenshotPath = tempDir.resolve("element.png");

            alice.attemptsTo(
                Open.url("https://the-internet.herokuapp.com/"),
                TakeScreenshot.ofElement("h1.heading").saveTo(screenshotPath)
            );

            assertThat(screenshotPath.toFile()).exists();
            assertThat(screenshotPath.toFile().length()).isGreaterThan(0);
        }
    }

    // Note: Tap tests are omitted as they require touch-enabled context
    // which may not work reliably in all CI environments.
    // See DirectPlaywrightApiTest for examples of configuring touch-enabled contexts.
}
