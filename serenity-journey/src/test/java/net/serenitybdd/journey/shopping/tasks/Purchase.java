package net.serenitybdd.journey.shopping.tasks;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.thucydides.core.annotations.Step;
import net.thucydides.core.steps.StepFactory;

import static org.assertj.core.api.Assertions.assertThat;

public class Purchase implements Task {

    String purchasedItem;
    int cost;
    String currency;

    static StepFactory stepFactory = new StepFactory();

    public static Purchase purchased() {return instrumented(Purchase.class);}

    private static Purchase instrumented(Class<Purchase> purchaseClass) {
        return stepFactory.getUniqueStepLibraryFor(purchaseClass);
    }

    public static Purchase purchase() {return instrumented(Purchase.class);}

    public Purchase anApple() {
        this.purchasedItem = "an apple";
        return this;
    }

    private Purchase forItem(String item) {
        this.purchasedItem = item;
        return this;
    }

    public Purchase aPear() {
        this.purchasedItem = "a pear";
        return this;
    }


    @Step("Given {0} has purchased #purchasedItem for #cost #currency")
    public void performAs(Actor actor) {
        assertThat(cost).isGreaterThan(0);
    }

    public Purchase thatCosts(int cost) {
        this.cost = cost;
        return this;
    }

    public Purchase dollars() {
        this.currency = "dollars";
        return this;
    }

}
