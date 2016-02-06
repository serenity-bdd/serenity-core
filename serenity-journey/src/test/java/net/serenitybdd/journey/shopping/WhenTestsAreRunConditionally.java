package net.serenitybdd.journey.shopping;

import net.serenitybdd.journey.shopping.tasks.Purchase;
import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.Unless;
import org.junit.Test;
import org.junit.runner.RunWith;

import static net.serenitybdd.journey.shopping.tasks.Purchase.purchase;
import static net.serenitybdd.screenplay.GivenWhenThen.when;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SerenityRunner.class)
public class WhenTestsAreRunConditionally {

    Actor dana = Actor.named("Dana");

    @Test
    public void shouldBeAbleToBuySomethingConditionally() {
        int cost = 10;

        Purchase purchase = purchase();

        purchase.theItemWasPurchased = false;
        when(dana).attemptsTo(Unless.the(cost < 100, purchase.aPear().thatCosts(5).dollars()));

        assertThat(purchase.theItemWasPurchased).isFalse();
    }

    @Test
    public void shouldBeAbleToNotBuySomethingConditionally() {
        int cost = 10;
        Purchase purchase = purchase();

        purchase.theItemWasPurchased = false;
        when(dana).attemptsTo(Unless.the(cost > 100, purchase.aPear().thatCosts(5).dollars()));

        assertThat(purchase.theItemWasPurchased).isTrue();
    }

    private class IsExpensive implements Question<Boolean> {

        private final int cost;

        public IsExpensive(int cost) {
            this.cost = cost;
        }

        @Override
        public Boolean answeredBy(Actor actor) {
            return (cost > 10);
        }
    }


    @Test
    public void shouldBeAbleToBuySomethingConditionallyUsingAQuestion() {
        int cost = 5;
        Purchase purchase = purchase();
        IsExpensive isTooExpensive = new IsExpensive(cost);

        purchase.theItemWasPurchased = false;
        when(dana).attemptsTo(Unless.it(isTooExpensive, purchase.aPear().thatCosts(5).dollars()));

        assertThat(purchase.theItemWasPurchased).isTrue();
    }

    @Test
    public void shouldBeAbleToNotBuySomethingConditionallyUsingAQuestion() {
        int cost = 20;
        Purchase purchase = purchase();
        IsExpensive isTooExpensive = new IsExpensive(cost);

        purchase.theItemWasPurchased = false;
        when(dana).attemptsTo(Unless.it(isTooExpensive, purchase.aPear().thatCosts(5).dollars()));

        assertThat(purchase.theItemWasPurchased).isFalse();
    }

}

