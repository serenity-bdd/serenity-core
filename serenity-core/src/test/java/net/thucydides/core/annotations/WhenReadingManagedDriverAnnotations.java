package net.thucydides.core.annotations;


import net.serenitybdd.annotations.Managed;
import net.thucydides.core.webdriver.WebDriverFacade;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class WhenReadingManagedDriverAnnotations {

    static final class SampleTestCase {

        public void normalTest(){}

        @Managed
        WebDriver webDriver;

    }

    static final class SampleTestCaseWithNoManagedField {

        public void normalTest(){}

    }

    @Before
    public void initMock() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldFindManagedDriverField() {
        ManagedWebDriverAnnotatedField managedField
                = ManagedWebDriverAnnotatedField.findFirstAnnotatedField(SampleTestCase.class);

        assertThat(managedField, is(not(nullValue())));
    }

    @Test (expected = InvalidManagedWebDriverFieldException.class)
    public void shouldRaiseExceptionIfNoManagedFieldFound() {
        ManagedWebDriverAnnotatedField.findFirstAnnotatedField(SampleTestCaseWithNoManagedField.class);
    }
    
    static final class SampleTestCaseUsingWebDriverFacade {

        public void normalTest(){}

        @Managed
        WebDriverFacade webDriver;
    }
    
    @Test
    public void shouldFindManagedDriverFacadeField() {
        ManagedWebDriverAnnotatedField managedField
                = ManagedWebDriverAnnotatedField.findFirstAnnotatedField(SampleTestCaseUsingWebDriverFacade.class);

        assertThat(managedField, is(not(nullValue())));
    }
    
    static final class SampleTestCaseUsingUniqueSessionWithChrome {

        public void normalTest(){}

        @Managed(uniqueSession = true, driver = "chrome")
        WebDriver webDriver;

    }

    @Test
    public void shouldKnowWhenAUniqueBrowserSessionHasBeenRequested() {
        ManagedWebDriverAnnotatedField managedField
                = ManagedWebDriverAnnotatedField.findFirstAnnotatedField(SampleTestCaseUsingUniqueSessionWithChrome.class);

        assertThat(managedField.isUniqueSession(), is(true));
    }

    @Test
    public void shouldKnowWhatDriverHasBeenRequested() {
        ManagedWebDriverAnnotatedField managedField
                = ManagedWebDriverAnnotatedField.findFirstAnnotatedField(SampleTestCaseUsingUniqueSessionWithChrome.class);

        assertThat(managedField.getDriver(), is("chrome"));
    }

    @Test
    public void shouldAllowMultipleDriversToBeManaged() {
        ManagedWebDriverAnnotatedField managedField
                = ManagedWebDriverAnnotatedField.findFirstAnnotatedField(SampleTestCaseUsingUniqueSessionWithChrome.class);

        assertThat(managedField.getDriver(), is("chrome"));
    }

    static final class SampleTestCaseUsingFirefoxWebDriver {

        public void normalTest(){}

        @Managed
        FirefoxDriver webDriver;
    }
    @Test (expected = InvalidManagedWebDriverFieldException.class)
    public void shouldRaiseExceptionIfNotWebDriverOrFacade() {
        ManagedWebDriverAnnotatedField.findFirstAnnotatedField(SampleTestCaseUsingFirefoxWebDriver.class);
    }

}
