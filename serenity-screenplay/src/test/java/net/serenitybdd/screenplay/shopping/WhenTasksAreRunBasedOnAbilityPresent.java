package net.serenitybdd.screenplay.shopping;

import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.screenplay.Ability;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.NoMatchingAbilityException;
import net.serenitybdd.screenplay.conditions.PerformBasedOnAbility;
import net.serenitybdd.screenplay.shopping.tasks.Purchase;
import org.junit.Test;
import org.junit.runner.RunWith;

import static net.serenitybdd.screenplay.conditions.PossibleAction.givenTheyCan;
import static net.serenitybdd.screenplay.shopping.tasks.Purchase.purchase;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SerenityRunner.class)
public class WhenTasksAreRunBasedOnAbilityPresent {

    Actor actorWithoutAbilities = Actor.named("Dana");
    Actor actorWhoCanGoToTheStore = Actor.named("Bob").whoCan(new GoToTheStore());
    Actor actorWhoCanBuyApples = Actor.named("Peter").whoCan(new PurchaseApples());

    @Test(expected = NoMatchingAbilityException.class)
    public void havingNoAbilityResultsInException() {
        actorWithoutAbilities.attemptsTo(PerformBasedOnAbility.checkInOrder(
            givenTheyCan(PurchaseApples.class).theyAttemptTo(new Purchase())
        ));
    }

    @Test
    public void whenHavingMatchingAbility() {
        Purchase purchase = purchase().anApple().thatCosts(1).dollars();
        actorWhoCanGoToTheStore.attemptsTo(new PerformBasedOnAbility(
            givenTheyCan(GoToTheStore.class).theyAttemptTo(purchase)
        ));
        assertThat(purchase.theItemWasPurchased).isTrue();
    }

    @Test
    public void whenHavingMoreSpecificAbility() {
        Purchase purchase = purchase().anApple().thatCosts(1).dollars();
        actorWhoCanBuyApples.attemptsTo(PerformBasedOnAbility.checkInOrder(
            givenTheyCan(GoToTheStore.class).theyAttemptTo(purchase)
        ));
        assertThat(purchase.theItemWasPurchased).isTrue();
    }

    @Test
    public void whenHavingMultipleOptionsFirstMatchingIsPicked() {
        Purchase purchaseApple = purchase().anApple().thatCosts(1).dollars();
        Purchase purchasePear = purchase().aPear().thatCosts(1).dollars();
        actorWhoCanBuyApples.attemptsTo(PerformBasedOnAbility.checkInOrder(
            givenTheyCan(PurchasePears.class).theyAttemptTo(purchasePear),
            givenTheyCan(PurchaseApples.class).theyAttemptTo(purchaseApple),
            givenTheyCan(GoToTheStore.class).theyAttemptTo(purchasePear, purchaseApple)
        ));
        assertThat(purchaseApple.theItemWasPurchased).isTrue();
        assertThat(purchasePear.theItemWasPurchased).isFalse();
    }

    private class GoToTheStore implements Ability { }
    private class PurchaseApples extends GoToTheStore {}
    private class PurchasePears extends GoToTheStore {}

}

