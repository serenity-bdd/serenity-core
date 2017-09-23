package net.serenitybdd.junit.runners.smoketests;

import net.serenitybdd.junit.runners.SerenityRunner;
import net.thucydides.core.annotations.Step;
import net.thucydides.core.annotations.Steps;
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
 * Smoke tests to demonstrate how step libraries work. Intended to be run as a whole test, not individually (order is important).
 */
public class WhenInstantiatingStepLibraries {

    public static class SomeStepLibrary {

        public int stepsRun = 0;

        @Step
        public void doSomething() {
            stepsRun++;
        }

        @Step
        public void doSomethingElse() {
            stepsRun++;
        }

        @Step
        public void doSomeOtherThing() {
            stepsRun++;
        }

    }

    @Steps
    SomeStepLibrary someStepLibrary;

    @Steps
    SomeStepLibrary anotherStepLibrary;

//    @Steps(uniqueInstance = true)
//    SomeStepLibrary aUniqueStepLibrary;
//
//    @Steps(uniqueInstance = true)
//    SomeStepLibrary anotherUniqueStepLibrary;

    @Test
    public void example_1_serenity_injects_step_library_fields_that_are_annotated_with_the_Steps_annotation() {
        assertThat(someStepLibrary, notNullValue());
        assertThat(anotherStepLibrary, notNullValue());
    }

    @Test
    public void example_2_you_can_maintain_state_in_the_step_library_within_a_test_() {

        someStepLibrary.doSomething();
        someStepLibrary.doSomethingElse();

        assertThat(someStepLibrary.stepsRun, equalTo(2));
    }

    @Test
    public void example_3_tests_reset_the_step_libraries_before_each_test() {

        assertThat(someStepLibrary.stepsRun, equalTo(0));

        someStepLibrary.doSomeOtherThing();

        assertThat(someStepLibrary.stepsRun, equalTo(1));
    }

    @Test
    public void example_4_you_can_have_several_step_libararies_of_the_same_type_in_a_test() {

        someStepLibrary.stepsRun = 0;
        anotherStepLibrary.stepsRun = 0;

        someStepLibrary.doSomething();

        anotherStepLibrary.doSomething();
        anotherStepLibrary.doSomethingElse();

        assertThat(someStepLibrary.stepsRun, equalTo(1));
        assertThat(anotherStepLibrary.stepsRun, equalTo(2));
    }

//    @Test
//    public void example_5_unique_step_libraries_maintain_their_own_state() {
//
//        aUniqueStepLibrary.doSomething();
//        aUniqueStepLibrary.doSomeOtherThing();
//
//        anotherUniqueStepLibrary.doSomethingElse();
//
//        assertThat(aUniqueStepLibrary.stepsRun, equalTo(2));
//        assertThat(anotherUniqueStepLibrary.stepsRun, equalTo(1));
//    }

}
