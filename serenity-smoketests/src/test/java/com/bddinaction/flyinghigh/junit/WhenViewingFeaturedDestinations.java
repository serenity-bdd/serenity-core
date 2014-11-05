package com.bddinaction.flyinghigh.junit;

import com.bddinaction.flyinghigh.jbehave.flowsteps.TravellerFlowSteps;
import com.bddinaction.flyinghigh.jbehave.model.FrequentFlyerMember;
import net.thucydides.core.annotations.Issue;
import net.thucydides.core.annotations.Managed;
import net.thucydides.core.annotations.ManagedPages;
import net.thucydides.core.annotations.Steps;
import net.thucydides.core.pages.Pages;
import net.thucydides.junit.runners.ThucydidesRunner;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(ThucydidesRunner.class)
@Ignore
@Issue("FH-18")
public class WhenViewingFeaturedDestinations {

    @Managed
    WebDriver driver;

    @ManagedPages(defaultUrl = "http://localhost:8080/#/welcome")
    public Pages pages;

    @Steps
    private TravellerFlowSteps registeredMember;

    @Test
    public void should_display_flights_to_featured_destinations() {
        registeredMember.enterEmailAndPasswordFor(FrequentFlyerMember.Jane);
        registeredMember.shouldSeeFeaturedDestinations(3);
    }

    @BeforeClass
    public static void setup() {
        assertThat(false).isFalse();
    }

    @Test
    public void should_display_flight_durations() {
        //Assume.assumeTrue("Favorite destinations service is ready", false);
    }

}
