package net.thucydides.core.steps;

import net.serenitybdd.core.pages.PageObject;
import net.serenitybdd.model.time.SystemClock;
import net.thucydides.core.pages.Pages;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openqa.selenium.WebDriver;

import static org.mockito.Mockito.verify;

public class WhenPausingTestExecution {

    @Mock
    SystemClock clock;

    @Mock
    Pages pages;

    @Mock
    WebDriver driver;

    @Mock
    PageObject parent;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void should_pause_test_execution_for_a_millisecond() {

        PageObjectStepDelayer delayer = new PageObjectStepDelayer(clock, parent);
        delayer.waitFor(1).millisecond();

        verify(clock).pauseFor(1);
    }

    @Test
    public void should_pause_test_execution_for_a_specified_number_of_milliseconds() {

        PageObjectStepDelayer delayer = new PageObjectStepDelayer(clock, parent);
        delayer.waitFor(2).milliseconds();

        verify(clock).pauseFor(2);
    }

    @Test
    public void should_pause_test_execution_for_a_minute() {

        PageObjectStepDelayer delayer = new PageObjectStepDelayer(clock, parent);
        delayer.waitFor(1).minute();

        verify(clock).pauseFor(60000);
    }

    @Test
    public void should_pause_test_execution_for_a_specified_number_of_minutes() {

        PageObjectStepDelayer delayer = new PageObjectStepDelayer(clock, parent);
        delayer.waitFor(2).minutes();

        verify(clock).pauseFor(120000);
    }

    @Test
    public void should_pause_test_execution_for_a_second() {

        PageObjectStepDelayer delayer = new PageObjectStepDelayer(clock, parent);
        delayer.waitFor(1).second();

        verify(clock).pauseFor(1000);
    }

    @Test
    public void should_pause_test_execution_for_a_specified_number_of_seconds() {

        PageObjectStepDelayer delayer = new PageObjectStepDelayer(clock, parent);
        delayer.waitFor(2).seconds();

        verify(clock).pauseFor(2000);
    }

    @Test
    public void should_pause_test_execution_for_an_hour() {

        PageObjectStepDelayer delayer = new PageObjectStepDelayer(clock, parent);
        delayer.waitFor(1).hour();

        verify(clock).pauseFor(1000 * 60 * 60);
    }

    @Test
    public void should_pause_test_execution_for_a_specified_number_of_hourss() {

        PageObjectStepDelayer delayer = new PageObjectStepDelayer(clock, parent);
        delayer.waitFor(2).hours();

        verify(clock).pauseFor(2 * 1000 * 60 * 60);
    }

    class PausedScenario extends ScenarioSteps {
        PausedScenario(Pages pages) {
            super(pages);
        }

        public void step1() {}

    }

    @Test
    public void should_pause_step_execution() {
        PausedScenario scenario = new PausedScenario(pages);
        scenario.waitFor(2).milliseconds();
    }

    
    static final class PausedPageObject extends PageObject {

        PausedPageObject(WebDriver driver) {
            super(driver);
        }
    }
    
    @Test
    public void should_pause_page_execution() {
        PausedPageObject page = new PausedPageObject(driver);
        page.waitFor(2).milliseconds();
    }

    @Test
    public void should_chain_after_pauses() {
        PausedPageObject page = new PausedPageObject(driver);

        page.waitFor(2).milliseconds().findAll(".item");
    }

}
