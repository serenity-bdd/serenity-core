package net.thucydides.core.webdriver;

import com.google.common.collect.ImmutableList;
import net.thucydides.core.annotations.locators.SmartAjaxElementLocator;
import net.thucydides.core.steps.StepEventBus;
import net.thucydides.core.steps.StepFailure;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;

import java.lang.reflect.Field;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;

public class WhenLocatingWebElementsWithSmartLocator {

    @Mock
    WebDriverFacade driver;

    @Mock
    WebElement webElement;

    @Mock
    StepFailure failure;

    Field field;

    SmartAjaxElementLocator locator;

    static class SomePageObject {

        @FindBy(id = "someId")
        public WebElement someField;

    }

    @Before
    public void initMocks() throws NoSuchFieldException {
        MockitoAnnotations.initMocks(this);

        field = SomePageObject.class.getField("someField");

        StepEventBus.getEventBus().clear();

        when(driver.findElement(By.id("someId"))).thenReturn(webElement);
        when(driver.findElements(By.id("someId"))).thenReturn(ImmutableList.of(webElement));
    }

    @Test(timeout = 2000)
    @Ignore
    public void should_find_elements_immediately_if_a_previous_step_has_failed() {
        SmartAjaxElementLocator locator = new SmartAjaxElementLocator(driver, field, MobilePlatform.NONE);
        StepEventBus.getEventBus().stepFailed(failure);
        locator.findElements();
    }

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    @Ignore
    public void should_wait_for_find_element_if_no_previous_step_has_failed() {

        expectedException.expectMessage(containsString("Timed out after 2 second"));

        SmartAjaxElementLocator locator = new SmartAjaxElementLocator(driver, field, MobilePlatform.NONE);
        locator.findElement();
    }

    @Test
    @Ignore
    public void should_wait_for_find_elements_if_no_previous_step_has_failed() {

        expectedException.expectMessage(containsString("Timed out after 2 second"));

        SmartAjaxElementLocator locator = new SmartAjaxElementLocator(driver, field, MobilePlatform.NONE);
        locator.findElements();
    }


}
