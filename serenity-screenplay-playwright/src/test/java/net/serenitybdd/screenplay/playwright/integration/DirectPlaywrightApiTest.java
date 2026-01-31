package net.serenitybdd.screenplay.playwright.integration;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.LoadState;
import net.serenitybdd.junit5.SerenityJUnit5Extension;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;
import net.serenitybdd.screenplay.playwright.interactions.Open;
import net.serenitybdd.screenplay.playwright.questions.TheWebPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Demonstrates how to access the Playwright API directly from Screenplay tests.
 *
 * <p>While Serenity Screenplay provides high-level interactions and questions,
 * there are times when you need to access Playwright's API directly for:
 * <ul>
 *   <li>Features not yet wrapped in Screenplay interactions</li>
 *   <li>Complex scenarios requiring fine-grained control</li>
 *   <li>Performance-critical operations</li>
 *   <li>Debugging and exploration</li>
 * </ul>
 */
@ExtendWith(SerenityJUnit5Extension.class)
@DisplayName("Direct Playwright API Access")
public class DirectPlaywrightApiTest {

    Actor alice;

    @BeforeEach
    void setup() {
        alice = Actor.named("Alice")
            .whoCan(BrowseTheWebWithPlaywright.usingTheDefaultConfiguration());
    }

    // ========================================================================
    // SECTION 1: Accessing Playwright Objects
    // ========================================================================

    @Nested
    @DisplayName("Accessing Playwright Objects")
    class AccessingPlaywrightObjects {

        @Test
        @DisplayName("Can access the Page object directly")
        void can_access_page_directly() {
            alice.attemptsTo(Open.url("https://the-internet.herokuapp.com/"));

            // Access the Page object directly
            Page page = BrowseTheWebWithPlaywright.as(alice).getCurrentPage();

            // Use Playwright's native API
            assertThat(page.title()).contains("The Internet");
            assertThat(page.url()).contains("the-internet.herokuapp.com");
        }

        @Test
        @DisplayName("Can access the Browser object")
        void can_access_browser_directly() {
            alice.attemptsTo(Open.url("https://example.com"));

            Browser browser = BrowseTheWebWithPlaywright.as(alice).getBrowser();

            assertThat(browser.isConnected()).isTrue();
            assertThat(browser.browserType().name()).isIn("chromium", "firefox", "webkit");
        }

        @Test
        @DisplayName("Can create Locators directly from Page")
        void can_create_locators_directly() {
            alice.attemptsTo(Open.url("https://the-internet.herokuapp.com/"));

            Page page = BrowseTheWebWithPlaywright.as(alice).getCurrentPage();

            // Create locators using various strategies
            Locator byCSS = page.locator("h1.heading");
            Locator byText = page.getByText("Welcome to the-internet");
            Locator byRole = page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setLevel(1));

