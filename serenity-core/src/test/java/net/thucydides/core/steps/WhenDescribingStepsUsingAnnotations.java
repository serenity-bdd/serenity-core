package net.thucydides.core.steps;

import net.serenitybdd.core.collect.NewMap;
import net.thucydides.core.annotations.*;
import net.thucydides.core.pages.Pages;
import org.jbehave.core.annotations.Given;
import org.junit.Ignore;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


public class WhenDescribingStepsUsingAnnotations {

    @UserStoryCode("U2")
    class SampleTestSteps extends ScenarioSteps {

        public SampleTestSteps(final Pages pages) {
            super(pages);
        }

        public String color;
        public Integer age;
        public String emptyField;

        @Step
        public void a_step() {}

        @Step
        @Pending
        public void a_pending_step() {}

        @Step
        @Ignore
        public void an_ignored_step() {}

        @Title("A step with an annotation")
        @Step
        public void an_annotated_step_with_a_title() {}

        @Step("A step with an annotation")
        public void an_annotated_step() {}

        @StepGroup
        public void a_step_group() {}

        @StepGroup("A step group with an annotation")
        public void an_annotated_step_group() {}

        public void a_step_with_parameters(String name) {}

        @Step("a step with a parameter called '{0}'")
        public void a_customized_step_with_parameters(String name) {}

        @Step("a step with first parameter called '{0}' and second parameter called '{1}'")
        public void a_customized_step_with_parameters(String first, String second) {}

        @Step("a step with a parameter called '{0}' and a field called #color")
        public void a_customized_step_with_parameters_and_fields(String name) {}


        @Step("a step with a parameter called '{0}' and a field called #emptyField")
        public void a_customized_step_with_parameters_and_empty_field_value(String name) {}

        @Step("a step about a person called {0}, aged {1}")
        public void a_customized_step_with_two_parameters(String name, int age) {}

        @Step("a step with complex parameter '{0.email}'")
        public void a_customized_step_with_complex_parameter_property(User user){}

        @Step("a step with complex parameter '{0.getEmail()}'")
        public void a_customized_step_with_complex_parameter_method(User user){}

        @Step("a step with complex parameter '{0.id}'")
        public void a_customized_step_with_complex_parameter_invalid_property(User user){}

        @Step("a step with complex parameter '{0.getId()}'")
        public void a_customized_step_with_complex_parameter_invalid_method(User user){}

        @Step("a step with complex parameter '{1.getId()}' and '{0.email}'")
        public void a_customized_step_with_several_complex_parameters(User user, DbUser dbUser){}

        @Step("a step with several usage of param: email - '{0.email}', nickname - '{0.nickname}', id - '{0.id}'")
        public void a_customized_step_with_several_refs_to_same_complex_parameter(DbUser dbUser){}

        @Step("a step with ref to nested object: email - '{0.email}', city - '{0.address.city}'")
        public void a_customized_step_with_references_to_nested_object_as_parameter(User user){}


        @TestsRequirement("REQ-1")
        @Step
        public void a_step_testing_a_requirement() {}

        @TestsRequirements({"REQ-1","REQ-2"})
        @Step
        public void a_step_testing_several_requirements() {}

        @Given("A step with a given annotation")
        public void a_given_annotated_step() {}
    }

    static class User {
        private String email;
        private String nickname;
        private Address address;

        public User(String email) {
            this.email = email;
        }

        public User(String email, String nickname) {
            this(email);
            this.nickname = nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public void setAddress(Address address) {
            this.address = address;
        }

        public String getEmail() {
            return email;
        }

        public String toString() {
            return "User: email - " + email + ", nickname - " + nickname;
        }
    }

    static class DbUser extends User {
        private int id;

        public DbUser(int id, String email) {
            super(email);
            this.id = id;
        }

        public int getId() {
            return id;
        }
    }

    static class Address {
        private String city;
        private String address1;

        public Address(String city, String address1) {
            this.city = city;
            this.address1 = address1;
        }
    }

    @Test
    public void the_default_step_name_should_be_a_human_readable_version_of_the_method_name() {
        ExecutedStepDescription description = new ExecutedStepDescription(SampleTestSteps.class, "a_step");

        AnnotatedStepDescription annotatedStepDescription = AnnotatedStepDescription.from(description);

        assertThat(annotatedStepDescription.getName(), is("A step"));
    }

