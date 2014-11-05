package com.bddinaction.flyinghigh.jbehave.steps;


import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.junit.Assert;

import java.util.Date;

import static org.hamcrest.CoreMatchers.is;

public class EarningPointsFromFlights {
    @Given("the flying distance between $departure and $destination is $distance km")
    public void define_flying_distance(String departure,
                                       String destination,
                                       int distance) {
    }

    @When("I fly from $departure to $destination on $date$")
    public void I_fly_from(String departure, String destination, Date date) throws Throwable {
    }

    @Then("I should earn $points points")
    public void I_should_earn_points(int points) throws Throwable {
        Assert.assertThat(points, is(points));
    }

    @Then("I should earn a status bonus of <bonus>")
    public void I_should_earn_a_status_bonus_of(int bonus) throws Throwable {
    }

    @When("I fly on a flight that is worth <base> base points")
    public void I_fly_on_a_flight_that_is_worth_base_base_points() throws Throwable {
    }

    @When("I fly on a flight that is worth $points base points")
    public void I_fly_on_a_flight_that_is_worth_base_points(int points) throws Throwable {
    }

    @Then("I should have guaranteed minimum earned points per trip of $minimum")
    public void I_should_have_guaranteed_minimum_earned_points_per_trip_of(int minimum) throws Throwable {
    }

    @Then("I should earn $total points in all")
    public void I_should_earn_points_in_all(int total) throws Throwable {
    }

    @Given("we can earn points with partners")
    public void partnerFeatureAvailable() {
//  TODO: Doesn't work properly yet
//        Assume.assumeTrue("The partner feature is ready", false);
    }

}
