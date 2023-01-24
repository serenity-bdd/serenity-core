package caffeinateme.steps;

import caffeinateme.model.*;
import io.cucumber.java.Before;
import io.cucumber.java.DataTableType;
import io.cucumber.java.ParameterType;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import net.serenitybdd.core.Serenity;
import org.assertj.core.api.SoftAssertions;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class OrderCoffeeSteps {
    ProductCatalog catalog = new ProductCatalog();
    CoffeeShop coffeeShop = new CoffeeShop(catalog);
//    Order order;
    Customer customer;

    @Before
    public void before() {
        System.out.println("Running in thread " + Thread.currentThread());
    }
    @Given("{} is a CaffeinateMe customer")
    public void a_caffeinate_me_customer_named(String customerName) {
        customer = coffeeShop.registerNewCustomer(customerName);
    }

    @Given("Cathy is {int} metres from the coffee shop")
    public void cathy_is_metres_from_the_coffee_shop(Integer distanceInMetres) {
        customer.setDistanceFromShop(distanceInMetres);
    }

    @Given("{} has ordered a/an {string}")
    @When("{} orders a/an {string}")
    public void orders_a(String customerName, String productName) {
        customer = coffeeShop.findCustomerByName(customerName);
        Order order = Order.of(1, productName).forCustomer(customer);
        Serenity.setSessionVariable("ORDER").to(order);
        customer.placesAnOrderFor(order).at(coffeeShop);
    }

    @When("{word} orders a/an {string} with a comment {string}")
    public void orders_with_comment(String customerName, String productName, String comment) {
        customer = coffeeShop.findCustomerByName(customerName);
        Order order = Order.of(1, productName).forCustomer(customer).withComment(comment);
        Serenity.setSessionVariable("ORDER").to(order);
        customer.placesAnOrderFor(order).at(coffeeShop);
    }

    @Then("Barry should receive the order")
    public void barry_should_receive_the_order() {
        Order order = Serenity.sessionVariableCalled("ORDER");
        assertThat(coffeeShop.getPendingOrders()).contains(order);
    }

    @Then("^Barry should know that the order is (.*)")
    public void barry_should_know_that_the_order_is(OrderStatus expectedStatus) {
        Order cathysOrder = coffeeShop.getOrderFor(customer)
                .orElseThrow(() -> new AssertionError("No order found!"));
        assertThat(cathysOrder.getStatus()).isEqualTo(expectedStatus);
    }

    @Then("the order should have the comment {string}")
    public void order_should_have_comment(String comment) {
        Order order = coffeeShop.getOrderFor(customer).get();
        assertThat(order.getComment()).isEqualTo(comment);
    }

    @DataTableType
    public OrderItem orderItem(Map<String, String> row) {
        return new OrderItem(row.get("Product"), Integer.parseInt(row.get("Quantity")));
    }

    @When("^(.*) (?:has placed|places) an order for the following items:")
    public void lacesAnOrderForTheFollowingItems(String customerName, List<OrderItem> orderItems) {
        this.customer = coffeeShop.findCustomerByName(customerName);
        Order order = new Order(orderItems, customer);
        Serenity.setSessionVariable("ORDER").to(order);
        coffeeShop.placeOrder(order);
    }

    @When("^(.*) records the details of the order")
    public void recordOrderDetails(String customerName) {
        Order order = Serenity.sessionVariableCalled("ORDER");
        Serenity.recordReportData().withTitle("Order details for " + customerName).andContents(order.toString());
    }

    @And("the order should contain {int} line items")
    public void theOrderShouldContainLineItems(int expectedNumberOfLineItems) {
        Order order = coffeeShop.getOrderFor(customer).get();
        assertThat(order.getItems()).hasSize(expectedNumberOfLineItems);
    }

    @And("the order should contain the following products:")
    public void theOrderShouldContain(List<String> expectedProducts) {
        Order order = coffeeShop.getOrderFor(customer).get();
        List<String> productItems = order.getItems().stream().map(item -> item.product()).collect(Collectors.toList());
        assertThat(productItems).hasSameElementsAs(expectedProducts);
    }

    @DataTableType
    public ProductPrice productPrice(Map<String, String> entry) {
        return new ProductPrice(entry.get("Product"),
                Double.parseDouble(entry.get("Price")));
    }

    @Given("the following prices:")
    public void setupCatalog(List<ProductPrice> productPrices) {
        catalog.addProductsWithPrices(productPrices);
    }

    Receipt receipt;

    @When("he/she asks for a receipt")
    public void sheAsksForAReceipt() {
        receipt = coffeeShop.getReceiptFor(customer);
    }

    @Then("she should receive a receipt totalling:")
    public void sheShouldReceiveAReceiptTotalling(Map<String, Double> receiptTotals) {
        double serviceFee = receiptTotals.get("Service Fee");
        double subtotal = receiptTotals.get("Subtotal");
        double total = receiptTotals.get("Total");

        assertThat(receipt.serviceFee()).isEqualTo(serviceFee);
        assertThat(receipt.subtotal()).isEqualTo(subtotal);
        assertThat(receipt.total()).isEqualTo(total);
    }

    @DataTableType
    public ReceiptLineItem receiptLineItem(Map<String, String> itemData) {
        return new ReceiptLineItem(
                itemData.get("Product"),
                Integer.parseInt(itemData.get("Quantity")),
                Double.parseDouble(itemData.get("Price"))
        );
    }

    @And("the receipt should contain the line items:")
    public void theReceiptShouldContainTheLineItems(List<ReceiptLineItem> expectedItems) {
        assertThat(receipt.lineItems()).containsExactlyElementsOf(expectedItems);
    }

    List<Order> pendingOrders;

    @When("Barry reviews the pending orders")
    public void barryReviewsThePendingOrders() {
        pendingOrders = coffeeShop.getPendingOrders();
    }

    @When("{word} cancels her order")
    public void cancelOrder(String customerName){
        coffeeShop.cancelOrderFor(customerName);
    }

    @DataTableType
    public OrderDetails orderDetails(Map<String, String> row) {
        return new OrderDetails(row.get("Customer"), row.get("Order"));
    }

    @Then("he/she should see the following orders:")
    public void shouldSeeOrders(List<OrderDetails> expectedOrders) {
        SoftAssertions softly = new SoftAssertions();

        for (OrderDetails orderDetails : expectedOrders) {
            Optional<Order> matchingOrder = firstPendingOrderMatching(orderDetails);
            softly.assertThat(matchingOrder).isPresent();
            matchingOrder.ifPresent(
                    order -> softly.assertThat(order.getOrderItemForProduct(orderDetails.productName())).isPresent()
            );
        }
        softly.assertAll();
    }

    @NotNull
    private Optional<Order> firstPendingOrderMatching(OrderDetails orderDetails) {
        return pendingOrders.stream()
                .filter(order -> order.getCustomer().getName().equals(orderDetails.customerName()))
                .findFirst();
    }
}
