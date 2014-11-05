package com.bddinaction.flyinghigh.junit;

import com.bddinaction.flyinghigh.jbehave.flowsteps.TravellerFlowSteps;
import com.bddinaction.flyinghigh.jbehave.model.FrequentFlyerMember;
import com.bddinaction.flyinghigh.jbehave.pages.BookingPage;
import com.google.common.collect.ImmutableList;
import net.thucydides.core.annotations.Issue;
import net.thucydides.core.annotations.Managed;
import net.thucydides.core.annotations.ManagedPages;
import net.thucydides.core.annotations.Steps;
import net.thucydides.core.pages.Pages;
import net.thucydides.junit.runners.ThucydidesRunner;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;

import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(ThucydidesRunner.class)
@Ignore
//@Story(Application.ViewingFlights.displaying_flight_details.class)
@Issue("FH-18")
public class WhenDisplayingMoreFlightDetails {

    @Managed
    WebDriver driver;

    @ManagedPages(defaultUrl = "http://localhost:8080/#/welcome")
    public Pages pages;

    @Steps
    private TravellerFlowSteps registeredMember;

    @Test
    public void display_flight_details_to_featured_destinations() {
        registeredMember.enterEmailAndPasswordFor(FrequentFlyerMember.Jane);
        registeredMember.shouldSeeFeaturedDestinations(3);
    }

    @BeforeClass
    public static void setup() {
        assertThat(false).isFalse();
    }


    @Test
    public void display_flight_durations() {
        Assume.assumeTrue("Favorite destinations service is ready", false);
    }

    @Test
    public void display_flight_for_all_available_flights() {
        registeredMember.navigateToFlightBookings();
        registeredMember.searchForFlights("return", "Sydney", "London", "Business");
    }

    public BookingPage bookingPage;

    @Test
    public void display_flight_details_with_lookahead() {
        registeredMember.navigateToFlightBookings();
        registeredMember.searchForFlightsFromCitiesStartingWith("Se");

        List<String> proposedCities = registeredMember.getProposedDepartureCities();
        assertThat(proposedCities).containsAll(ImmutableList.of("Seattle","Seoul"))
                .hasSize(2);
    }

}
