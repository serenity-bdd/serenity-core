package net.serenitybdd.core.webdriver.enhancers;

import net.thucydides.model.util.EnvironmentVariables;
import org.openqa.selenium.WebDriver;

import java.time.Duration;

public class SampleDriverEnhancer implements CustomDriverEnhancer {

    public static boolean DRIVER_ENHANCED = false;
    @Override
    public void apply(EnvironmentVariables environmentVariables, WebDriver driver) {
        DRIVER_ENHANCED = true;
    }
}
