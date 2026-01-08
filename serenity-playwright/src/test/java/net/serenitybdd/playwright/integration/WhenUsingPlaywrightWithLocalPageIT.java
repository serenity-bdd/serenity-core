package net.serenitybdd.playwright.integration;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import net.serenitybdd.annotations.Step;
import net.serenitybdd.junit5.SerenityJUnit5Extension;
import net.serenitybdd.playwright.PlaywrightSerenity;
import net.serenitybdd.annotations.Steps;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration test using local HTML content (no network required).
 * <p>
 * This test demonstrates Playwright integration with Serenity using
 * inline HTML content, making it suitable for CI environments without
 * external network access.
 * </p>
 * <p>
 * Run with: mvn verify -pl serenity-playwright -Dit.test=WhenUsingPlaywrightWithLocalPageIT
 * </p>
 */
@ExtendWith(SerenityJUnit5Extension.class)
@Tag("integration")
class WhenUsingPlaywrightWithLocalPageIT {

    @Steps
    LocalPageSteps steps;

    private static Playwright playwright;
    private static Browser browser;
    private Page page;

    private static final String SAMPLE_HTML = """
        <!DOCTYPE html>
        <html>
        <head>
            <title>Serenity Playwright Test Page</title>
            <style>
                body { font-family: Arial, sans-serif; padding: 20px; }
                h1 { color: #333; }
                .form-group { margin: 10px 0; }
                input, button { padding: 8px; margin: 5px 0; }
                #result { margin-top: 20px; padding: 10px; background: #f0f0f0; }
            </style>
        </head>
        <body>
            <h1>Welcome to Serenity Playwright Integration</h1>
            <div class="form-group">
                <label for="username">Username:</label>
                <input type="text" id="username" name="username" placeholder="Enter username">
            </div>
            <div class="form-group">
                <label for="password">Password:</label>
                <input type="password" id="password" name="password" placeholder="Enter password">
            </div>
            <button id="loginBtn" onclick="handleLogin()">Login</button>
            <div id="result"></div>
            <script>
                function handleLogin() {
                    const username = document.getElementById('username').value;
                    const result = document.getElementById('result');
                    if (username) {
                        result.textContent = 'Welcome, ' + username + '!';
                        result.style.color = 'green';
                    } else {
                        result.textContent = 'Please enter a username';
                        result.style.color = 'red';
                    }
                }
            </script>
        </body>
        </html>
        """;

    @BeforeAll
    static void setupBrowser() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions().setHeadless(true)
        );
    }

    @AfterAll
    static void closeBrowser() {
        if (browser != null) {
            browser.close();
        }
        if (playwright != null) {
            playwright.close();
        }
    }

    @BeforeEach
    void setupPage() {
        page = browser.newPage();
        PlaywrightSerenity.registerPage(page);
        // Load the HTML content
        page.setContent(SAMPLE_HTML);
    }

    @AfterEach
    void closePage() {
        PlaywrightSerenity.unregisterPage(page);
        if (page != null) {
            page.close();
        }
    }

    @Test
    @DisplayName("Should capture screenshot of local HTML page")
    void shouldCaptureScreenshotOfLocalPage() {
        steps.verifyPageTitle(page, "Serenity Playwright Test Page");
        steps.verifyHeadingIsVisible(page, "Welcome to Serenity Playwright Integration");
    }

    @Test
    @DisplayName("Should capture screenshots during form interaction")
    void shouldCaptureScreenshotsDuringFormInteraction() {
        // Fill in the username
        steps.enterUsername(page, "TestUser");

        // Fill in the password
        steps.enterPassword(page, "secret123");

        // Click login
        steps.clickLogin(page);

        // Verify the result
        steps.verifyWelcomeMessage(page, "TestUser");
    }

    @Test
    @DisplayName("Should capture screenshot on validation error")
    void shouldCaptureScreenshotOnValidationError() {
        // Click login without entering username
        steps.clickLogin(page);

        // Verify error message
        steps.verifyErrorMessage(page, "Please enter a username");
    }

    /**
     * Step library for local page testing
     */
    public static class LocalPageSteps {

        @Step("Verify page title is '{1}'")
        public void verifyPageTitle(Page page, String expectedTitle) {
            assertThat(page.title()).isEqualTo(expectedTitle);
        }

        @Step("Verify heading '{1}' is visible")
        public void verifyHeadingIsVisible(Page page, String headingText) {
            assertThat(page.locator("h1").textContent()).contains(headingText);
        }

        @Step("Enter username: {1}")
        public void enterUsername(Page page, String username) {
            page.fill("#username", username);
        }

        @Step("Enter password")
        public void enterPassword(Page page, String password) {
            page.fill("#password", password);
        }

        @Step("Click the login button")
        public void clickLogin(Page page) {
            page.click("#loginBtn");
        }

        @Step("Verify welcome message for user '{1}'")
        public void verifyWelcomeMessage(Page page, String username) {
            String resultText = page.locator("#result").textContent();
            assertThat(resultText).contains("Welcome, " + username);
        }

        @Step("Verify error message: '{1}'")
        public void verifyErrorMessage(Page page, String expectedMessage) {
            String resultText = page.locator("#result").textContent();
            assertThat(resultText).contains(expectedMessage);
        }
    }
}
