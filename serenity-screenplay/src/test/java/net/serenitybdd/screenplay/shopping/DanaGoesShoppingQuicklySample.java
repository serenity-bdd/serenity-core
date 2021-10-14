package net.serenitybdd.screenplay.shopping;

import net.serenitybdd.core.collect.NewList;
import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.GivenWhenThen;
import net.serenitybdd.screenplay.ThisTakesTooLong;
import net.serenitybdd.screenplay.shopping.questions.NestedThankYouMessage;
import net.serenitybdd.screenplay.shopping.tasks.Checkout;
import net.serenitybdd.screenplay.shopping.tasks.HaveItemsDelivered;
import net.serenitybdd.screenplay.shopping.tasks.Purchase;
import net.serenitybdd.screenplay.waits.Wait;
import net.thucydides.core.annotations.Steps;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Every;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static java.util.function.Predicate.isEqual;
import static net.serenitybdd.screenplay.EventualConsequence.eventually;
import static net.serenitybdd.screenplay.GivenWhenThen.*;
import static net.serenitybdd.screenplay.shopping.questions.DisplayedPrices.*;
import static net.serenitybdd.screenplay.shopping.questions.NextPersonToBeServed.nextPersonToBeServed;
import static net.serenitybdd.screenplay.shopping.questions.ThankYouMessage.theThankYouMessage;
import static net.serenitybdd.screenplay.shopping.questions.TotalCost.theTotalCost;
import static net.serenitybdd.screenplay.shopping.questions.TotalCostIncludingDelivery.theTotalCostIncludingDelivery;
import static net.serenitybdd.screenplay.shopping.tasks.Checkout.fastCheckout;
import static net.serenitybdd.screenplay.shopping.tasks.Checkout.slowCheckout;
import static net.serenitybdd.screenplay.shopping.tasks.JoinTheCheckoutQueue.joinTheCheckoutQueue;
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

