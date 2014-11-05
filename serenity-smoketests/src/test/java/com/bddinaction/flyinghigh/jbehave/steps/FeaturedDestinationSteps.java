package com.bddinaction.flyinghigh.jbehave.steps;

import com.bddinaction.flyinghigh.jbehave.model.DestinationDeal;
import com.bddinaction.flyinghigh.jbehave.pages.HomePage;
import org.jbehave.core.annotations.Alias;
import org.jbehave.core.annotations.Then;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * A description goes here.
 * User: john
 * Date: 6/10/13
 * Time: 9:13 PM
 */
public class FeaturedDestinationSteps {

    HomePage homePage;

    @Then("she should see $featuredCount featured destinations")
    @Alias("he should see $featuredCount featured destinations")
    public void shouldSeeFeaturedDestinations(int featuredCount) {
        assertThat(homePage.getFeaturedDestinations().size()).isEqualTo(featuredCount);
    }

    @Then("the featured destinations should include $expectedDestination costing $price")
    public void featuredDestinationsShouldInclude(String expectedDestination, int price) {
        DestinationDeal expectedDeal = new DestinationDeal(expectedDestination, price);
        assertThat(homePage.getFeaturedDestinations()).contains(expectedDeal);
    }
}
