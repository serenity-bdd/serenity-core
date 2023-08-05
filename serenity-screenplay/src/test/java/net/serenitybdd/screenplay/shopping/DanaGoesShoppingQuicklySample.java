package net.serenitybdd.screenplay.shopping;

import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.shopping.tasks.HaveItemsDelivered;
import net.serenitybdd.screenplay.shopping.tasks.Purchase;
import net.serenitybdd.annotations.Steps;
import org.junit.Test;
import org.junit.runner.RunWith;

import static net.serenitybdd.screenplay.GivenWhenThen.*;
import static net.serenitybdd.screenplay.shopping.questions.ThankYouMessage.theThankYouMessage;
import static net.serenitybdd.screenplay.shopping.questions.TotalCost.theTotalCost;
import static net.serenitybdd.screenplay.shopping.questions.TotalCostIncludingDelivery.theTotalCostIncludingDelivery;
import static net.serenitybdd.screenplay.shopping.tasks.Purchase.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;

@RunWith(SerenityRunner.class)
public class DanaGoesShoppingQuicklySample {

    Actor dana = Actor.named("Dana");

    @Steps
    Purchase purchased;

    @Steps
    HaveItemsDelivered haveThemDelivered;

    @Test
    public void shouldBeAbleToPurchaseSomeItemsWithDelivery() {
        givenThat(dana).has(purchased().anApple().thatCosts(10).dollars(),
                andPurchased().aPear().thatCosts(5).dollars());

        when(dana).attemptsTo(haveThemDelivered);

        then(dana).should(seeThat(theTotalCost(), equalTo(15)),
                seeThat(theTotalCostIncludingDelivery(), greaterThanOrEqualTo(20)),
                seeThat(theThankYouMessage(), equalTo("Thank you")));
    }

    @Test
    public void shouldBeAbleToPurchaseSomeItems() {
        givenThat(dana).has(purchased().anApple().thatCosts(10).dollars(),
                purchased.aPear().thatCosts(5).dollars());
        when(dana).attemptsTo(haveThemDelivered);
    }

    // Expected to fail
    @Test
    public void shouldBeAbleToPurchaseAnItemForFree() {

        givenThat(dana).attemptsTo(purchase().anApple().thatCosts(0).dollars(),
                purchase().aPear().thatCosts(5).dollars());
        then(dana).should(seeThat(theTotalCost(), equalTo(15)));
    }

    // Expected to fail with an error
    @Test
    public void shouldBeAbleToPurchaseAnItemWithANegativeAmount() {
        givenThat(dana).attemptsTo(purchase().anApple().thatCosts(-10).dollars(), // Will fail with an error
                purchase().aPear().thatCosts(5).dollars());  // Should be skipped
    }
}

