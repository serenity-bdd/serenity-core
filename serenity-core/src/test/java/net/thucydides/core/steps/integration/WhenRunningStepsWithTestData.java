package net.thucydides.core.steps.integration;

import net.serenitybdd.annotations.Step;
import net.serenitybdd.annotations.StepGroup;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.steps.*;
import net.thucydides.model.steps.StepFailure;
import net.thucydides.model.steps.StepListener;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openqa.selenium.WebDriver;
import sample.csv.TestPageObject;

import java.io.IOException;

import static net.thucydides.core.steps.stepdata.StepData.setDefaultStepFactory;
import static net.thucydides.core.steps.stepdata.StepData.withTestDataFrom;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


public class WhenRunningStepsWithTestData {


    @Mock
    WebDriver driver;

    @Mock
    StepListener listener;

    private StepFactory factory;

    static class TestSteps extends ScenarioSteps {

        private String name;
        private String address;
        private String dateOfBirth;
        
        public TestPageObject testPage;
    	
    	public void verifyPage(){
    		getDriver().get(testPage.toString());
    	}

        public TestSteps(Pages pages) {
            super(pages);
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getDateOfBirth() {
            return dateOfBirth;
        }

        public void setDateOfBirth(String dateOfBirth) {
            this.dateOfBirth = dateOfBirth;
        }

        @StepGroup
        public void step_group1() {
            step1();
            step2();
        }

        @StepGroup
        public void name_and_dob() {
            step1();
            step3();
        }


        @Step
        public TestSteps step1() {
            getDriver().get(name);
            return this;
        }

        @Step
        public TestSteps step2() {
            getDriver().get(address);
            return this;
        }

        @Step
        public TestSteps step3() {
            getDriver().get(dateOfBirth);
            return this;
        }

        @Step
        public void fail_sometimes() {
            getDriver().get(name);
            if (name.equals("Joe")) {
                throw new AssertionError("Bad name");
            }
        }

    }


    static class DifferentTestSteps extends ScenarioSteps {

        private String name;
        private String address;

        public DifferentTestSteps(Pages pages) {
            super(pages);
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        @Step
        public void nameStep() {
            getDriver().get(name);
        }

        @Step
        public void addressStep() {
            getDriver().get(address);
        }
    }


    public static class TestStepsWithNoSetters extends ScenarioSteps {

        public String name;
        public String address;

        public TestStepsWithNoSetters(Pages pages) {
            super(pages);
        }

        @Step
        public void nameStep() {
            getDriver().get(name);
        }

        @Step
        public void addressStep() {
            getDriver().get(address);
        }
    }

    public static class TestStepsWithNoSettersAndInaccessibleFields extends ScenarioSteps {

        private String name;
        private String address;

        public TestStepsWithNoSettersAndInaccessibleFields(Pages pages) {
            super(pages);
        }

        @Step
        public void nameStep() {
            getDriver().get(name);
        }

        @Step
        public void addressStep() {
            getDriver().get(address);
        }
    }

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
        factory = StepFactory.getFactory().usingPages(new Pages(driver));

        StepEventBus.getParallelEventBus().reset();
        StepEventBus.getParallelEventBus().registerListener(listener);
        setDefaultStepFactory(null);
    }

    @Test
    public void should_invoke_step_method_for_each_row_in_the_csv_file() throws IOException {

        TestSteps steps = factory.getSharedStepLibraryFor(TestSteps.class);

        withTestDataFrom("testdata/test.csv").usingFactory(factory).run(steps).step1();

        verify(driver, times(3)).get(anyString());
    }

    @Test
    public void should_invoke_step_method_for_each_row_in_a_list_of_strings() throws IOException {

        TestSteps steps = factory.getSharedStepLibraryFor(TestSteps.class);

        withTestDataFrom(
                "Name, Address, Date of birth",
                "Bill, 1 main street, 10/10/1970",
                "Joe, 2 main street, 10 / 10 / 1971",
                "Mary, 4 main street, 10/10/1972"
        ).usingFactory(factory).run(steps).step1();

        verify(driver, times(3)).get(anyString());
    }

    @Test
    public void should_pass_test_data_into_invoked_methods() throws IOException {

        TestSteps steps = factory.getSharedStepLibraryFor(TestSteps.class);

        StepEventBus.getParallelEventBus().testStarted("some test");
        withTestDataFrom("testdata/test.csv").usingFactory(factory).run(steps).step1();

        verify(driver).get("Bill");
        verify(driver).get("Joe");
        verify(driver).get("Mary");
    }

