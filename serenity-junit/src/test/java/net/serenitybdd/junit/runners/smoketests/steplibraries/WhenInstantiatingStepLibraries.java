package net.serenitybdd.junit.runners.smoketests.steplibraries;

import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.annotations.Step;
import net.serenitybdd.annotations.Steps;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

@RunWith(SerenityRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
/**
 * Smoke tests to demonstrate how persistant step libraries work.
 */
public class WhenInstantiatingStepLibraries {

    public static class SomeStepLibrary {

        public int stepRunCount = 0;

        @Step
        public void doSomething() {
            stepRunCount++;
        }

        @Step
        public void doSomethingElse() {
            stepRunCount++;
        }

        @Step
        public void doSomeOtherThing() {
            stepRunCount++;
        }

        @Steps(shared = true)
        public ASharedStepLibrary aNestedSharedStepLibrary;

        @Steps
        public ASharedStepLibrary anUnsharedStepLibrary;

    }

    public static class ASharedStepLibrary {

        public int stepRunCount = 0;

        @Step
        public void doSomePersistantThing() {
            stepRunCount++;
        }

    }

    @Steps
    SomeStepLibrary someStepLibrary;

    @Steps
    SomeStepLibrary anotherStepLibrary;

    @Steps(shared = true)
    ASharedStepLibrary aSharedStepLibrary;

    @Steps(shared = true)
    ASharedStepLibrary anotherSharedStepLibrary;

    @Steps
    ASharedStepLibrary anUnsharedStepLibrary;

    @Test
    public void example_1_serenity_injects_step_library_fields_that_are_annotated_with_the_Steps_annotation() {
        assertThat(someStepLibrary, notNullValue());
        assertThat(anotherStepLibrary, notNullValue());
    }

    @Test
    public void example_2_you_can_maintain_state_in_the_step_library_within_a_test_() {

        someStepLibrary.doSomething();
        someStepLibrary.doSomethingElse();

        assertThat(someStepLibrary.stepRunCount, equalTo(2));
    }

    @Test
    public void example_3_tests_reset_the_step_libraries_before_each_test() {

        assertThat(someStepLibrary.stepRunCount, equalTo(0));

        someStepLibrary.doSomeOtherThing();

        assertThat(someStepLibrary.stepRunCount, equalTo(1));
    }

    @Test
    public void example_4_you_can_have_several_step_libararies_of_the_same_type_in_a_test() {

        someStepLibrary.stepRunCount = 0;
        anotherStepLibrary.stepRunCount = 0;

        someStepLibrary.doSomething();

        anotherStepLibrary.doSomething();
        anotherStepLibrary.doSomethingElse();

        assertThat(someStepLibrary.stepRunCount, equalTo(1));
        assertThat(anotherStepLibrary.stepRunCount, equalTo(2));
    }


    @Test
    public void example_5_shared_step_libraries_share_state() {
        assertThat(aSharedStepLibrary.stepRunCount, equalTo(0));
        assertThat(anotherSharedStepLibrary.stepRunCount, equalTo(0));

        aSharedStepLibrary.doSomePersistantThing();

        assertThat(aSharedStepLibrary.stepRunCount, equalTo(1));
        assertThat(anotherSharedStepLibrary.stepRunCount, equalTo(1));
    }

    @Test
    public void example_6_shared_step_libraries_can_be_used_inside_other_step_libraries() {
        assertThat(someStepLibrary.aNestedSharedStepLibrary.stepRunCount, equalTo(0));
        assertThat(aSharedStepLibrary.stepRunCount, equalTo(0));
        assertThat(aSharedStepLibrary.stepRunCount, equalTo(0));

        aSharedStepLibrary.doSomePersistantThing();

        assertThat(aSharedStepLibrary.stepRunCount, equalTo(1));
        assertThat(someStepLibrary.aNestedSharedStepLibrary.stepRunCount, equalTo(1));

    }

    @Test
    public void example_7_shared_step_libraries_dont_interfere_with_unshared_libraries() {
        assertThat(someStepLibrary.aNestedSharedStepLibrary.stepRunCount, equalTo(0));
        assertThat(aSharedStepLibrary.stepRunCount, equalTo(0));
        assertThat(anUnsharedStepLibrary.stepRunCount, equalTo(0));

        aSharedStepLibrary.doSomePersistantThing();

        assertThat(aSharedStepLibrary.stepRunCount, equalTo(1));
        assertThat(someStepLibrary.aNestedSharedStepLibrary.stepRunCount, equalTo(1));
        assertThat(anUnsharedStepLibrary.stepRunCount, equalTo(0));

    }

    @Test
    public void example_8_shared_step_libraries_do_not_interfere_with_independent_step_libraries() {
        assertThat(anUnsharedStepLibrary.stepRunCount, equalTo(0));
        assertThat(someStepLibrary.aNestedSharedStepLibrary.stepRunCount, equalTo(0));
        assertThat(aSharedStepLibrary.stepRunCount, equalTo(0));

        aSharedStepLibrary.doSomePersistantThing();

        assertThat(aSharedStepLibrary.stepRunCount, equalTo(1));
        assertThat(anotherSharedStepLibrary.stepRunCount, equalTo(1));
        assertThat(someStepLibrary.aNestedSharedStepLibrary.stepRunCount, equalTo(1));
        assertThat(anUnsharedStepLibrary.stepRunCount, equalTo(0));
    }



}
