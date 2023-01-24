package caffeinateme.model;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

public class Order {
    private final Customer customer;
    private final List<OrderItem> items;
    private OrderStatus status;
    private String comment;
    private final Long orderNumber;

    private final static AtomicLong ORDER_NUMBERS = new AtomicLong();

    public Order(List<OrderItem> items, Customer customer) {
        this(items, customer, OrderStatus.Normal, "");
    }

    public Order(List<OrderItem> items, Customer customer, String comment) {
        this(items, customer, OrderStatus.Normal, comment);
    }

    public Order(List<OrderItem> items, Customer customer, OrderStatus status, String comment) {
        this(items, customer, status, comment, ORDER_NUMBERS.incrementAndGet());
    }


    public Order(List<OrderItem> items, Customer customer, OrderStatus status, String comment, long orderNumber) {
        this.items = items;
        this.customer = customer;
        this.status = status;
        this.comment = comment;
        this.orderNumber = orderNumber;
    }

    public Order withComment(String comment) {
        this.comment = comment;
        return this;
    }

    public Order withStatus(OrderStatus status) {
        this.status = status;
        return this;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public List<OrderItem> getItems() { return items; }

    public String getComment() { return comment; }

    public Customer getCustomer() {
        return customer;
    }

    public Optional<OrderItem> getOrderItemForProduct(String productName) {
        return items.stream().filter(item -> item.product().equals(productName)).findFirst();
    }

    public static OrderBuilder of(int quantity, String product) {
        return new OrderBuilder(quantity, product);
    }

    public static class OrderBuilder {

        private final int quantity;
        private final String product;

        public OrderBuilder(int quantity, String product) {
            this.quantity = quantity;
            this.product = product;
        }

        public Order forCustomer(Customer customerName) {
            return new Order(List.of(new OrderItem(product, quantity)), customerName);
        }
    }
}
