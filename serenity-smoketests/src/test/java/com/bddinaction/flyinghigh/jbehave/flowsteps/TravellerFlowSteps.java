package com.bddinaction.flyinghigh.jbehave.flowsteps;

import com.bddinaction.flyinghigh.jbehave.pages.BookingPage;
import net.thucydides.core.annotations.Screenshots;
import net.thucydides.core.annotations.Step;

import java.util.List;

import static org.fest.assertions.Assertions.assertThat;

public class TravellerFlowSteps extends AuthenticationFlowSteps {

    BookingPage bookingPage;

    @Step
    @Screenshots(onlyOnFailures=true)
    public void navigateToFlightBookings() {
        loginPage.open();
        loginPage.inMainMenu().selectMenuOption("Book");
        bookingPage.waitUntilDisplayed();
    }

    @Step("Search for {0} flights from {1} to {2} in {3} class")
    public void searchForFlights(String flightType, String from,
                                 String to, String travelClass) {
        navigateToFlightBookings();
        bookingPage.setFlightType(flightType);
        bookingPage.setFrom(from);
        bookingPage.setTo(to);
        bookingPage.setTravelClass(travelClass);
        bookingPage.searchButton().shouldNotBeEnabled();
    }


    @Step
    public void shouldSeeFeaturedDestinations(int featureCount) {
        assertThat(homePage.getFeaturedDestinations().size()).isEqualTo(featureCount);
    }

    public void searchForFlightsFromCitiesStartingWith(String prefix) {
        bookingPage.setFrom(prefix);
    }

    public List<String> getProposedDepartureCities() {
        return bookingPage.getFromTypeaheads();
    }

    @Step
    public void registerViaFacebook() {

    }
}
