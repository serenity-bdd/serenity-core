package serenitycore.net.serenitybdd.core.webdriver.driverproviders;

import serenitymodel.net.thucydides.core.util.EnvironmentVariables;
import serenitycore.net.thucydides.core.webdriver.stubs.WebDriverStub;
import org.openqa.selenium.WebDriver;

public class RemoteWebdriverStub {

    public static WebDriver from(EnvironmentVariables environmentVariables) {
        return new WebDriverStub();
    }
}
