package net.serenitybdd.cucumber.smoketests;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import net.serenitybdd.core.Serenity;
import net.thucydides.core.annotations.Step;
import net.thucydides.core.annotations.Steps;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class SmokeTestStepDefinitions {

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
    }

    @Steps
    private SomeStepLibrary someStepLibrary;

    @Given("^I want to use a step library$")
    public void iWantToUseAStepLibrary() {}

    @When("^I add a step library field annotated with @Steps$")
    public void iAddAStepLibraryFieldAnnotatedWithSteps() {}

    @Then("^Serenity should instantiate the field$")
    public void serenityShouldInstantiateTheField() throws Throwable {
        assertThat(someStepLibrary, notNullValue());
    }

    /////
    @Steps
    private SomeStepLibrary someOtherStepLibrary;

    @Given("^I want to use several step library fields of the same type$")
    public void iWantToUseSeveralStepLibraryFieldsOfTheSameType() {
    }

    @When("^I add a step library fields to each of them$")
    public void iAddAStepLibraryFieldsToEachOfThem(){
    }

    @Then("^Serenity should instantiate a different library for each field$")
    public void serenityShouldInstantiateADifferentLibraryForEachField() {
        assertThat(someOtherStepLibrary, is(not(sameInstance(someStepLibrary))));
    }

    //////
    @Steps
    private SomeStepLibrary myStepLibrary;

    @Given("^I have a Serenity step library$")
    public void iHaveASerenityStepLibrary() throws Throwable {
    }

    @When("^I do something with the library$")
    public void iDoSomethingWithTheLibrary() throws Throwable {
        myStepLibrary.doSomething();
    }

    @Then("^the state of the library should be updated$")
    public void theStateOfTheLibraryShouldBeUpdated() throws Throwable {
        assertThat(myStepLibrary.stepRunCount, is(1));
    }

    ////

    @When("^I start a new scenario$")
    public void iStartANewScenario() throws Throwable {
    }

    @Then("^the step library should be reinitialised$")
    public void theStepLibraryShouldBeReinitialised() throws Throwable {
        assertThat(myStepLibrary.stepRunCount, is(0));
    }

    ///
    @Steps(shared = true)
    private SomeStepLibrary aSharedStepLibrary;

    @Steps(shared = true)
    private SomeStepLibrary anotherSharedStepLibrary;

    @Given("^I have two Serenity step libraries$")
    public void iHaveTwoSerenityStepLibraries() throws Throwable {
    }

    @When("^they are annotated with @Steps\\(shared=true\\)$")
    public void theyAreAnnotatedWithStepsSharedTrue() throws Throwable {
    }

    @Then("^both should refer to the same instance$")
    public void bothShouldReferToTheSameInstance() throws Throwable {
        assertThat(aSharedStepLibrary, is(sameInstance(anotherSharedStepLibrary)));
    }

    @When("^I store information the session state$")
    public void storeSessionState() {
        Serenity.setSessionVariable("color").to("red");

    }

    @Then("^the session state information should be available in subsequent steps$")
    public void retrieveSessionState() {
        assertThat(Serenity.sessionVariableCalled("color"), is("red"));

    }

    @Then("^the session state information from previous scenarios should be cleared$")
    public void clearedSessionState() {
        assertThat(Serenity.sessionVariableCalled("color"), isEmptyOrNullString());

    }
}