    @Test
    public void the_() {
        ExecutedStepDescription description = new ExecutedStepDescription(SampleTestSteps.class, "a_step");

        AnnotatedStepDescription annotatedStepDescription = AnnotatedStepDescription.from(description);

        assertThat(annotatedStepDescription.getName(), is("A step"));
    }

    @Test
    public void if_a_name_is_provided_it_will_be_added_to_the_start_of_the_step() {
        ExecutedStepDescription description = new ExecutedStepDescription(SampleTestSteps.class, "a_step");

        AnnotatedStepDescription annotatedStepDescription = AnnotatedStepDescription.from(description);

        assertThat(annotatedStepDescription.getName(), is("A step"));
    }

    @Test
    public void a_step_can_be_annotated_to_provide_a_more_readable_name() {
        ExecutedStepDescription description = new ExecutedStepDescription(SampleTestSteps.class, "an_annotated_step");

        AnnotatedStepDescription annotatedStepDescription = AnnotatedStepDescription.from(description);

        assertThat(annotatedStepDescription.getName(), is("A step with an annotation"));
    }

    @Test
    public void a_title_annotation_can_also_be_used_to_provide_a_more_readable_name() {
        ExecutedStepDescription description = new ExecutedStepDescription(SampleTestSteps.class, "an_annotated_step_with_a_title");

        AnnotatedStepDescription annotatedStepDescription = AnnotatedStepDescription.from(description);

        assertThat(annotatedStepDescription.getName(), is("A step with an annotation"));
    }

    @Test
    public void a_title_annotation_can_be_defined_in_a_given_annotation() {
        ExecutedStepDescription description = new ExecutedStepDescription(SampleTestSteps.class, "a_given_annotated_step");

        AnnotatedStepDescription annotatedStepDescription = AnnotatedStepDescription.from(description);

        assertThat(annotatedStepDescription.getName(), is("Given a step with a given annotation"));
    }

    @Test
    public void a_step_group_name_should_be_a_human_readable_version_of_the_method_name() {
        ExecutedStepDescription description = new ExecutedStepDescription(SampleTestSteps.class, "a_step_group");

        AnnotatedStepDescription annotatedStepDescription = AnnotatedStepDescription.from(description);

        assertThat(annotatedStepDescription.getName(), is("A step group"));
    }

    @Test
    public void a_step_group_can_be_annotated_to_provide_a_more_readable_name() {
        ExecutedStepDescription description = new ExecutedStepDescription(SampleTestSteps.class, "an_annotated_step_group");

        AnnotatedStepDescription annotatedStepDescription = AnnotatedStepDescription.from(description);

        assertThat(annotatedStepDescription.getName(), is("A step group with an annotation"));
    }

    @Test
    public void a_step_can_be_annotated_to_provide_a_more_readable_name_including_a_parameter() {
        ExecutedStepDescription description = ExecutedStepDescription.of(SampleTestSteps.class,
                "a_customized_step_with_parameters: Joe",
                new Object[]{"Joe"});

        AnnotatedStepDescription annotatedStepDescription = AnnotatedStepDescription.from(description);

        assertThat(annotatedStepDescription.getName(), is("a step with a parameter called 'Joe'"));
    }

    @Test
    public void a_step_can_be_annotated_to_provide_a_more_readable_name_including_multiple_parameters() {
        ExecutedStepDescription description = ExecutedStepDescription.of(SampleTestSteps.class,
                "a_customized_step_with_parameters: Joe, Doe",
                new Object[]{"Joe", "Doe"});

        AnnotatedStepDescription annotatedStepDescription = AnnotatedStepDescription.from(description);

        assertThat(annotatedStepDescription.getName(), is("a step with first parameter called 'Joe' and second parameter called 'Doe'"));
    }

    @Test
    public void a_step_can_be_annotated_to_provide_a_more_readable_name_including_a_parameter_and_a_field_variable() {

        ExecutedStepDescription description = ExecutedStepDescription.of(SampleTestSteps.class,
                "a_customized_step_with_parameters_and_fields",
                new Object[]{"Joe"})
                .withDisplayedFields(NewMap.of("color", (Object) "red"));

        AnnotatedStepDescription annotatedStepDescription = AnnotatedStepDescription.from(description);

        assertThat(annotatedStepDescription.getName(), is("a step with a parameter called 'Joe' and a field called red"));
    }

