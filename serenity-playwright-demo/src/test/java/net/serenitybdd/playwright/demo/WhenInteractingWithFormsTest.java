package net.serenitybdd.playwright.demo;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import net.serenitybdd.annotations.Steps;
import net.serenitybdd.junit5.SerenityJUnit5Extension;
import net.serenitybdd.playwright.PlaywrightSerenity;
import net.serenitybdd.playwright.demo.steps.FormSteps;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Demonstrates form interactions with Serenity Playwright.
 * <p>
 * This test uses inline HTML content, making it suitable for CI environments
 * without external network access. It shows how Serenity captures screenshots
 * during form interactions.
 * </p>
 */
@ExtendWith(SerenityJUnit5Extension.class)
@DisplayName("When interacting with forms")
class WhenInteractingWithFormsTest {

    @Steps
    FormSteps form;

    private static Playwright playwright;
    private static Browser browser;
    private Page page;

    // Sample HTML form for testing
    private static final String LOGIN_FORM_HTML = """
        <!DOCTYPE html>
        <html>
        <head>
            <title>Login Form Demo</title>
            <style>
                body { font-family: Arial, sans-serif; padding: 40px; background: #f5f5f5; }
                .container { max-width: 400px; margin: 0 auto; background: white; padding: 30px; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
                h1 { color: #333; margin-bottom: 20px; }
                .form-group { margin-bottom: 15px; }
                label { display: block; margin-bottom: 5px; font-weight: bold; color: #555; }
                input[type="text"], input[type="password"], input[type="email"] {
                    width: 100%; padding: 10px; border: 1px solid #ddd; border-radius: 4px; font-size: 14px;
                }
                input:focus { outline: none; border-color: #007bff; }
                button { width: 100%; padding: 12px; background: #007bff; color: white; border: none; border-radius: 4px; font-size: 16px; cursor: pointer; }
                button:hover { background: #0056b3; }
                .message { margin-top: 20px; padding: 15px; border-radius: 4px; }
                .success { background: #d4edda; color: #155724; }
                .error { background: #f8d7da; color: #721c24; }
                .checkbox-group { display: flex; align-items: center; gap: 8px; }
            </style>
        </head>
        <body>
            <div class="container">
                <h1>Welcome Back</h1>
                <form id="loginForm">
                    <div class="form-group">
                        <label for="email">Email Address</label>
                        <input type="email" id="email" name="email" placeholder="Enter your email">
                    </div>
                    <div class="form-group">
                        <label for="password">Password</label>
                        <input type="password" id="password" name="password" placeholder="Enter your password">
                    </div>
                    <div class="form-group checkbox-group">
                        <input type="checkbox" id="remember" name="remember">
                        <label for="remember" style="margin-bottom: 0;">Remember me</label>
                    </div>
                    <button type="button" id="loginBtn" onclick="handleLogin()">Sign In</button>
                </form>
                <div id="message"></div>
            </div>
            <script>
                function handleLogin() {
                    const email = document.getElementById('email').value;
                    const password = document.getElementById('password').value;
                    const message = document.getElementById('message');

                    if (!email || !password) {
                        message.className = 'message error';
                        message.textContent = 'Please fill in all fields';
                        return;
                    }

                    if (!email.includes('@')) {
                        message.className = 'message error';
                        message.textContent = 'Please enter a valid email address';
                        return;
                    }

                    message.className = 'message success';
                    message.textContent = 'Welcome back, ' + email.split('@')[0] + '!';
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
        if (browser != null) browser.close();
        if (playwright != null) playwright.close();
    }

    @BeforeEach
    void setupPage() {
        page = browser.newPage();
        PlaywrightSerenity.registerPage(page);
        page.setContent(LOGIN_FORM_HTML);
    }

    @AfterEach
    void closePage() {
        PlaywrightSerenity.unregisterPage(page);
        if (page != null) page.close();
    }

    @Test
    @DisplayName("Should fill and submit a login form")
    void shouldFillAndSubmitLoginForm() {
        // Fill in the form fields
        form.enterIntoField(page, "#email", "john.doe@example.com");
        form.enterIntoField(page, "#password", "secretpassword123");

        // Check the remember me checkbox
        form.checkCheckbox(page, "#remember");

        // Submit the form
        form.clickOn(page, "#loginBtn");

        // Verify success message
        form.verifyElementContainsText(page, "#message", "Welcome back, john.doe");
    }

    @Test
    @DisplayName("Should show validation error for empty fields")
    void shouldShowValidationErrorForEmptyFields() {
        // Try to submit without filling fields
        form.clickOn(page, "#loginBtn");

        // Verify error message
        form.verifyElementContainsText(page, "#message", "Please fill in all fields");
    }

    @Test
    @DisplayName("Should show error for invalid email")
    void shouldShowErrorForInvalidEmail() {
        // Enter invalid email
        form.enterIntoField(page, "#email", "invalid-email");
        form.enterIntoField(page, "#password", "somepassword");

        // Submit
        form.clickOn(page, "#loginBtn");

        // Verify error message
        form.verifyElementContainsText(page, "#message", "valid email");
    }

    @Test
    @DisplayName("Should verify form elements are visible")
    void shouldVerifyFormElementsAreVisible() {
        form.verifyElementIsVisible(page, "#email");
        form.verifyElementIsVisible(page, "#password");
        form.verifyElementIsVisible(page, "#remember");
        form.verifyElementIsVisible(page, "#loginBtn");
    }
}
