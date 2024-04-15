package net.serenitybdd.screenplay.shopping;

import net.serenitybdd.model.collect.NewList;
import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.GivenWhenThen;
import net.serenitybdd.screenplay.ThisTakesTooLong;
import net.serenitybdd.screenplay.shopping.questions.BrokenQuestion;
import net.serenitybdd.screenplay.shopping.questions.NestedThankYouMessage;
import net.serenitybdd.screenplay.shopping.tasks.Checkout;
import net.serenitybdd.screenplay.shopping.tasks.HaveItemsDelivered;
import net.serenitybdd.screenplay.shopping.tasks.Purchase;
import net.serenitybdd.annotations.Steps;
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
import static net.serenitybdd.screenplay.shopping.tasks.JoinTheCheckoutQueue.joinTheCheckoutQueue;
import static net.serenitybdd.screenplay.shopping.tasks.Purchase.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;

@RunWith(SerenityRunner.class)
public class DanaGoesShoppingWithoutWaitingSample {

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
    public void shouldBeAbleToPurchaseSomeItemsWithDeliveryUsingPredicates() {
        givenThat(dana).has(purchased().anApple().thatCosts(10).dollars(),
                andPurchased().aPear().thatCosts(5).dollars());

        when(dana).attemptsTo(haveThemDelivered);

        then(dana).should(seeThat("Total cost", theTotalCost(),
                GivenWhenThen.returnsAValueThat("is equal to 15", isEqual(15))));
    }

    @Test
    public void shouldBeAbleToPurchaseSomeItemsWithDeliveryAndCustomErrors() {
        givenThat(dana).has(purchased().anApple().thatCosts(10).dollars(),
                andPurchased().aPear().thatCosts(5).dollars());

        when(dana).attemptsTo(haveThemDelivered);

        then(dana).should(seeThat(theTotalCostIncludingDelivery(), greaterThanOrEqualTo(20)),
                seeThat(theThankYouMessage(), equalTo("You're welcome")));
    }

    @Test
    public void shouldBeAbleToPurchaseSomeItemsWithDeliveryAndCustomErrorsByQuestion() {
        givenThat(dana).has(purchased().anApple().thatCosts(10).dollars(),
                andPurchased().aPear().thatCosts(5).dollars());

        when(dana).attemptsTo(haveThemDelivered);

        then(dana).should(seeThat(theTotalCostIncludingDelivery(), greaterThanOrEqualTo(20)),
                seeThat(theThankYouMessage(), equalTo("You're welcome"))
                        .orComplainWith(PeopleAreSoImpolite.class));
    }

    @Test
    public void shouldBeAbleToPurchaseSomeItemsWithDeliveryAndCustomErrorsByQuestionWithACustomMessage() {
        givenThat(dana).has(purchased().anApple().thatCosts(10).dollars(),
                andPurchased().aPear().thatCosts(5).dollars());

        when(dana).attemptsTo(haveThemDelivered);

        then(dana).should(seeThat(theTotalCostIncludingDelivery(), greaterThanOrEqualTo(20)),
                seeThat(theThankYouMessage(), equalTo("You're welcome"))
                        .orComplainWith(PeopleAreSoImpolite.class, "You should say something nice"));
    }


    @Test
    public void shouldBeABleToEvaluateAllTheConsequencesInAGroup() {
        givenThat(dana).has(purchased().anApple().thatCosts(10).dollars(),
                andPurchased().aPear().thatCosts(5).dollars());

        when(dana).attemptsTo(haveThemDelivered);

        then(dana).should("{0} should see the correct messages",
                seeThat(theThankYouMessage(), equalTo("You're welcome")),
                seeThat(theThankYouMessage(), equalTo("No problem")),
                seeThat(theThankYouMessage(), equalTo("Thank you!"))
        );
    }

    @Test
    public void shouldBeAbleToEvaluateConsequenceGroups() {
        givenThat(dana).has(purchased().anApple().thatCosts(10).dollars(),
                andPurchased().aPear().thatCosts(5).dollars());

        when(dana).attemptsTo(new HaveItemsDelivered());

        then(dana).should(seeThat(thePriceIsCorrectlyDisplayed()));
    }

    @Test
    public void shouldBeAbleToEvaluateFailingConsequenceGroups() {
        givenThat(dana).has(purchased().anApple().thatCosts(10).dollars(),
                andPurchased().aPear().thatCosts(5).dollars());

        when(dana).attemptsTo(haveThemDelivered);

        then(dana).should(seeThat(thePriceIsIncorrectlyDisplayed()));
    }

    @Test
    public void shouldBeAbleToEvaluateErrorConsequenceGroups() {
        givenThat(dana).has(purchased().anApple().thatCosts(10).dollars(),
                            andPurchased().aPear().thatCosts(5).dollars());

        when(dana).attemptsTo(haveThemDelivered);

        then(dana).should(seeThat(thePriceIsIncorrectlyDisplayedWithAnError()));
    }


