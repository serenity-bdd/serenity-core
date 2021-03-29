package serenityscreenplay.net.serenitybdd.screenplay.shopping;

import serenityscreenplay.net.serenitybdd.screenplay.Performable;
import serenityscreenplay.net.serenitybdd.screenplay.Task;
import serenityscreenplay.net.serenitybdd.screenplay.shopping.tasks.HaveItemsDelivered;
import serenityscreenplay.net.serenitybdd.screenplay.shopping.tasks.Purchase;
import net.serenitybdd.junit.runners.SerenityRunner;
import serenityscreenplay.net.serenitybdd.screenplay.Actor;
import serenitymodel.net.thucydides.core.annotations.Step;
import serenitycore.net.thucydides.core.annotations.Steps;
import org.junit.Test;
import org.junit.runner.RunWith;

import static serenityscreenplay.net.serenitybdd.screenplay.Tasks.instrumented;
import static serenityscreenplay.net.serenitybdd.screenplay.shopping.questions.TotalCost.theCorrectTotalCost;
import static serenityscreenplay.net.serenitybdd.screenplay.shopping.tasks.Purchase.*;
import static serenityscreenplay.net.serenitybdd.screenplay.GivenWhenThen.*;
import static org.hamcrest.Matchers.equalTo;

@RunWith(SerenityRunner.class)
public class WhenDanaGoesShopping {

    Actor dana = Actor.named("Dana");

    @Steps
    Purchase purchased;

    @Steps
    HaveItemsDelivered haveThemDelivered;

    Task checkTheInventory  = Task.where("{0} checks for #item", LookThroughTheBags.forItem("apples"))
                                  .with("item").of("apples");

    @Test
    public void shouldBeAbleToPurchaseSomeItemsWithDelivery() {
        givenThat(dana).has(purchased().anApple().thatCosts(10).dollars(),
                            andPurchased().aPear().thatCosts(5).dollars());

        when(dana).attemptsTo(haveThemDelivered, checkTheInventory);

        then(dana).should(seeThat(theCorrectTotalCost(), equalTo(15)));
    }

    @Test
    public void shouldBeAbleToPurchaseSomeItemsWithDeliveryUsingUninstrumentedClasses() {
        givenThat(dana).has(new Purchase("Apple", 10, "Dollars"),
                new Purchase("Pear", 5, "Dollars"));

        when(dana).attemptsTo(haveThemDelivered, checkTheInventory);

        then(dana).should(seeThat(theCorrectTotalCost(), equalTo(15)));
    }

    @Test
    public void shouldBeAbleToCheckHerStuff() {
        dana.attemptsTo(checkTheInventory);
    }

    public static class LookThroughTheBags implements Task {


        @Override
        @Step("Look through the bags")
        public <T extends Actor> void performAs(T actor) {
        }

        static Performable forItem(String items) {
            return instrumented(LookThroughTheBags.class);
        }
    }
}

