package net.serenitybdd.screenplay.shopping;

import com.google.common.collect.ImmutableList;
import net.serenitybdd.PeopleAreTerriblyIncorrect;
import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.shopping.tasks.HaveItemsDelivered;
import net.serenitybdd.screenplay.shopping.tasks.Purchase;
import net.thucydides.core.annotations.Steps;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Every;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static net.serenitybdd.screenplay.GivenWhenThen.*;
import static net.serenitybdd.screenplay.shopping.questions.ThankYouMessage.theThankYouMessage;
import static net.serenitybdd.screenplay.shopping.questions.TotalCost.theTotalCost;
import static net.serenitybdd.screenplay.shopping.questions.TotalCostIncludingDelivery.theTotalCostIncludingDelivery;
import static net.serenitybdd.screenplay.shopping.tasks.Purchase.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;

@RunWith(SerenityRunner.class)
public class DanaGoesShoppingSample {

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
                        .orComplainWith(PeopleAreSoImpolite.class,"You should say something nice"));
    }

    @Test
    public void shouldBeAbleToAskForNiceThings() {
        Integer totalCost = dana.asksFor(theTotalCost());
        assertThat(totalCost).isEqualTo(14);
    }

    @Test
    public void shouldBeAbleToRememberAnswersToQuestions() {
        dana.remember("Total Cost", theTotalCost());
        assertThat(dana.recall("Total Cost")).isEqualTo(14);
    }

    @Test
    public void shouldBeAbleToRememberValues() {
        dana.remember("Total Cost", 14);
        assertThat(dana.recall("Total Cost")).isEqualTo(14);

        List<String> colorSet = ImmutableList.of("red","green","blue");

        MatcherAssert.assertThat(colorSet, (Every.everyItem(isOneOf("red","green","blue","yellow"))));
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

        givenThat(dana).attemptsTo(purchase().anApple().thatCosts(0).dollars(), // Will fail
                purchase().aPear().thatCosts(5).dollars());  // Should be skipped
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


    // Expected to fail with two failures and a skipped
    @Test
    public void shouldBeAbleToPurchaseAnItemWithAllTheRightDetailsAndContinue() {
        givenThat(dana).attemptsTo(purchase().anApple().thatCosts(10).dollars(),
                purchase().aPear().thatCosts(5).dollars());

        then(dana).should(seeThat(theTotalCost(), equalTo(20)));
        and(dana).should(seeThat(theThankYouMessage(), equalTo("De nada")));
    }
}

