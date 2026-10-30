package shop;

import net.serenitybdd.core.pages.PageObject;
import net.serenitybdd.core.pages.WebElementFacade;
import org.openqa.selenium.support.FindBy;

// A small page served from a data: URL, so the example needs no server or network.
public class SignInPage extends PageObject {
    static final String HTML = "<html><head><title>Bookshop</title></head><body>"
            + "<h1 id='heading'>Sign in</h1>"
            + "<input id='email'><input id='password' type='password'>"
            + "<button id='go' onclick=\"if (document.getElementById('password').value == 'correct-horse') {"
            + "document.getElementById('heading').innerText='Catalogue';"
            + "document.getElementById('error').innerText='5 books'} else {"
            + "document.getElementById('error').innerText='Invalid email or password'}\">Sign in</button>"
            + "<p id='error'></p></body></html>";

    @FindBy(id = "email")
    WebElementFacade email;

    @FindBy(id = "password")
    WebElementFacade password;

    @FindBy(id = "go")
    WebElementFacade signInButton;

    @FindBy(id = "heading")
    WebElementFacade heading;

    @FindBy(id = "error")
    WebElementFacade message;

    public void show() {
        getDriver().get("data:text/html;charset=utf-8," + HTML.replace("#", "%23"));
    }

    public void signInWith(String emailAddress, String secret) {
        email.type(emailAddress);
        password.type(secret);
        signInButton.click();
    }

    public String headingText() {
        return heading.getText();
    }

    public String messageText() {
        return message.getText();
    }
}