            assertThat(byCSS.textContent()).contains("Welcome");
            assertThat(byText.isVisible()).isTrue();
            assertThat(byRole.count()).isGreaterThan(0);
        }
    }

    // ========================================================================
    // SECTION 2: Custom Interactions Using Direct API
    // ========================================================================

    @Nested
    @DisplayName("Custom Interactions")
    class CustomInteractions {

        @Test
        @DisplayName("Can create inline Performable for one-off actions")
        void can_create_inline_performable() {
            // Use anonymous class to create a quick one-off interaction
            Performable scrollDown = new Performable() {
                @Override
                public <T extends Actor> void performAs(T actor) {
                    Page page = BrowseTheWebWithPlaywright.as(actor).getCurrentPage();
                    page.evaluate("window.scrollBy(0, 500)");
                }
            };

            alice.attemptsTo(
                Open.url("https://the-internet.herokuapp.com/"),
                scrollDown
            );

            // Verify scroll happened
            Page page = BrowseTheWebWithPlaywright.as(alice).getCurrentPage();
            Number scrollY = (Number) page.evaluate("window.scrollY");
            assertThat(scrollY.intValue()).isGreaterThanOrEqualTo(0);
        }

        @Test
        @DisplayName("Can create custom Question using direct API")
        void can_create_custom_question() {
            alice.attemptsTo(Open.url("https://the-internet.herokuapp.com/"));

            // Create a custom Question inline
            Question<List<String>> allLinks = actor -> {
                Page page = BrowseTheWebWithPlaywright.as(actor).getCurrentPage();
                return page.locator("a").allTextContents();
            };

            List<String> linkTexts = alice.asksFor(allLinks);
            assertThat(linkTexts).isNotEmpty();
            assertThat(linkTexts).anyMatch(text -> text.contains("Form Authentication"));
        }
    }

    // ========================================================================
    // SECTION 3: Features Available via Direct API (not yet in Screenplay)
    // ========================================================================

    @Nested
    @DisplayName("Direct API Features")
    class DirectApiFeatures {

        @Test
        @DisplayName("Can work with frames using FrameLocator")
        void can_work_with_frames() {
            alice.attemptsTo(Open.url("https://the-internet.herokuapp.com/nested_frames"));

            Page page = BrowseTheWebWithPlaywright.as(alice).getCurrentPage();

            // Use Playwright's frameLocator API with nested frames
            FrameLocator topFrame = page.frameLocator("frame[name='frame-top']");
            FrameLocator leftFrame = topFrame.frameLocator("frame[name='frame-left']");

            // Verify we can access content in the nested frame
            assertThat(leftFrame.locator("body").textContent()).contains("LEFT");
        }

        @Test
        @DisplayName("Can capture element screenshots")
        void can_capture_element_screenshot() {
            alice.attemptsTo(Open.url("https://the-internet.herokuapp.com/"));

            Page page = BrowseTheWebWithPlaywright.as(alice).getCurrentPage();
            Locator heading = page.locator("h1.heading");

            // Capture element screenshot
            Path screenshotPath = Paths.get("target/element-screenshot.png");
            heading.screenshot(new Locator.ScreenshotOptions().setPath(screenshotPath));

            assertThat(screenshotPath.toFile()).exists();
        }

        @Test
        @DisplayName("Can capture full page screenshot")
        void can_capture_full_page_screenshot() {
            alice.attemptsTo(Open.url("https://the-internet.herokuapp.com/"));

            Page page = BrowseTheWebWithPlaywright.as(alice).getCurrentPage();

            Path screenshotPath = Paths.get("target/full-page-screenshot.png");
            page.screenshot(new Page.ScreenshotOptions()
                .setPath(screenshotPath)
                .setFullPage(true));

            assertThat(screenshotPath.toFile()).exists();
        }

        @Test
        @DisplayName("Can listen to console messages")
        void can_listen_to_console_messages() {
            alice.attemptsTo(Open.url("https://the-internet.herokuapp.com/"));

            Page page = BrowseTheWebWithPlaywright.as(alice).getCurrentPage();

            AtomicReference<String> capturedLog = new AtomicReference<>();

            // Set up console listener
            page.onConsoleMessage(msg -> {
                if (msg.type().equals("log")) {
                    capturedLog.set(msg.text());
                }
            });

            // Trigger a console.log
            page.evaluate("console.log('Hello from Playwright!')");

            assertThat(capturedLog.get()).isEqualTo("Hello from Playwright!");
        }

        @Test
        @DisplayName("Can use waitForURL")
        void can_wait_for_url() {
            alice.attemptsTo(Open.url("https://the-internet.herokuapp.com/"));

            Page page = BrowseTheWebWithPlaywright.as(alice).getCurrentPage();

            // Click a link and wait for URL to change
            page.locator("a[href='/login']").click();
            page.waitForURL("**/login");

            assertThat(page.url()).contains("/login");
        }

        @Test
        @DisplayName("Can use keyboard shortcuts")
        void can_use_keyboard_shortcuts() {
            alice.attemptsTo(Open.url("https://the-internet.herokuapp.com/inputs"));

            Page page = BrowseTheWebWithPlaywright.as(alice).getCurrentPage();
            Locator input = page.locator("input[type='number']");

            // Focus and use keyboard
            input.focus();
            input.fill("100");

            // Select all and copy (platform-specific)
            page.keyboard().press("Control+a");

            String value = input.inputValue();
            assertThat(value).isEqualTo("100");
        }

        @Test
        @DisplayName("Can access localStorage")
        void can_access_local_storage() {
            alice.attemptsTo(Open.url("https://the-internet.herokuapp.com/"));

            Page page = BrowseTheWebWithPlaywright.as(alice).getCurrentPage();

            // Set localStorage
            page.evaluate("localStorage.setItem('testKey', 'testValue')");

            // Get localStorage
            String value = (String) page.evaluate("localStorage.getItem('testKey')");
            assertThat(value).isEqualTo("testValue");

            // Clear localStorage
            page.evaluate("localStorage.clear()");
        }

        @Test
        @DisplayName("Can access sessionStorage")
        void can_access_session_storage() {
            alice.attemptsTo(Open.url("https://the-internet.herokuapp.com/"));

            Page page = BrowseTheWebWithPlaywright.as(alice).getCurrentPage();

            // Set sessionStorage
            page.evaluate("sessionStorage.setItem('sessionKey', 'sessionValue')");

            // Get sessionStorage
            String value = (String) page.evaluate("sessionStorage.getItem('sessionKey')");
            assertThat(value).isEqualTo("sessionValue");
        }

        // Note: Tap test removed as it requires touch-enabled context
        // which may not work reliably in all CI environments.
        // To use tap in your tests, create a context with touch enabled:
        // BrowseTheWebWithPlaywright.usingTheDefaultConfiguration()
        //     .withContextOptions(new Browser.NewContextOptions().setHasTouch(true))

        @Test
        @DisplayName("Can use focus and blur")
        void can_use_focus_and_blur() {
            alice.attemptsTo(Open.url("https://the-internet.herokuapp.com/login"));

            Page page = BrowseTheWebWithPlaywright.as(alice).getCurrentPage();
            Locator username = page.locator("#username");
            Locator password = page.locator("#password");

            // Focus on username
            username.focus();
            assertThat((Boolean) page.evaluate(
                "document.activeElement === document.querySelector('#username')")).isTrue();

            // Blur (move focus away)
            username.blur();

            // Focus on password
            password.focus();
            assertThat((Boolean) page.evaluate(
                "document.activeElement === document.querySelector('#password')")).isTrue();
        }
    }

    // ========================================================================
    // SECTION 4: Mixing Screenplay and Direct API
    // ========================================================================

    @Nested
    @DisplayName("Mixing Screenplay and Direct API")
    class MixingScreenplayAndDirectApi {

        @Test
        @DisplayName("Can mix high-level Screenplay with low-level Playwright")
        void can_mix_screenplay_and_direct_api() {
            // Use Screenplay for navigation
            alice.attemptsTo(Open.url("https://the-internet.herokuapp.com/login"));

            // Use direct API for complex assertion
            Page page = BrowseTheWebWithPlaywright.as(alice).getCurrentPage();
            Locator form = page.locator("#login");

            // Playwright's expect-style assertions
            assertThat(form.isVisible()).isTrue();
            assertThat(form.locator("input").count()).isGreaterThanOrEqualTo(2);

            // Back to Screenplay question
            String pageTitle = alice.asksFor(TheWebPage.title());
            assertThat(pageTitle).contains("Internet");
        }

        @Test
        @DisplayName("Can create reusable interaction that wraps direct API")
        void can_create_reusable_wrapped_interaction() {
            // Example of a reusable interaction that uses direct API
            Performable highlightElement = new Performable() {
                private final String selector = "h1.heading";

                @Override
                public <T extends Actor> void performAs(T actor) {
                    Page page = BrowseTheWebWithPlaywright.as(actor).getCurrentPage();
                    Locator element = page.locator(selector);

                    // Highlight the element (useful for debugging)
                    element.highlight();

                    // Add visual indicator
                    page.evaluate("el => el.style.border = '3px solid red'",
                        element.elementHandle());
                }
            };

            alice.attemptsTo(
                Open.url("https://the-internet.herokuapp.com/"),
                highlightElement
            );

            // Verify the element was modified
            Page page = BrowseTheWebWithPlaywright.as(alice).getCurrentPage();
            String border = page.locator("h1.heading").evaluate("el => el.style.border").toString();
            assertThat(border).contains("red");
        }
    }
}
