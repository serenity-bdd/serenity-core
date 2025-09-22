package net.serenitybdd.cucumber.integration.steps;

import io.cucumber.java.ParameterType;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import net.serenitybdd.core.steps.Instrumented;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.Task;

public class AnonymousPerformableFunctionSteps {

    Actor john = Actor.named("John");

    @Given("I use the Screenplay Anonymous Performable")
    public void IUseAnonymousPerformable() {
    }

    @ParameterType("(pass|fail)")
    public String passOrFail(String passOrFail) {
        if ("pass".equals(passOrFail) || "fail".equals(passOrFail)) {
            return passOrFail;
        }
        throw new IllegalArgumentException("Invalid value for passOrFail: " + passOrFail);
    }

    @When("I make this anonymous performable task {passOrFail}")
    public void makeThisAnonymousPerformablePassOrFail(String passOrFail) {
        john.attemptsTo(Task.where("{0} makes this " + passOrFail, actor -> {
            if ("fail".equals(passOrFail)) {
                throw new IllegalArgumentException("This step failed intentionally.");
            }
        }));
    }

    @When("I make this dedicated instrumented task {passOrFail}")
    public void makeThisDedicatedInstrumentedTaskPassOrFail(String passOrFail) {
        john.attemptsTo(PassOrFailTask.withOutcome(passOrFail));
    }

    @When("I make this dedicated non-instrumented task {passOrFail}")
    public void makeThisDedicatedNonInstrumentedTaskPassOrFail(String passOrFail) {
        john.attemptsTo(new PassOrFailTask(passOrFail));
    }

    @Then("I see this step is not executed")
    public void iDoNotSeeThisStepBeingExecuted() {
        throw new RuntimeException("This step should not be executed if the previous step fails");
    }

    @Then("I see this step being executed")
    public void iSeeThisStepBeingExecuted() {
    }

    @ParameterType("(not executed|executed)")
    public String runOrNotRun(String runOrNotRun) {
        if ("not executed".equals(runOrNotRun) || "executed".equals(runOrNotRun)) {
            return runOrNotRun;
        }
        throw new IllegalArgumentException("Invalid value for runOrNotRun: " + runOrNotRun);
    }

    @Then("I see this step is {runOrNotRun} depending on the outcome of the previous step")
    public void iSeeThisStepIsExecutedIfTheOutcomeOfThePreviousStepIsPass(String toRun) {
        if ("not executed".equals(toRun)) {
            throw new RuntimeException("This step should not be executed if the previous step fails");
        }
    }

    public static class PassOrFailTask implements Task {
        private final String outcome;

        public static Performable withOutcome(String outcome) {
            return Instrumented.instanceOf(PassOrFailTask.class).withProperties(outcome);
        }

        public PassOrFailTask(String outcome) {
            this.outcome = outcome;
        }

        public PassOrFailTask() {
            this.outcome = "pass";
        }

        @Override
        public <T extends Actor> void performAs(T actor) {
            if ("fail".equals(outcome)) {
                throw new IllegalArgumentException("This step failed intentionally.");
            }
        }
    }
}