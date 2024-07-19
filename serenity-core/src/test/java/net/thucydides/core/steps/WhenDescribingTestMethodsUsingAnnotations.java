package net.thucydides.core.steps;

import net.serenitybdd.annotations.Step;
import net.serenitybdd.annotations.TestsRequirement;
import net.serenitybdd.annotations.TestsRequirements;
import net.serenitybdd.annotations.Title;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


public class WhenDescribingTestMethodsUsingAnnotations {

    class MyTestingClass {
        public void should_do_this() {}

        @Title("A test with an annotation")
        public void an_annotated_test_with_a_title() {}

        @TestsRequirement("REQ-1")
        @Step
        public void a_test_testing_a_requirement() {}

        @TestsRequirements({"REQ-1","REQ-2"})
        @Step
        public void a_test_testing_several_requirements() {}
    }

    @Test
    public void the_default_test_name_should_be_a_human_readable_version_of_the_method_name() {

        TestDescription testDescription = new TestDescription(MyTestingClass.class, "should_do_this");

        assertThat(testDescription.getName(), is("Should do this"));
    }
    @Test
    public void a_test_can_be_annotated_to_provide_a_more_readable_name() {

        TestDescription testDescription = new TestDescription(MyTestingClass.class, "an_annotated_test_with_a_title");

        assertThat(testDescription.getName(), is("A test with an annotation"));
    }

    @Test
    public void should_let_the_user_indicate_what_requirement_is_being_tested_by_a_step() {
        TestDescription description = new TestDescription(MyTestingClass.class, "a_test_testing_a_requirement");

        assertThat(description.getAnnotatedRequirements(), hasItem("REQ-1"));
    }

    @Test
    public void should_let_the_user_indicate_multiple_requirements() {
        TestDescription description = new TestDescription(MyTestingClass.class, "a_test_testing_several_requirements");

        assertThat(description.getAnnotatedRequirements(), hasItems("REQ-1", "REQ-2"));
    }


    @Test(expected = TestMethodNotFoundException.class)
    public void a_test_description_with_an_invalid_test_name_should_throw_an_exception() {
        TestDescription description = new TestDescription(MyTestingClass.class, "this_test_does_not_exist");
        description.getAnnotatedRequirements();
    }

    /*


    @Test
    public void should_let_the_user_indicate_what_requirement_is_being_tested_by_a_step() {
        ExecutedStepDescription description = new ExecutedStepDescription(SampleTestSteps.class, "a_step_testing_a_requirement");

        AnnotatedStepDescription annotatedStepDescription = AnnotatedStepDescription.from(description);

        assertThat(annotatedStepDescription.getAnnotatedRequirements(), hasItem("REQ-1"));
    }

    @Test
    public void should_let_the_user_indicate_multiple_requirements() {
        ExecutedStepDescription description = new ExecutedStepDescription(SampleTestSteps.class, "a_step_testing_several_requirements");

        AnnotatedStepDescription annotatedStepDescription = AnnotatedStepDescription.from(description);

        assertThat(annotatedStepDescription.getAnnotatedRequirements(), hasItems("REQ-1", "REQ-2"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void should_throw_an_exception_if_no_matching_step_exists() {
        ExecutedStepDescription description = new ExecutedStepDescription(SampleTestSteps.class, "a_step_that_does_not_exist");

        AnnotatedStepDescription annotatedStepDescription = AnnotatedStepDescription.from(description);

        annotatedStepDescription.getName();

    }

    @Test(expected = IllegalArgumentException.class)
    public void should_throw_an_exception_if_you_ask_for_a_method_where_no_matching_step_exists() {
        ExecutedStepDescription description = new ExecutedStepDescription(SampleTestSteps.class, "a_step_that_does_not_exist");

        AnnotatedStepDescription annotatedStepDescription = AnnotatedStepDescription.from(description);

        annotatedStepDescription.getStepMethod();

    }

    @Test
    public void the_description_should_return_the_corresponding_step_method() {
        ExecutedStepDescription description = new ExecutedStepDescription(SampleTestSteps.class, "a_step");

        AnnotatedStepDescription annotatedStepDescription = AnnotatedStepDescription.from(description);

        assertThat(annotatedStepDescription.getStepMethod().getName(), is("a_step"));
    }

    @Test
    public void the_description_should_return_the_corresponding_step_method_with_parameters() {
        ExecutedStepDescription description = new ExecutedStepDescription(SampleTestSteps.class, "a_step_with_parameters: Joe");

        AnnotatedStepDescription annotatedStepDescription = AnnotatedStepDescription.from(description);

        assertThat(annotatedStepDescription.getStepMethod().getName(), is("a_step_with_parameters"));
    }

    @Test
    public void should_find_the_specified_title_if_no_class_is_specified() {
        ExecutedStepDescription description = ExecutedStepDescription.withTitle("a step with no class");

        AnnotatedStepDescription annotatedStepDescription = AnnotatedStepDescription.from(description);

        assertThat(annotatedStepDescription.getName(), is("a step with no class"));
    }
    */
}
