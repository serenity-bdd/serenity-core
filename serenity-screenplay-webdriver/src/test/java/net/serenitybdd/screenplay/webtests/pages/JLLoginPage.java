package net.serenitybdd.screenplay.webtests.pages;

import net.serenitybdd.core.annotations.findby.FindBy;
import net.serenitybdd.core.pages.PageObject;
import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.screenplay.targets.Target;
import net.thucydides.core.annotations.DefaultUrl;

@DefaultUrl("https://uat.joblogic.com/Account/LogOn")
public class JLLoginPage extends PageObject {
    // Job Logic selectors
    public static Target EMAIL = Target.the("Email").locatedBy("#UserName");
    public static Target PASSWORD = Target.the("Password").locatedBy("#Password");
    public static Target LOGIN = Target.the("Login").locatedBy("#loginButton");

    @FindBy(id="UserName")
    public WebElementFacade txtEmail;

    @FindBy(id="Password")
    public WebElementFacade txtPassword;

    @FindBy(id="loginButton")
    public WebElementFacade btnLogin;

    public void loginJLPage(String username, String password) {
        txtEmail.sendKeys(username);
        txtPassword.sendKeys(password);
        btnLogin.click();
        //$("#UserName").type(username);
        //$("#Password").type(password);
        //$(BTNLOGIN).click();
    }
}
