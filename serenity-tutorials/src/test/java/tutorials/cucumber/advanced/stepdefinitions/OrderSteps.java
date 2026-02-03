package tutorials.cucumber.advanced.stepdefinitions;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import tutorials.cucumber.advanced.model.Money;
import tutorials.cucumber.advanced.model.Order;
import tutorials.cucumber.advanced.model.UserRole;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Step definitions demonstrating custom parameter types.
 * Shows how {date}, {money}, and {role} transform into domain objects.
 */
public class OrderSteps {

    // Thread-local storage for the current order (safe for parallel execution)
    private static final ThreadLocal<Order> currentOrder = new ThreadLocal<>();

    @Given("an order placed on {date}")
    public void anOrderPlacedOnDate(LocalDate orderDate) {
        Order order = new Order(UUID.randomUUID().toString(), orderDate);
        currentOrder.set(order);
        System.out.println("[STEP] Order placed on " + orderDate);
    }

    @Given("an order totaling {money}")
    public void anOrderTotaling(Money total) {
        Order order = currentOrder.get();
        if (order == null) {
            order = new Order(UUID.randomUUID().toString(), LocalDate.now());
            currentOrder.set(order);
        }
        order.setTotal(total);
        System.out.println("[STEP] Order total set to " + total);
    }

    @When("delivery is scheduled for {date}")
    public void deliveryScheduledForDate(LocalDate deliveryDate) {
        Order order = currentOrder.get();
        order.setDeliveryDate(deliveryDate);
        System.out.println("[STEP] Delivery scheduled for " + deliveryDate);
    }

    @When("a {role} applies a {int}% discount")
    public void roleAppliesDiscount(UserRole role, int percentage) {
        // Verify the role has permission
        assertThat(role.canWrite())
            .as("Role %s should have write permission to apply discounts", role)
            .isTrue();

        Order order = currentOrder.get();
        Money discountedTotal = order.getTotal().applyDiscount(percentage);
        order.setTotal(discountedTotal);

        System.out.println("[STEP] " + role.getDisplayName() + " applied "
            + percentage + "% discount. New total: " + discountedTotal);
    }

    @Then("the delivery date should be after {date}")
    public void deliveryDateShouldBeAfter(LocalDate compareDate) {
        Order order = currentOrder.get();

        assertThat(order.getDeliveryDate())
            .as("Delivery date should be after " + compareDate)
            .isAfter(compareDate);

        System.out.println("[STEP] Verified delivery date " + order.getDeliveryDate()
            + " is after " + compareDate);
    }

    @Then("the order total should be {money}")
    public void orderTotalShouldBe(Money expectedTotal) {
        Order order = currentOrder.get();

        assertThat(order.getTotal().getAmount())
            .as("Order total should be " + expectedTotal)
            .isEqualTo(expectedTotal.getAmount());

        System.out.println("[STEP] Verified order total is " + expectedTotal);
    }

    @Then("the new total should be approximately {money}")
    public void newTotalShouldBeApproximately(Money expectedTotal) {
        Order order = currentOrder.get();

        assertThat(order.getTotal().getAmount())
            .as("Order total should be approximately " + expectedTotal)
            .isCloseTo(expectedTotal.getAmount(), org.assertj.core.data.Offset.offset(0.01));

        System.out.println("[STEP] Verified order total is approximately " + expectedTotal);
    }
}
