package net.serenitybdd.screenplay.shopping;

import java.util.Arrays;
import net.serenitybdd.core.pages.ListOfWebElementFacades;
import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.conditions.Check;
import net.serenitybdd.screenplay.shopping.tasks.Purchase;
import net.serenitybdd.screenplay.targets.Target;
import org.mockito.Mockito;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;

import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.isCurrentlyVisible;
import static net.serenitybdd.screenplay.shopping.tasks.Purchase.purchase;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SerenityRunner.class)
public class WhenTasksAreRunConditionally {

    Actor dana = Actor.named("Dana");

    @Test
    public void shouldBeAbleToPerformATaskConditionally() {
        int cost = 10;
        Purchase purchaseAnApple = purchase().anApple().thatCosts(10).dollars();
        Purchase purchaseAPear = purchase().aPear().thatCosts(5).dollars();

        dana.attemptsTo(
                Check.whether(cost>100)
                        .andIfSo(purchaseAPear)
                        .otherwise(purchaseAnApple)
        );

        assertThat(purchaseAPear.theItemWasPurchased).isFalse();
        assertThat(purchaseAnApple.theItemWasPurchased).isTrue();
    }

    @Test
    public void shouldBeAbleToPerformAnAlternativeTaskConditionally() {
        int cost = 10;
        Purchase purchaseAnApple = purchase().anApple().thatCosts(10).dollars();
        Purchase purchaseAPear = purchase().aPear().thatCosts(5).dollars();

        dana.attemptsTo(
                Check.whether(cost<100)
                        .andIfSo(purchaseAPear)
                        .otherwise(purchaseAnApple)
        );

        assertThat(purchaseAPear.theItemWasPurchased).isTrue();
        assertThat(purchaseAnApple.theItemWasPurchased).isFalse();
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
    public void shouldBeAbleToPerformATaskConditionallyUsingAQuestion() {

        Purchase purchaseAPear = purchase().aPear().thatCosts(5).dollars();
        Purchase purchaseAnApple = purchase().anApple().thatCosts(10).dollars();

        IsExpensive itIsTooExpensive = new IsExpensive(5);


        dana.attemptsTo(
                Check.whether(itIsTooExpensive)
                        .andIfSo(purchaseAPear)
                        .otherwise(purchaseAnApple)
        );

        assertThat(purchaseAnApple.theItemWasPurchased).isTrue();
        assertThat(purchaseAPear.theItemWasPurchased).isFalse();
    }

    @Test
    public void shouldBeAbleToPerformAnAlternativeTaskConditionallyUsingAQuestion() {

        Purchase purchaseAPear = purchase().aPear().thatCosts(5).dollars();
        Purchase purchaseAnApple = purchase().anApple().thatCosts(10).dollars();

        IsExpensive itIsTooExpensive = new IsExpensive(15);


        dana.attemptsTo(
                Check.whether(itIsTooExpensive)
                        .andIfSo(purchaseAPear)
                        .otherwise(purchaseAnApple)
        );

        assertThat(purchaseAnApple.theItemWasPurchased).isFalse();
        assertThat(purchaseAPear.theItemWasPurchased).isTrue();
    }

    @Test
    public void shouldBeAbleToConditionallyWithExplicitMatchers() {

        Purchase purchaseAPear = purchase().aPear().thatCosts(5).dollars();
        Purchase purchaseAnApple = purchase().anApple().thatCosts(10).dollars();

        IsExpensive itIsTooExpensive = new IsExpensive(15);

        Question<Integer> theCost = actor -> 30;

        dana.attemptsTo(
                Check.whether(theCost, Matchers.is(30))
                        .andIfSo(purchaseAPear)
                        .otherwise(purchaseAnApple)
        );

        assertThat(purchaseAnApple.theItemWasPurchased).isFalse();
        assertThat(purchaseAPear.theItemWasPurchased).isTrue();
    }

    @Test
    public void shouldBeAbleToPerformATaskConditionallyUsingATargetStateMatcher() {
        Purchase purchaseAPear = purchase().aPear().thatCosts(5).dollars();
        Purchase purchaseAnApple = purchase().anApple().thatCosts(10).dollars();

        Target target = Mockito.mock(Target.class);
        WebElementFacade webElementFacade = Mockito.mock(WebElementFacade.class);
        Mockito.when(target.resolveAllFor(dana))
                .thenReturn(new ListOfWebElementFacades(Arrays.asList(webElementFacade)));
        Mockito.when(webElementFacade.isCurrentlyVisible()).thenReturn(true);

        dana.attemptsTo(
                Check.whetherThe(target, isCurrentlyVisible()).andIfSo(purchaseAPear).otherwise(purchaseAnApple));

        assertThat(purchaseAnApple.theItemWasPurchased).isFalse();
        assertThat(purchaseAPear.theItemWasPurchased).isTrue();
    }

    @Test
    public void shouldBeAbleToPerformAnAlternativeTaskConditionallyUsingATargetStateMatcher() {
        Purchase purchaseAPear = purchase().aPear().thatCosts(5).dollars();
        Purchase purchaseAnApple = purchase().anApple().thatCosts(10).dollars();

        Target target = Mockito.mock(Target.class);
        WebElementFacade webElementFacade = Mockito.mock(WebElementFacade.class);
        Mockito.when(target.resolveAllFor(dana))
                .thenReturn(new ListOfWebElementFacades(Arrays.asList(webElementFacade)));
        Mockito.when(webElementFacade.isCurrentlyVisible()).thenReturn(false);

        dana.attemptsTo(
                Check.whetherThe(target, isCurrentlyVisible()).andIfSo(purchaseAPear).otherwise(purchaseAnApple));

        assertThat(purchaseAnApple.theItemWasPurchased).isTrue();
        assertThat(purchaseAPear.theItemWasPurchased).isFalse();
    }
}