    @Test
    public void should_execute_all_tests_even_if_one_of_the_steps_fails() throws IOException {

        TestSteps steps = factory.getSharedStepLibraryFor(TestSteps.class);

        StepEventBus.getParallelEventBus().testStarted("some test");
        withTestDataFrom("testdata/test.csv").usingFactory(factory).run(steps).fail_sometimes();

        verify(driver).get("Bill");
        verify(driver).get("Joe");
        verify(driver).get("Mary");
    }

    @Test
    public void should_notify_listeners_if_one_of_the_steps_fails() throws IOException {

        TestSteps steps = factory.getSharedStepLibraryFor(TestSteps.class);

        StepEventBus.getParallelEventBus().testStarted("some test");
        withTestDataFrom("testdata/test.csv")
                .usingFactory(factory)
                .run(steps).fail_sometimes();

        verify(listener).stepFailed(any(StepFailure.class));
    }


    @Test
    public void should_be_able_to_define_a_default_factory_before_the_step() throws IOException {

        TestSteps steps = factory.getSharedStepLibraryFor(TestSteps.class);

        setDefaultStepFactory(factory);

        StepEventBus.getParallelEventBus().testStarted("some test");
        withTestDataFrom("testdata/test.csv")
                .run(steps).step1();

        verify(driver).get("Bill");
        verify(driver).get("Joe");
        verify(driver).get("Mary");
    }

    @Test
    public void should_be_able_to_define_a_default_test_step_class_before_the_step() throws IOException {

        TestSteps steps = factory.getSharedStepLibraryFor(TestSteps.class);

        StepEventBus.getParallelEventBus().testStarted("some test");
        withTestDataFrom("testdata/test.csv").usingFactory(factory).run(steps).step1();

        verify(driver).get("Bill");
        verify(driver).get("Joe");
        verify(driver).get("Mary");
    }

    @Test
    public void should_be_able_to_define_default_factory_and_test_step_class_before_the_step() throws IOException {

        TestSteps steps = factory.getSharedStepLibraryFor(TestSteps.class);

        setDefaultStepFactory(factory);

        StepEventBus.getParallelEventBus().testStarted("some test");
        withTestDataFrom("testdata/test.csv").run(steps).step1();

        verify(driver).get("Bill");
        verify(driver).get("Joe");
        verify(driver).get("Mary");
    }

    @Test
    public void should_be_able_to_use_test_data_with_semicolons() throws IOException {

        TestSteps steps =  factory.getSharedStepLibraryFor(TestSteps.class);

        setDefaultStepFactory(factory);

        StepEventBus.getParallelEventBus().testStarted("some test");
        withTestDataFrom("testdata/semicolon-test.csv").separatedBy(';').run(steps).step1();

        verify(driver).get("Bill");
        verify(driver).get("Joe");
        verify(driver).get("Mary");
    }

    @Test
    public void should_be_able_to_use_different_step_libraries_in_the_same_test() throws IOException {

        TestSteps steps =  factory.getSharedStepLibraryFor(TestSteps.class);
        DifferentTestSteps differentSteps = factory.getSharedStepLibraryFor(DifferentTestSteps.class);

        StepEventBus.getParallelEventBus().testStarted("some test");
        setDefaultStepFactory(factory);

        withTestDataFrom("testdata/test.csv").run(steps).name_and_dob();

        withTestDataFrom("testdata/test.csv").run(differentSteps).nameStep();

        verify(driver, times(2)).get("Bill");
        verify(driver, times(1)).get("10/10/1970");
    }


    @Test
    public void should_be_able_to_use_a_step_library_with_public_fields_and_no_setters() throws IOException {
        TestStepsWithNoSetters steps = factory.getSharedStepLibraryFor(TestStepsWithNoSetters.class);

        setDefaultStepFactory(factory);

        StepEventBus.getParallelEventBus().testStarted("some test");
        withTestDataFrom("testdata/test.csv").run(steps).nameStep();

        verify(driver).get("Bill");
        verify(driver).get("Joe");
        verify(driver).get("Mary");
    }

    @Test
    public void should_work_with_private_fields() throws IOException {
        TestStepsWithNoSettersAndInaccessibleFields steps
                = factory.getSharedStepLibraryFor(TestStepsWithNoSettersAndInaccessibleFields.class);

        setDefaultStepFactory(factory);

        StepEventBus.getParallelEventBus().testStarted("some test");
        withTestDataFrom("testdata/test.csv").run(steps).nameStep();
    }
    
    @Test
    public void should_instantiate_any_uninitialized_page_objects_in_a_step_class_when_using_data_driven_approach()
    	throws IOException {

		TestSteps steps = factory.getSharedStepLibraryFor(TestSteps.class);
        StepEventBus.getParallelEventBus().testStarted("some test");
		withTestDataFrom("testdata/test.csv").usingFactory(factory).run(steps).verifyPage();
		verify(driver, times(3)).get("TestPageObject");
    }

}