    @Test
    public void shouldBeAbleToEvaluateNestedGroup() {
        givenThat(dana).has(purchased().anApple().thatCosts(10).dollars(),
                andPurchased().aPear().thatCosts(5).dollars());

        when(dana).attemptsTo(haveThemDelivered);

        then(dana).should(
                seeThat(NestedThankYouMessage.theNestedThankYouMessage(), equalTo("Thank you!"))
        );
    }

    @Test
    public void shouldBeABleToEvaluateAVoidNestedGroup() {
        givenThat(dana).has(purchased().anApple().thatCosts(10).dollars(),
                andPurchased().aPear().thatCosts(5).dollars());

        when(dana).attemptsTo(haveThemDelivered);

        then(dana).should(
                seeThat(NestedThankYouMessage.theNestedThankYouMessage(), equalTo("Thank you!"))
        );
    }

    @Test
    public void shouldBeAbleToEvaluateAllTheConsequencesInANestedGroup() {
        givenThat(dana).has(purchased().anApple().thatCosts(10).dollars(),
                andPurchased().aPear().thatCosts(5).dollars());

        when(dana).attemptsTo(haveThemDelivered);

        then(dana).should(
                seeThat(theThankYouMessage(), equalTo("You're welcome")),
                seeThat(theThankYouMessage(), equalTo("No problem")),
                seeThat(theThankYouMessage(), equalTo("Thank you!"))
        );
    }


    @Test
    public void shouldBeAbleToAskForNiceThings() {
        Integer totalCost = dana.asksFor(theTotalCost());
        assertThat(totalCost).isEqualTo(14);
    }

    @Test
    public void shouldBeAbleToRememberAnswersToQuestions() {
        dana.remember("Total Cost", theTotalCost());
        int totalCost = dana.recall("Total Cost");
        assertThat(totalCost).isEqualTo(14);
    }

    @Test
    public void shouldBeAbleToRememberValues() {
        dana.remember("Total Cost", 14);
        assertThat((int) dana.recall("Total Cost")).isEqualTo(14);

        List<String> colorSet = NewList.of("red", "green", "blue");

        MatcherAssert.assertThat(colorSet, (Every.everyItem(isOneOf("red", "green", "blue", "yellow"))));
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

    // Expected to fail with two failues
    @Test
    public void shouldBeAbleToPurchaseAnItemWithAllTheRightDetails() {
        givenThat(dana).attemptsTo(purchase().anApple().thatCosts(10).dollars(),
                purchase().aPear().thatCosts(5).dollars());

        then(dana).should(seeThat(theTotalCost(), equalTo(20)),
                seeThat(theThankYouMessage(), equalTo("De nada")));
    }

    // Expected to fail with two failures and a compromised test
    @Test
    public void shouldBeAbleToPurchaseAnItemWithACompromisedTest() {
        givenThat(dana).attemptsTo(purchase().anApple().thatCosts(10).dollars(),
                purchase().aPear().thatCosts(5).dollars());

        then(dana).should(seeThat(theTotalCost(), equalTo(20)),
                seeThat(theThankYouMessage(), equalTo("De nada")).orComplainWith(PeopleAreTerriblyIncorrect.class),
                seeThat(theTotalCost(), equalTo(20)));
    }


    @Test
    public void shouldBeAbleToPurchaseAnItemWithABrokenQuestion() {
        givenThat(dana).attemptsTo(purchase().anApple().thatCosts(10).dollars(),
                purchase().aPear().thatCosts(5).dollars());

        then(dana).should(seeThat(BrokenQuestion.thatThrowsAnException(), equalTo(10)));
    }

    @Test
    public void shouldBeAbleToPurchaseAnItemWithAQuestionWithAFailingAssertion() {
        when(dana).should(seeThat(BrokenQuestion.thatThrowsAnAssertionError(), equalTo(10)));
    }

    // Expected to fail with two failures and a skipped
    @Test
    public void shouldBeAbleToPurchaseAnItemWithAllTheRightDetailsAndContinue() {
        givenThat(dana).attemptsTo(purchase().anApple().thatCosts(10).dollars(),
                purchase().aPear().thatCosts(5).dollars());

        then(dana).should(seeThat(theTotalCost(), equalTo(20)));
        and(dana).should(seeThat(theThankYouMessage(), equalTo("De nada")));
    }

    @Test
    public void shouldBeAbleToWaitInCheckoutLine() {
        Checkout fastCheckout = fastCheckout();

        givenThat(dana).attemptsTo(joinTheCheckoutQueue().of(fastCheckout));

        then(dana).should(eventually(seeThat(
            nextPersonToBeServed().by(fastCheckout), is("Dana")
        )).waitingForNoLongerThan(3).seconds()
            .orComplainWith(ThisTakesTooLong.class));
    }
}