    @Ignore("Don't throw exceptions when a variable is not found, as this can sometimes happen in normal execution.")
    @Test(expected = AssertionError.class)
    public void a_step_can_be_annotated_to_provide_a_more_readable_name_including_a_parameter_and_an_empty_field_variable() {

        ExecutedStepDescription description = ExecutedStepDescription.of(SampleTestSteps.class,
                "a_customized_step_with_parameters_and_empty_field_value",
                new Object[] {"Joe"})
                .withDisplayedFields(NewMap.of("color",(Object)"red", "emptyField", Fields.FieldValue.UNDEFINED));

        AnnotatedStepDescription.from(description);
    }

    @Test
    public void a_step_can_be_annotated_to_provide_a_more_readable_name_including_several_parameters() {
        ExecutedStepDescription description = ExecutedStepDescription.of(SampleTestSteps.class,
                "a_customized_step_with_two_parameters",
                new Object[]{"Joe", "20"});

        AnnotatedStepDescription annotatedStepDescription = AnnotatedStepDescription.from(description);

        assertThat(annotatedStepDescription.getName(), is("a step about a person called Joe, aged 20"));
    }

    @Test
    public void a_step_can_be_annotated_to_provide_a_more_readable_name_including_several_parameters_with_comma() {
        ExecutedStepDescription description = ExecutedStepDescription.of(SampleTestSteps.class,
                "a_customized_step_with_two_parameters",
                new Object[] {"Smith, Joe", "20"});

        AnnotatedStepDescription annotatedStepDescription = AnnotatedStepDescription.from(description);

        assertThat(annotatedStepDescription.getName(), is("a step about a person called Smith, Joe, aged 20"));
    }

    @Test
    public void should_identify_pending_steps() {
        ExecutedStepDescription description = new ExecutedStepDescription(SampleTestSteps.class, "a_pending_step");

        AnnotatedStepDescription annotatedStepDescription = AnnotatedStepDescription.from(description);

        assertThat(annotatedStepDescription.isPending(), is(true));
    }

    @Test
    public void should_identify_non_pending_steps() {
        ExecutedStepDescription description = new ExecutedStepDescription(SampleTestSteps.class, "a_step");

        AnnotatedStepDescription annotatedStepDescription = AnnotatedStepDescription.from(description);

        assertThat(annotatedStepDescription.isPending(), is(false));
    }

    @Test
    public void should_identify_ignored_steps() {
        ExecutedStepDescription description = new ExecutedStepDescription(SampleTestSteps.class, "an_ignored_step");

        AnnotatedStepDescription annotatedStepDescription = AnnotatedStepDescription.from(description);

        assertThat(annotatedStepDescription.isIgnored(), is(true));
    }

    @Test
    public void should_identify_unignored_steps() {
        ExecutedStepDescription description = new ExecutedStepDescription(SampleTestSteps.class, "a_step");

        AnnotatedStepDescription annotatedStepDescription = AnnotatedStepDescription.from(description);

        assertThat(annotatedStepDescription.isIgnored(), is(false));
    }

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

        annotatedStepDescription.getTestMethod();

    }

    @Test
    public void the_description_should_return_the_corresponding_step_method() {
        ExecutedStepDescription description = new ExecutedStepDescription(SampleTestSteps.class, "a_step");

        AnnotatedStepDescription annotatedStepDescription = AnnotatedStepDescription.from(description);

        assertThat(annotatedStepDescription.getTestMethod().getName(), is("a_step"));
    }

    @Test
    public void the_description_should_return_the_corresponding_step_method_with_parameters() {
        ExecutedStepDescription description = new ExecutedStepDescription(SampleTestSteps.class, "a_step_with_parameters: Joe");

        AnnotatedStepDescription annotatedStepDescription = AnnotatedStepDescription.from(description);

        assertThat(annotatedStepDescription.getTestMethod().getName(), is("a_step_with_parameters"));
    }

    @Test
    public void should_find_the_specified_title_if_no_class_is_specified() {
        ExecutedStepDescription description = ExecutedStepDescription.withTitle("a step with no class");

        AnnotatedStepDescription annotatedStepDescription = AnnotatedStepDescription.from(description);

        assertThat(annotatedStepDescription.getName(), is("a step with no class"));
    }

