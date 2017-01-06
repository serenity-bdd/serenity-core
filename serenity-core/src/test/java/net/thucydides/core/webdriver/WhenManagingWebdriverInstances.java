package net.thucydides.core.webdriver;

import net.thucydides.core.steps.StepEventBus;
import net.thucydides.core.util.MockEnvironmentVariables;
import net.thucydides.core.configuration.SystemPropertiesConfiguration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.openqa.selenium.firefox.FirefoxProfile;

import java.lang.reflect.InvocationTargetException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;

public class WhenManagingWebdriverInstances {

    MockEnvironmentVariables environmentVariables;
    
    WebdriverManager webdriverManager;

    WebDriverFactory factory;

    Configuration configuration; 

    FirefoxProfile firefoxProfile;

    private void initWebdriverManagers() throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {

        firefoxProfile = mock(FirefoxProfile.class);

        factory = new WebDriverFactory(environmentVariables);

        configuration = new SystemPropertiesConfiguration(environmentVariables);
        
        webdriverManager = new SerenityWebdriverManager(factory, configuration);
        webdriverManager.closeAllDrivers();
    }


    @Before
    public void createATestableDriverFactory() throws Exception {
        MockitoAnnotations.initMocks(this);
        environmentVariables = new MockEnvironmentVariables();
        initWebdriverManagers();
        StepEventBus.getEventBus().clearStepFailures();
    }

    @After
    public void closeDriver() {
        webdriverManager.closeAllDrivers();
    }

    @Test
    public void if_an_empty_driver_type_is_specified_the_default_driver_should_be_used() {

        WebDriverFacade defaultDriver = (WebDriverFacade) webdriverManager.getWebdriver();
        WebDriverFacade driver = (WebDriverFacade) webdriverManager.getWebdriver("");

        assertThat(driver, is(defaultDriver));
    }

    @Test
    public void if_a_null_driver_type_is_specified_the_default_driver_should_be_used() {

        WebDriverFacade defaultDriver = (WebDriverFacade) webdriverManager.getWebdriver();
        WebDriverFacade driver = (WebDriverFacade) webdriverManager.getWebdriver(null);

        assertThat(driver, is(defaultDriver));
    }

    @Test
    public void the_default_driver_should_be_the_firefox_driver() {

        WebDriverFacade defaultDriver = (WebDriverFacade) webdriverManager.getWebdriver();
        WebDriverFacade firefoxDriver = (WebDriverFacade) webdriverManager.getWebdriver("firefox");

        assertThat(firefoxDriver, is(defaultDriver));
    }

    @Test
    public void driver_names_should_be_case_insensitive() {

        WebDriverFacade uppercaseFirefoxDriver = (WebDriverFacade) webdriverManager.getWebdriver("Firefox");
        WebDriverFacade firefoxDriver = (WebDriverFacade) webdriverManager.getWebdriver("firefox");

        assertThat(firefoxDriver, is(uppercaseFirefoxDriver));
    }

    @Test
    public void driver_names_for_non_default_drivers_should_be_case_insensitive() {

        WebDriverFacade uppercaseFirefoxDriver = (WebDriverFacade) webdriverManager.getWebdriver("htmlUnit");
        WebDriverFacade firefoxDriver = (WebDriverFacade) webdriverManager.getWebdriver("htmlunit");

        assertThat(firefoxDriver, is(uppercaseFirefoxDriver));
    }


    @Test
    public void the_default_output_directory_can_be_overrided_via_a_system_property() {
        environmentVariables.setProperty("thucydides.outputDirectory","out");

        SystemPropertiesConfiguration config = new SystemPropertiesConfiguration(environmentVariables);

        assertThat(config.getOutputDirectory().getName(), is("out"));
    }


    @Test
    public void should_close_drivers_in_current_thread() {
        SerenityWebdriverManager aWebdriverManager = new SerenityWebdriverManager(factory, configuration);
        aWebdriverManager.getWebdriver("firefox");
        aWebdriverManager.getWebdriver("htmlunit");
        aWebdriverManager.getWebdriver("chrome");

        assertThat(aWebdriverManager.getCurrentActiveWebdriverCount(), is(3));
        aWebdriverManager.closeAllDrivers();
        assertThat(aWebdriverManager.getCurrentActiveWebdriverCount(), is(0));
    }

    @Test
    public void should_close_drivers_in_all_threads() {
        SerenityWebdriverManager aWebdriverManager = new SerenityWebdriverManager(factory, configuration);
        aWebdriverManager.getWebdriver("firefox");
        aWebdriverManager.getWebdriver("htmlunit");
        aWebdriverManager.getWebdriver("chrome");

        assertThat(aWebdriverManager.getActiveWebdriverCount(), is(3));
        aWebdriverManager.closeAllDrivers();
        assertThat(aWebdriverManager.getActiveWebdriverCount(), is(0));
    }

    @Test
    public void should_close_drivers_in_all_threads_after_driver_resets() {
        SerenityWebdriverManager aWebdriverManager = new SerenityWebdriverManager(factory, configuration);
        aWebdriverManager.getWebdriver("firefox");
        aWebdriverManager.getWebdriver("htmlunit");
        aWebdriverManager.getWebdriver("chrome");

        assertThat(aWebdriverManager.getActiveWebdriverCount(), is(3));
        aWebdriverManager.resetDriver();
        aWebdriverManager.closeAllDrivers();
        assertThat(aWebdriverManager.getActiveWebdriverCount(), is(0));
    }
}
