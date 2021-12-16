package net.serenitybdd.cucumber.integration.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import net.serenitybdd.cucumber.integration.steps.thucydides.WidgetSteps;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.Task;
import net.thucydides.core.annotations.Steps;

import static net.serenitybdd.screenplay.GivenWhenThen.seeThat;
import static net.serenitybdd.screenplay.Tasks.instrumented;
import static org.hamcrest.Matchers.equalTo;

;

/**
 * Created by john on 23/07/2014.
 */
public class SampleWidgetSteps {

    private int quantity;
    private int cost;
    private int billedPrice;

    @Steps
    WidgetSteps widgetSteps;

    @Given("^I have \\$(.*)$")
    public void iHaveMoney(Integer money) {

    }

    @Given("^I want to purchase (.*) widgets$")
    public void wantToPurchaseWidgets(int quantity) {
        this.quantity = quantity;
    }

    @Given("^a widget costs \\$(.*)$")
    public void widgetsCost(int cost) {
        if (cost < 0) {
            throw new RuntimeException("Oh Crap!");
        }
        this.cost = cost;
    }

    @Given("^at a cost of (.*)$")
    public void widgetsCostAt(int cost) {
        if (cost < 0) {
            throw new RuntimeException("Oh Crap!");
        }
        this.cost = cost;
    }

    @When("I buy the widgets")
    public void buyWidgets() {
        billedPrice = cost * quantity;
    }

    @Then("^I should be billed \\$(.*)$")
    public void shouldBeBilled(int totalPrice) {
        widgetSteps.shouldBeBilled(billedPrice, totalPrice);
    }

    Actor john = Actor.named("John");

    public static class BuyGizmos implements Task {

        private final int quantity;

        public BuyGizmos(int quantity) {
            this.quantity = quantity;
        }

        @Override
        public <T extends Actor> void performAs(T actor) {
        }

        public static Performable times(int quantity) {
            return instrumented(BuyGizmos.class, quantity);
        }
    }

    public static class OrderGizmos implements Task {

        private final int quantity;

        public OrderGizmos(int quantity) {
            this.quantity = quantity;
        }

        @Override
        public <T extends Actor> void performAs(T actor) {
            if (quantity == -1) {
                throw new RuntimeException("Oh crap!");
            }

        }

        public static Performable times(int quantity) {
            return instrumented(OrderGizmos.class, quantity);
        }
    }

    public static class TheTotalPrice implements Question<Integer> {

        private final int billedPrice;

        public TheTotalPrice(int billedPrice) {

            this.billedPrice = billedPrice;
        }

        @Override
        public Integer answeredBy(Actor actor) {
            return billedPrice;
        }

        public static Question<? extends Integer> ofTheGizmos(int billedPrice) {
            return new TheTotalPrice(billedPrice);
        }
    }

    @Given("^I want to purchase (.*) gizmos$")
    public void wantToPurchaseGizmos(int quantity) {
        this.quantity = quantity;
    }

    @Given("^a gizmo costs \\$(.*)$")
    public void gizmosCost(int cost) {
        if (cost < 0) {
            throw new RuntimeException("Oh crap!");
        }
        this.cost = cost;
    }

    @When("I buy said gizmos")
    public void buyGizmo() {

        john.attemptsTo(BuyGizmos.times(quantity));
        billedPrice = cost * quantity;
    }

    @When("I order the gizmos")
    public void orderGizmo() {
        john.attemptsTo(OrderGizmos.times(quantity));
        billedPrice = cost * quantity;
    }

    @Then("^I should pay \\$(.*)$")
    public void shouldPay(int totalPrice) {
        john.should(seeThat(TheTotalPrice.ofTheGizmos(billedPrice), equalTo(totalPrice)));
    }

}