    @Test
    public void a_step_can_be_annotated_to_provide_a_more_readable_name_including_property_of_object_parameter() {
        ExecutedStepDescription description = ExecutedStepDescription.of(SampleTestSteps.class,
                "a_customized_step_with_complex_parameter_property",
                new Object[] {new User("user@example.com")});

        AnnotatedStepDescription annotatedStepDescription = AnnotatedStepDescription.from(description);

        assertThat(annotatedStepDescription.getName(), is("a step with complex parameter 'user@example.com'"));
    }

    @Test
    public void a_step_can_be_annotated_to_provide_a_more_readable_name_including_method_of_object_parameter() {
        ExecutedStepDescription description = ExecutedStepDescription.of(SampleTestSteps.class,
                "a_customized_step_with_complex_parameter_method",
                new Object[] {new User("user@example.com")});

        AnnotatedStepDescription annotatedStepDescription = AnnotatedStepDescription.from(description);

        assertThat(annotatedStepDescription.getName(), is("a step with complex parameter 'user@example.com'"));
    }

    @Test
    public void should_use_null_if_property_of_object_parameter_is_null() {
        ExecutedStepDescription description = ExecutedStepDescription.of(SampleTestSteps.class,
                "a_customized_step_with_complex_parameter_property",
                new Object[] {new User(null)});

        AnnotatedStepDescription annotatedStepDescription = AnnotatedStepDescription.from(description);

        assertThat(annotatedStepDescription.getName(), is("a step with complex parameter 'null'"));
    }

    @Test
    public void should_fall_back_to_default_toString_if_property_of_object_parameter_doesnt_exist() {
        ExecutedStepDescription description = ExecutedStepDescription.of(SampleTestSteps.class,
                "a_customized_step_with_complex_parameter_invalid_property",
                new Object[] {new User("user@example.com")});

        AnnotatedStepDescription annotatedStepDescription = AnnotatedStepDescription.from(description);

        assertThat(annotatedStepDescription.getName(),
                is("a step with complex parameter 'User: email - user@example.com, nickname - null'"));
    }

    @Test
    public void should_fall_back_to_default_toString_if_method_of_object_parameter_doesnt_exist() {
        ExecutedStepDescription description = ExecutedStepDescription.of(SampleTestSteps.class,
                "a_customized_step_with_complex_parameter_invalid_method",
                new Object[] {new User("user@example.com")});

        AnnotatedStepDescription annotatedStepDescription = AnnotatedStepDescription.from(description);

        assertThat(annotatedStepDescription.getName(),
                is("a step with complex parameter 'User: email - user@example.com, nickname - null'"));
    }

    @Test
    public void a_step_can_be_annotated_to_provide_a_more_readable_name_including_several_object_parameters() {
        ExecutedStepDescription description = ExecutedStepDescription.of(SampleTestSteps.class,
                "a_customized_step_with_several_complex_parameters",
                new Object[] {new User("user@example.com"), new DbUser(1, "id@example.com")});

        AnnotatedStepDescription annotatedStepDescription = AnnotatedStepDescription.from(description);

        assertThat(annotatedStepDescription.getName(), is("a step with complex parameter '1' and 'user@example.com'"));
    }

    @Test
    public void a_step_can_be_annotated_to_provide_a_more_readable_name_including_several_references_to_same_object_parameter() {
        DbUser dbUser = new DbUser(1, "user@example.com");
        dbUser.setNickname("Sunny");
        ExecutedStepDescription description = ExecutedStepDescription.of(SampleTestSteps.class,
                "a_customized_step_with_several_refs_to_same_complex_parameter",
                new Object[] {dbUser});

        AnnotatedStepDescription annotatedStepDescription = AnnotatedStepDescription.from(description);

        assertThat(annotatedStepDescription.getName(),
                is("a step with several usage of param: email - 'user@example.com', nickname - 'Sunny', id - '1'"));
    }

    @Test
    public void a_step_can_be_annotated_to_provide_a_more_readable_name_including_references_to_nested_object() {
        User user = new User("user@example.com");
        Address address = new Address("New York", "5th Avenue");
        user.setAddress(address);
        ExecutedStepDescription description = ExecutedStepDescription.of(SampleTestSteps.class,
                "a_customized_step_with_references_to_nested_object_as_parameter",
                new Object[] {user});

        AnnotatedStepDescription annotatedStepDescription = AnnotatedStepDescription.from(description);

        assertThat(annotatedStepDescription.getName(),
                is("a step with ref to nested object: email - 'user@example.com', city - 'New York'"));
    }
}
