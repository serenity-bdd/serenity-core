package net.serenitybdd.journey.shopping;

import net.serenitybdd.journey.shopping.tasks.Purchase;
import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.screenplay.Actor;
import net.thucydides.core.annotations.Steps;
import org.junit.Test;
import org.junit.runner.RunWith;

import static net.serenitybdd.journey.shopping.tasks.Purchase.purchase;
import static net.serenitybdd.journey.shopping.tasks.Purchase.purchased;
import static net.serenitybdd.screenplay.GivenWhenThen.givenThat;

@RunWith(SerenityRunner.class)
public class DanaGoesShopping {

    Actor dana = Actor.named("Dana");

    @Steps
    Purchase purchased;

    @Test
    public void shouldBeAbleToPurchaseSomeItems() {
        givenThat(dana).has(purchased().anApple().thatCosts(10).dollars(),
                purchased.aPear().thatCosts(5).dollars());
    }

    // Expected to fail
    @Test
    public void shouldBeAbleToPurchaseAnItemForFree() {
        givenThat(dana).attemtpsTo(purchase().anApple().thatCosts(0).dollars(), // Will fail
                purchase().aPear().thatCosts(5).dollars());  // Should be skipped
    }

    // Expected to fail with an error
    @Test
    public void shouldBeAbleToPurchaseAnItemWithANegativeAmount() {
        givenThat(dana).attemtpsTo(purchase().anApple().thatCosts(-10).dollars(), // Will fail with an error
                                   purchase().aPear().thatCosts(5).dollars());  // Should be skipped
    }

}

