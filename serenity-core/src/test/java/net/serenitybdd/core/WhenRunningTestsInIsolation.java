package net.serenitybdd.core;

import net.serenitybdd.annotations.Managed;
import net.serenitybdd.annotations.ManagedPages;
import net.serenitybdd.annotations.Step;
import net.serenitybdd.annotations.Steps;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.steps.ScenarioSteps;
import net.thucydides.model.steps.StepListener;
import net.thucydides.core.webdriver.ThucydidesWebDriverSupport;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

public class WhenRunningTestsInIsolation {


    public static class SampleSteps extends ScenarioSteps {

        public SampleSteps(final Pages pages) {
            super(pages);
        }

        @Step
        public void step1() {}

        @Step
        public void step2() {}
    }

    public class SampleTestClass {
        @Managed
        public WebDriver driver;

        @ManagedPages(defaultUrl = "classpath:static-site/index.html")
        public Pages pages;

        @Steps
        public SampleSteps steps;

    }


    @Mock
    FirefoxDriver mockDriver;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
        ThucydidesWebDriverSupport.closeAllDrivers();
    }

    @After
    public void clearMockDriver() {
        Serenity.stopUsingMockDriver();
        Serenity.done();
    }

    @Test
    public void any_class_can_host_an_annotated_webdriver_instance() {
        SampleTestClass sampleTestClass = new SampleTestClass();

        Serenity.initialize(sampleTestClass);

        assertThat(sampleTestClass.driver, is(not(nullValue())));

    }


    @Test
    public void any_class_can_host_an_annotated_step_library() {
        SampleTestClass sampleTestClass = new SampleTestClass();

        Serenity.initialize(sampleTestClass);

        assertThat(sampleTestClass.steps, is(not(nullValue())));
    }

    @Test
    public void any_class_can_host_an_annotated_pages_library() {
        SampleTestClass sampleTestClass = new SampleTestClass();

        Serenity.initialize(sampleTestClass);

        assertThat(sampleTestClass.pages, is(not(nullValue())));
    }


    public class SampleParentTestClass {
        @Managed
        protected WebDriver driver;

        @ManagedPages(defaultUrl = "classpath:static-site/index.html")
        protected Pages pages;

        @Steps
        protected SampleSteps steps;

    }

    public class SampleChildTestClass extends SampleParentTestClass {}

    @Test
    public void a_web_driver_can_be_defined_in_a_parent_class() {
        SampleChildTestClass sampleTestClass = new SampleChildTestClass();

        Serenity.initialize(sampleTestClass);

        assertThat(sampleTestClass.driver, is(not(nullValue())));

    }

    @Test
    public void a_page_factory_can_be_defined_in_a_parent_class() {
        SampleChildTestClass sampleTestClass = new SampleChildTestClass();

        Serenity.initialize(sampleTestClass);

        assertThat(sampleTestClass.pages, is(not(nullValue())));

    }

    @Test
    public void a_step_library_can_be_defined_in_a_parent_class() {
        SampleChildTestClass sampleTestClass = new SampleChildTestClass();

        Serenity.initialize(sampleTestClass);

        assertThat(sampleTestClass.steps, is(not(nullValue())));

    }

    public class SampleTestClassWithPrivateFields {
        @Managed
        private WebDriver driver;

        @ManagedPages(defaultUrl = "classpath:static-site/index.html")
        private Pages pages;

        @Steps
        private SampleSteps steps;

    }

    @Test
    public void a_web_driver_can_be_defined_as_a_private_field() {
        SampleTestClassWithPrivateFields sampleTestClass = new SampleTestClassWithPrivateFields();

        Serenity.initialize(sampleTestClass);

        assertThat(sampleTestClass.driver, is(not(nullValue())));

    }

    @Test
    public void a_page_factory_can_be_defined_as_a_private_field() {
        SampleTestClassWithPrivateFields sampleTestClass = new SampleTestClassWithPrivateFields();

        Serenity.initialize(sampleTestClass);

        assertThat(sampleTestClass.pages, is(not(nullValue())));

    }

    @Test
    public void a_step_library_can_be_defined_as_a_private_field() {
        SampleTestClassWithPrivateFields sampleTestClass = new SampleTestClassWithPrivateFields();

        Serenity.initialize(sampleTestClass);

        assertThat(sampleTestClass.steps, is(not(nullValue())));

    }
    @Test
    public void a_step_listener_should_be_created() {
        StepListener currentListener = Serenity.getStepListener();

        SampleChildTestClass sampleTestClass = new SampleChildTestClass();

        Serenity.initialize(sampleTestClass);

        assertThat(Serenity.getStepListener(), is(not(currentListener)));

    }

    @Test
    public void no_step_listener_should_be_created() {
        StepListener currentListener = Serenity.getStepListener();

        SampleChildTestClass sampleTestClass = new SampleChildTestClass();

        // Given we don't want to touch the step listened
        Serenity.initializeWithNoStepListener(sampleTestClass);

        // Then the step listener should not be changed
        assertThat(Serenity.getStepListener(), is(currentListener));
    }

}
