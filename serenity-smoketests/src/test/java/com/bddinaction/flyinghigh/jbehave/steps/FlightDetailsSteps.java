package com.bddinaction.flyinghigh.jbehave.steps;

import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.jbehave.core.model.ExamplesTable;

/**
 * A description goes here.
 * User: john
 * Date: 4/02/2014
 * Time: 10:38 PM
 */
public class FlightDetailsSteps {
    @Given("I need to know the details of flight number $flightNumber")
    public void I_need_to_know_the_details_of_flight_number(String flightNumber) throws Throwable {
    }

    @When("I request the details about this flight")
    public void I_request_the_details_about_this_flight() throws Throwable {
    }

    @Then("I should receive the following: $flightDetails")
    public void I_should_receive_the_following(ExamplesTable flightDetails) throws Throwable {
    }
}
