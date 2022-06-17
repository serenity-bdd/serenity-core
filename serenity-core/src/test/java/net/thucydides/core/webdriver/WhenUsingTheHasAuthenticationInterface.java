package net.thucydides.core.webdriver;

import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.environment.MockEnvironmentVariables;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.UsernameAndPassword;
import org.openqa.selenium.chrome.ChromeDriver;

import java.net.URI;
import java.util.function.Predicate;

public class WhenUsingTheHasAuthenticationInterface {

    WebDriverFacade driver;
    EnvironmentVariables environmentVariables;

    @Before
    public void initDriver() {
        environmentVariables = new MockEnvironmentVariables();
        environmentVariables.setProperty("headless.mode","true");
        driver = new WebDriverFacade(ChromeDriver.class, new WebDriverFactory(), environmentVariables);
    }

    @Test
    public void shouldAllowTheHasAuthenticationInterfaceIfTheDriverSupportsIt() {
        driver.register(UsernameAndPassword.of("username", "pass"));
    }

    @Test
    public void shouldAllowTheHasAuthenticationInterfaceUsingASupplierIfTheDriverSupportsIt() {
        Predicate<URI> onlyAuthenticateOnTheLoginPage = uri -> uri.getPath().endsWith("/login");
        driver.register(onlyAuthenticateOnTheLoginPage, UsernameAndPassword.of("username", "pass"));
    }


}
