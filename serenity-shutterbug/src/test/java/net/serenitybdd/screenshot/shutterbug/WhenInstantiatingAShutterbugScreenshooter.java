package net.serenitybdd.screenshot.shutterbug;

import net.serenitybdd.core.photography.ScreenShooterFactory;
import net.serenitybdd.core.photography.WebDriverPhotoLens;
import net.serenitybdd.screenshots.shutterbug.ShutterbugScreenShooter;
import net.thucydides.model.util.EnvironmentVariables;
import net.thucydides.model.environment.MockEnvironmentVariables;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openqa.selenium.WebDriver;

import static org.assertj.core.api.Assertions.assertThat;

public class WhenInstantiatingAShutterbugScreenshooter {

    @Mock
    WebDriver driver;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shutterbugIsConfigueredUsingTheSerenityConfiguration() {
        EnvironmentVariables environmentVariables = new MockEnvironmentVariables();
        environmentVariables.setProperty("serenity.screenshooter","shutterbug");

        WebDriverPhotoLens photoLens = new WebDriverPhotoLens(driver);
        ScreenShooterFactory factory = new ScreenShooterFactory(environmentVariables);
        assertThat(factory.buildScreenShooter(photoLens)).isInstanceOf(ShutterbugScreenShooter.class);
    }
}
