package net.serenitybdd.journey.shopping.tasks;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.thucydides.core.annotations.Step;

import static net.serenitybdd.screenplay.Tasks.instrumented;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * The role of a task is to simulate a high-level action performed by the user.
 * It is common practice to add methods, e.g.
 *
 */
public class Purchase implements Task {

    String purchasedItem;
    int cost;
    String currency;

    public static Purchase purchased() {return instrumented(Purchase.class);}
    public static Purchase purchase() {return instrumented(Purchase.class);}

    public Purchase anApple() {
        this.purchasedItem = "an apple";
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
        if (cost < 0) {
            throw new IllegalArgumentException("Cost cannot be negative");
        }
        return this;
    }

    public Purchase dollars() {
        this.currency = "dollars";
        return this;
    }

}
