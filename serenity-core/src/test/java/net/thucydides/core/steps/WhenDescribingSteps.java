package net.thucydides.core.steps;

import net.thucydides.core.annotations.UserStoryCode;
import net.thucydides.core.pages.Pages;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


public class WhenDescribingSteps {

    @UserStoryCode("U2")
    class SampleTestSteps extends ScenarioSteps {

        public SampleTestSteps(final Pages pages) {
            super(pages);
        }

        public void a_step() {}

        public void a_step_group() {}

        public void a_step_with_parameters(String name) {}

    }

    @Test
    public void the_name_of_a_step_should_be_the_corresponding_method_name() {
        ExecutedStepDescription description = new ExecutedStepDescription(SampleTestSteps.class, "a_step");

        assertThat(description.getName(), is("a_step"));
    }

    @Test (expected=IllegalArgumentException.class)
    public void an_exception_should_be_thrown_if_the_name_is_incorrect() {
        ExecutedStepDescription description = new ExecutedStepDescription(SampleTestSteps.class, "not_a_step");
        description.getStepMethod();
    }

    @Test
    public void the_description_should_also_return_the_step_class() {
        ExecutedStepDescription description = ExecutedStepDescription.of(SampleTestSteps.class, "a_step");

        assertThat(description.getStepClass().getSimpleName(), is("SampleTestSteps"));
    }

    @Test
    public void the_title_of_a_step_should_be_a_human_readable_version_of_the_step_name() {
        ExecutedStepDescription description = new ExecutedStepDescription(SampleTestSteps.class, "a_step");

        assertThat(description.getTitle(), is("A step"));
    }

    @Test
    public void by_default_a_step_is_not_considered_a_group() {
        ExecutedStepDescription description = new ExecutedStepDescription(SampleTestSteps.class, "a_step");

        assertThat(description.isAGroup(), is(false));
    }

    class ATestCase {}

    @Test
    public void a_step_can_be_defined_as_a_group() {
        ExecutedStepDescription description = new ExecutedStepDescription(SampleTestSteps.class, "a_step_group", true);

        assertThat(description.isAGroup(), is(true));
    }

    @Test
    public void a_step_can_be_defined_as_a_group_after_its_creation() {
        ExecutedStepDescription description = new ExecutedStepDescription(SampleTestSteps.class, "a_step_group");
        description.setAGroup(true);
        assertThat(description.isAGroup(), is(true));
    }

    @Test
    public void the_description_should_return_the_step_method() {
        ExecutedStepDescription description = new ExecutedStepDescription(SampleTestSteps.class, "a_step");

        assertThat(description.getStepMethod().getName(), is("a_step"));
    }

    @Test
    public void the_description_should_return_the_step_method_if_the_name_has_parameters() {
        ExecutedStepDescription description = new ExecutedStepDescription(SampleTestSteps.class, "a_step_with_parameters: Joe");

        assertThat(description.getStepMethod().getName(), is("a_step_with_parameters"));
    }

    @Test
    public void a_description_can_be_created_without_a_class() {
        ExecutedStepDescription description = ExecutedStepDescription.withTitle("a_step");

        assertThat(description.getTitle(), is("A step"));
    }

    @Test
    public void a_description_without_a_class_should_have_no_method() {
        ExecutedStepDescription description = ExecutedStepDescription.withTitle("a_step");

        assertThat(description.getStepMethod(), is(nullValue()));
    }

    @Test
    public void a_description_can_be_cloned() {
        ExecutedStepDescription description = new ExecutedStepDescription(SampleTestSteps.class, "a_step_group", true);
        ExecutedStepDescription clone = description.clone();

        assertThat(clone.getName(), is(description.getName()));
        assertThat(clone.getStepMethod(), is(description.getStepMethod()));
        assertThat(clone.isAGroup(), is(description.isAGroup()));
        assertThat(clone.getArguments(), is(description.getArguments()));
    }

    @Test
    public void a_description_can_be_created_without_arguments_list() {
        ExecutedStepDescription description = new ExecutedStepDescription(SampleTestSteps.class, "a_step");

        assertThat(description.getStepMethod().getName(), is("a_step"));
        assertThat(description.getArguments(), notNullValue());
        assertThat(description.getArguments().size(), is(0));
    }

    @Test
    public void a_description_can_be_created_with_arguments_list() {
        ExecutedStepDescription description = ExecutedStepDescription.of(SampleTestSteps.class, "a_step_with_parameters", new Object[] {"Joe"});

        assertThat(description.getStepMethod().getName(), is("a_step_with_parameters"));
        assertThat(description.getArguments(), notNullValue());
        assertThat(description.getArguments().size(), is(1));
        assertThat(description.getArguments().get(0), is("Joe"));
    }
}
