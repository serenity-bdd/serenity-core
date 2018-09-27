package net.serenitybdd.screenplay.shopping.tasks;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Performable;
import net.thucydides.core.annotations.Step;

import static net.serenitybdd.screenplay.shopping.tasks.PlaceInBasket.placed_the_item_in_her_basket;
import static net.serenitybdd.screenplay.GivenWhenThen.andThat;
import static net.serenitybdd.screenplay.Tasks.instrumented;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * The role of a task is to simulate a high-level action performed by the user.
 * It is common practice to add methods, e.g.
 *
 */
public class Purchase implements Performable {

    String purchasedItem;
    int cost;
    String currency;


    public Purchase() {}

    public Purchase(String purchasedItem, int cost, String currency) {
        this.purchasedItem = purchasedItem;
        this.cost = cost;
        this.currency = currency;
    }

    public Boolean theItemWasPurchased = false;

    public static Purchase purchased() {return instrumented(Purchase.class);}
    public static Purchase purchase() {return instrumented(Purchase.class);}
    public static Purchase andPurchased() {return instrumented(Purchase.class);}

    @Step("Given {0} has purchased #purchasedItem for #cost #currency")
    public <T extends Actor> void performAs(T actor) {
        assertThat(cost).isGreaterThan(0);
        andThat(actor).has(placed_the_item_in_her_basket());

        theItemWasPurchased = true;
    }

    public Purchase anApple() {
        this.purchasedItem = "an apple";
        return this;
    }

    public Purchase aPear() {
        this.purchasedItem = "a pear";
        return this;
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
