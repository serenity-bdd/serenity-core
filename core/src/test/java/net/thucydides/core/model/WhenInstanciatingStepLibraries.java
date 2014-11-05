package net.thucydides.core.model;

import net.thucydides.core.Thucydides;
import net.thucydides.core.annotations.Step;
import net.thucydides.core.annotations.Steps;
import net.thucydides.core.model.samples.MyInheritedStepLibrary;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.steps.ScenarioSteps;
import net.thucydides.core.steps.StepEventBus;
import net.thucydides.core.steps.StepFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openqa.selenium.WebDriver;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

public class WhenInstanciatingStepLibraries {

    @Mock
    WebDriver driver;

    StepFactory stepFactory;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
        Pages pages = new Pages(driver);
        stepFactory = new StepFactory(pages);

    }

    public static class AStepLibrary extends ScenarioSteps {
        public AStepLibrary(Pages pages) {
            super(pages);
        }

        @Step
        public void step1() {}

        @Step
        public void step2() {}
    }


    public static class ASimpleStepLibrary  {
        private Pages pages;

        public ASimpleStepLibrary(Pages pages) {
            this.pages = pages;
        }

        @Step
        public void step1() {}

        @Step
        public void step2() {}
    }


    public static class ASimpleStepLibraryWithAPagesField  {
        private Pages pages;

        public ASimpleStepLibraryWithAPagesField() {}

        public Pages getPages() {
            return pages;
        }

        @Step
        public void step1() {}

        @Step
        public void step2() {}
    }

    public static class ANonWebStepLibrary  {

        public ANonWebStepLibrary() {}

        @Step
        public void step1() {}

        @Step
        public void step2() {}
    }

    public static class ARecursiveNonWebStepLibrary  {

        ARecursiveNonWebStepLibrary() {}

        @Steps
        ARecursiveNonWebStepLibrary aRecursiveNonWebStepLibrary;

        @Step
        public void step1() {
            aRecursiveNonWebStepLibrary.step2();
        }

        @Step
        public void step2() {}
    }

    public static class ANestedStepLibrary extends ScenarioSteps {
        ANestedStepLibrary(Pages pages) {
            super(pages);
        }

        @Steps
        public AStepLibrary aStepLibrary;

        @Step
        public void step1() {}

        @Step
        public void step2() {}
    }


    public static class ANonWebNestedStepLibrary  {
        ANonWebNestedStepLibrary() {
        }

        @Steps
        public ANonWebStepLibrary aStepLibrary;

        @Step
        public void step1() {}

        @Step
        public void step2() {}
    }

    public static class ARecursiveNestedStepLibrary extends ScenarioSteps {
        ARecursiveNestedStepLibrary(Pages pages) {
            super(pages);
        }

        @Steps
        public AStepLibrary aStepLibrary;

        @Steps
        public ARecursiveNestedStepLibrary aRecursiveNestedStepLibrary;

        @Step
        public void step1() {}

        @Step
        public void step2() {}
    }

    public static class ACyclicNestedStepLibrary extends ScenarioSteps {
        ACyclicNestedStepLibrary(Pages pages) {
            super(pages);
        }

        @Steps
        public AStepLibrary aStepLibrary;

        @Steps
        public ARecursiveNestedStepLibrary aRecursiveNestedStepLibrary;

        @Steps
        public ACyclicNestedStepLibrary aCyclicNestedStepLibrary;

        @Step
        public void step1() {}

        @Step
        public void step2() {}
    }

    @Before
    public void startTest() {
        Thucydides.initialize(this);
        StepEventBus.getEventBus().testSuiteStarted(Story.called("sample story"));
        StepEventBus.getEventBus().testStarted("sample test");
    }

    @After
    public void finishTest() {
        StepEventBus.getEventBus().testFinished();
        StepEventBus.getEventBus().testSuiteFinished();
    }

    @Test
    public void should_instanciate_step_library_instance() {
        AStepLibrary steps = stepFactory.getStepLibraryFor(AStepLibrary.class);

        assertThat(steps, is(notNullValue()));
    }

    @Test
    public void should_instanciate_step_library_instance_for_parentless_step_classes() {
        ASimpleStepLibrary steps = stepFactory.getStepLibraryFor(ASimpleStepLibrary.class);

        assertThat(steps, is(notNullValue()));
    }

    @Test
    public void should_instanciate_step_library_instances_with_a_pages_field() {
        ASimpleStepLibraryWithAPagesField steps = stepFactory.getStepLibraryFor(ASimpleStepLibraryWithAPagesField.class);

        assertThat(steps.getPages(), is(notNullValue()));
    }


    @Test
    public void should_instanciate_nested_step_library_instances() {
        ANestedStepLibrary steps = stepFactory.getStepLibraryFor(ANestedStepLibrary.class);

        assertThat(steps, is(notNullValue()));
        assertThat(steps.aStepLibrary, is(notNullValue()));
    }

    @Test
    public void should_instanciate_non_web_nested_step_library_instances() {
        ANonWebNestedStepLibrary steps = stepFactory.getStepLibraryFor(ANonWebNestedStepLibrary.class);

        assertThat(steps, is(notNullValue()));
        assertThat(steps.aStepLibrary, is(notNullValue()));
    }


    @Test
    public void should_correctly_instanciate_recursive_nested_step_library_instances() {
        ARecursiveNestedStepLibrary steps = stepFactory.getStepLibraryFor(ARecursiveNestedStepLibrary.class);

        assertThat(steps, notNullValue());
        assertThat(steps.aStepLibrary, is(notNullValue()));
        assertThat(steps.aRecursiveNestedStepLibrary, is(notNullValue()));
    }

    @Test
    public void should_correctly_instanciate_recursive_nested_non_web_step_library_instances() {
        ARecursiveNonWebStepLibrary steps = stepFactory.getStepLibraryFor(ARecursiveNonWebStepLibrary.class);

        assertThat(steps, notNullValue());
        assertThat(steps.aRecursiveNonWebStepLibrary, is(notNullValue()));
    }


    @Test
    public void should_correctly_instanciate_cyclic_nested_step_library_instances() {
        ACyclicNestedStepLibrary steps = stepFactory.getStepLibraryFor(ACyclicNestedStepLibrary.class);

        assertThat(steps, notNullValue());
        assertThat(steps.aStepLibrary, is(notNullValue()));
        assertThat(steps.aCyclicNestedStepLibrary, is(notNullValue()));
        assertThat(steps.aRecursiveNestedStepLibrary, is(notNullValue()));
    }

    @Test
    public void should_support_calling_protected_steps_in_parent_classes() {
        MyInheritedStepLibrary myStepLibrary = stepFactory.getStepLibraryFor(MyInheritedStepLibrary.class);
        assertThat(myStepLibrary.aStepWithAProtectedMethod(), is(true));
        assertThat(StepEventBus.getEventBus().aStepInTheCurrentTestHasFailed(), is(false));
    }

    @Test
    public void should_support_calling_protected_methods_in_parent_classes() {
        MyInheritedStepLibrary myStepLibrary = stepFactory.getStepLibraryFor(MyInheritedStepLibrary.class);
        assertThat(myStepLibrary.anotherStep(), is(true));
        assertThat(StepEventBus.getEventBus().aStepInTheCurrentTestHasFailed(), is(false));
    }
}
