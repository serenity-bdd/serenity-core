package com.bddinaction.flyinghigh.jbehave.steps;

import com.bddinaction.flyinghigh.jbehave.flowsteps.TravellerFlowSteps;
import com.bddinaction.flyinghigh.jbehave.pages.BookingPage;
import net.thucydides.core.annotations.Steps;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.jbehave.core.model.ExamplesTable;

import java.util.List;
import java.util.Map;

import static org.fest.assertions.api.Assertions.assertThat;

public class BookingAFlightSteps {

    @Steps
    TravellerFlowSteps traveller;

    @Given("I want to book a flight")
    public void givenIWantToBookAFlight() {
        traveller.navigateToFlightBookings();
    }

    @Given("the following flight timetable: $timetable")
    public void givenTimetable(ExamplesTable timetable) {
        for( Map<String, String> row : timetable.getRows()) {
            String number = row.get("number");
            String departure = row.get("departure");
            String destination = row.get("destination");
            String time = row.get("time");
        }
    }

    @Then("I should see the following flights: $expectedFlights")
    public void shouldSeeFlights(ExamplesTable expectedFlights) {
        for( Map<String, String> row : expectedFlights.getRows()) {
            String number = row.get("number");
            String departure = row.get("departure");
            String destination = row.get("destination");
            String time = row.get("time");
        }
    }

    @When("I search for $flightType flights from $from to $to in $travelClass")
    public void whenISearchForFlights(String flightType, String from,
                                      String to, String travelClass) {

        traveller.searchForFlights(flightType, from, to, travelClass);
    }

    @Then("I should see the list of all available flights")
    public void thenIShouldSeeTheListOfAllAvailableFlights() {
    }

    BookingPage bookingPage;

    @When("I enter '$prefix' into the 'from' city field")
    public void enterFromPrefix(String prefix) {
        bookingPage.setFrom(prefix);
    }

    @Then("I should see the following cities: $expectedCities")
    public void shouldSeeCities(List<String> expectedCities) {
        assertThat(bookingPage.getFromTypeaheads()).containsAll(expectedCities)
                                                   .hasSize(expectedCities.size());

    }

}
