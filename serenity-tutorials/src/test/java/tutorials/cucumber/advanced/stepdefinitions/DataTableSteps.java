package tutorials.cucumber.advanced.stepdefinitions;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.DataTableType;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import tutorials.cucumber.advanced.model.Money;
import tutorials.cucumber.advanced.model.Order;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Step definitions demonstrating data table handling.
 * Shows horizontal tables, vertical tables, and custom transformations.
 */
public class DataTableSteps {

    // Thread-local storage for the current order (safe for parallel execution)
    private static final ThreadLocal<Order> currentOrder = new ThreadLocal<>();
    private static final ThreadLocal<Map<String, String>> customerData = new ThreadLocal<>();

    /**
     * Custom DataTableType that transforms table rows into OrderItem objects.
     */
    @DataTableType
    public Order.OrderItem orderItemEntry(Map<String, String> entry) {
        String name = entry.get("item");
        int quantity = Integer.parseInt(entry.get("quantity"));
        double price = Double.parseDouble(entry.get("price").replace("$", ""));

        return new Order.OrderItem(name, quantity, new Money(price, "USD"));
    }

    @Given("an order with the following items:")
    public void anOrderWithItems(List<Order.OrderItem> items) {
        Order order = new Order();

        for (Order.OrderItem item : items) {
            order.addItem(item);
            System.out.println("[STEP] Added item: " + item.getName()
                + " x" + item.getQuantity() + " @ " + item.getPrice());
        }

        // Calculate total
        double total = items.stream()
            .mapToDouble(item -> item.getSubtotal().getAmount())
            .sum();
        order.setTotal(new Money(total, "USD"));

        currentOrder.set(order);
        System.out.println("[STEP] Order created with total: $" + total);
    }

    @Given("a customer with the following details:")
    public void customerWithDetails(DataTable dataTable) {
        // Vertical table: key-value pairs
        Map<String, String> data = dataTable.asMap();
        customerData.set(data);

        System.out.println("[STEP] Customer: " + data.get("First Name")
            + " " + data.get("Last Name"));
    }

    @When("the order is processed")
    public void orderIsProcessed() {
        Order order = currentOrder.get();
        order.setStatus("PROCESSED");

        System.out.println("[STEP] Order processed");
    }

    @Then("the order should contain {int} items")
    public void orderShouldContainItems(int expectedCount) {
        Order order = currentOrder.get();

        assertThat(order.getItems())
            .as("Order should contain %d items", expectedCount)
            .hasSize(expectedCount);
    }

    @Then("the calculated order total should be ${double}")
    public void calculatedOrderTotalShouldBe(double expectedTotal) {
        Order order = currentOrder.get();

        assertThat(order.getTotal().getAmount())
            .as("Order total should be $%.2f", expectedTotal)
            .isCloseTo(expectedTotal, org.assertj.core.data.Offset.offset(0.01));
    }

    @Then("the order status should be {string}")
    public void orderStatusShouldBe(String expectedStatus) {
        Order order = currentOrder.get();

        assertThat(order.getStatus())
            .as("Order status should be %s", expectedStatus)
            .isEqualTo(expectedStatus);
    }
}
