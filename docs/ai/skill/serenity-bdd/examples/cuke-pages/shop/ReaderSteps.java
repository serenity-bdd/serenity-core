package shop;

import net.serenitybdd.annotations.Step;

import static org.assertj.core.api.Assertions.assertThat;

/** The step library: business-level steps that use the page object. Cucumber's glue code calls these. */
public class ReaderSteps {

    SignInPage signInPage;   // page objects are created for you when declared as fields

    @Step("Open the sign-in page")
    public void opensTheSignInPage() {
        signInPage.show();
    }

    @Step("Sign in as {0} with the password {1}")
    public void signsInWith(String email, String password) {
        signInPage.signInWith(email, password);
    }

    @Step("The {0} page should be shown")
    public void shouldSeeThePage(String name) {
        assertThat(signInPage.headingText()).isEqualTo(name);
    }

    @Step("The message {0} should be shown")
    public void shouldBeToldThat(String message) {
        assertThat(signInPage.messageText()).isEqualTo(message);
    }
}
