package net.serenitybdd.journey.shopping;

import net.serenitybdd.journey.shopping.tasks.HaveItemsDelivered;
import net.serenitybdd.journey.shopping.tasks.Purchase;
import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.screenplay.Actor;
import net.thucydides.core.annotations.Steps;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static net.serenitybdd.journey.shopping.questions.TotalCost.theCorrectTotalCost;
import static net.serenitybdd.journey.shopping.questions.TotalCost.theTotalCost;
import static net.serenitybdd.journey.shopping.questions.TotalCostIncludingDelivery.theTotalCostIncludingDelivery;
import static net.serenitybdd.journey.shopping.tasks.Purchase.*;
import static net.serenitybdd.screenplay.GivenWhenThen.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;

@RunWith(SerenityRunner.class)
public class WhenDanaGoesShopping {

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

        then(dana).should(seeThat(theCorrectTotalCost(), equalTo(15)));
    }

}

