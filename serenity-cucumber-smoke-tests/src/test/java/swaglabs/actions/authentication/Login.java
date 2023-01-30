package swaglabs.actions.authentication;

import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.actions.Click;
import net.serenitybdd.screenplay.actions.Enter;
import net.serenitybdd.screenplay.actions.InTheBrowser;
import net.serenitybdd.screenplay.ui.Button;
import net.serenitybdd.screenplay.ui.InputField;
import org.openqa.selenium.Cookie;
import swaglabs.model.Customer;

public class Login {
    public static Performable withCredentials(String username, String password) {
        return Task.called("{0} logs in with username " + username + " and password " + password)
                .whereTheActorAttemptsTo(
                        Enter.theValue(username).into(InputField.withNameOrId("Username")),
                        Enter.theValue(password).into(InputField.withNameOrId("Password")),
                        Click.on(Button.withText("Login"))
                );
    }

    public static Performable as(String customerName) {
        return InTheBrowser.perform(
                browser -> {
                    Customer customer = Customer.withName(customerName);
                    Cookie authenticationCookie = new Cookie("session-username",customer.getUsername());
                    browser.getDriver().manage().addCookie(authenticationCookie);
                    browser.getDriver().navigate().refresh();
                }
        );
// Alternative UI implementation:
//        Customer customer = Customer.withName(customerName);
//        return withCredentials(customer.getUsername(), customer.getPassword());
    }

}
