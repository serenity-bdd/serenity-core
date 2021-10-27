package net.serenitybdd.cucumber.integration.steps;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import samples.calculator.RpnCalculator;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class RpnCalculatorStepdefs {
    private RpnCalculator calc;

    @Given("^a calculator I just turned on$")
    public void a_calculator_I_just_turned_on() {
        calc = new RpnCalculator();
    }

    @When("^I add (\\d+) and (\\d+)$")
    public void adding(int arg1, int arg2) {
        calc.push(arg1);
        calc.push(arg2);
        calc.push("+");
    }

    @Given("^I press (.+)$")
    public void I_press(String what) {
        calc.push(what);
    }

    @Then("^the result is (\\d+)$")
    public void the_result_is(Integer expected) {
        System.out.println("Expecting a result of " + expected);
        assertEquals(expected, calc.value());
    }

//    @Before({"~@foo"})
    @Before
    public void before(Scenario scenario) {
//        scenario.write("Some special text");
//        int i = 0;
    }

    @After
    public void after(Scenario scenario) {
    }

    @Given("^the previous entries:$")
    public void thePreviousEntries(List<Entry> entries) {
        for (Entry entry : entries) {
            calc.push(entry.first);
            calc.push(entry.second);
            calc.push(entry.operation);
        }
    }

    @When("^I enter (\\d+) and (\\d+)$")
    public void entering(int arg1, int arg2) {
        calc.push(arg1);
        calc.push(arg2);
    }


    public static class Entry {
        Integer first;
        Integer second;
        String operation;

        public Entry(Integer first, Integer second, String operation){
            this.first = first;
            this.second = second;
            this.operation = operation;
        }
    }
}
