package caffeinateme.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class CoffeeShop {

    private static final int MAX_DISTANCE = 10000;
    private Queue<Order> orders = new LinkedList<>();
    private Map<String, Customer> registeredCustomers = new HashMap<>();

    private final ProductCatalog productCatalog;

    public CoffeeShop(ProductCatalog productCatalog) {
        this.productCatalog = productCatalog;
    }

    public void placeOrder(Order order) {
        placeOrder(order, MAX_DISTANCE);
    }

    public void placeOrder(Order order, int distanceInMetres) {
        if (distanceInMetres <= 200) {
            order = order.withStatus(OrderStatus.Urgent);
        } else if (distanceInMetres <= 5000) {
            order = order.withStatus(OrderStatus.Normal);
        } else {
            order = order.withStatus(OrderStatus.Low);
        }
        orders.add(order);
    }

    public List<Order> getPendingOrders() {
        return new ArrayList<>(orders);
    }

    public Optional<Order> getOrderFor(Customer customer) {
        return orders.stream()
                .filter( order -> order.getCustomer().equals(customer))
                .findFirst();
    }

    public Customer registerNewCustomer(String customerName) {
        Customer newCustomer = Customer.named(customerName);
        registeredCustomers.put(customerName, newCustomer);
        return newCustomer;
    }

    public Customer findCustomerByName(String customerName) {
        return registeredCustomers.get(customerName);
    }

    public Receipt getReceiptFor(Customer customer) {
        Order order = orders.stream()
                .filter(customerOrder -> customerOrder.getCustomer().equals(customer)).findFirst()
                .orElseThrow(OrderNotFoundException::new);

        double subTotal = order.getItems().stream().mapToDouble(this::costOf).sum();

        List<ReceiptLineItem> lineItems = order.getItems().stream()
                .map( item -> new ReceiptLineItem(item.product(), item.quantity(), costOf(item)))
                .toList();

        double serviceFee = subTotal * 5 / 100;
        double total = subTotal + serviceFee;
        return new Receipt(roundedTo2DecimalPlaces(subTotal),
                roundedTo2DecimalPlaces(serviceFee),
                roundedTo2DecimalPlaces(total),
                lineItems);
    }

    private double costOf(OrderItem item) {
        return productCatalog.priceOf(item.product()) * item.quantity();
    }

    private double roundedTo2DecimalPlaces(double value) {
        return new BigDecimal(Double.toString(value)).setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }

    public void cancelOrderFor(String customerName) {
        this.orders.removeIf(order -> order.getCustomer().getName().equals(customerName));
    }
}
