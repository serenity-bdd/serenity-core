package tutorials.cucumber.advanced.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents an order in the system.
 * Used in Cucumber scenarios to demonstrate data handling.
 */
public class Order {
    private String orderId;
    private LocalDate orderDate;
    private LocalDate deliveryDate;
    private Money total;
    private List<OrderItem> items;
    private String status;

    public Order() {
        this.items = new ArrayList<>();
        this.status = "PENDING";
    }

    public Order(String orderId, LocalDate orderDate) {
        this();
        this.orderId = orderId;
        this.orderDate = orderDate;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public LocalDate getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDate orderDate) {
        this.orderDate = orderDate;
    }

    public LocalDate getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(LocalDate deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public Money getTotal() {
        return total;
    }

    public void setTotal(Money total) {
        this.total = total;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void addItem(OrderItem item) {
        this.items.add(item);
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public static class OrderItem {
        private String name;
        private int quantity;
        private Money price;

        public OrderItem() {}

        public OrderItem(String name, int quantity, Money price) {
            this.name = name;
            this.quantity = quantity;
            this.price = price;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public Money getPrice() {
            return price;
        }

        public void setPrice(Money price) {
            this.price = price;
        }

        public Money getSubtotal() {
            return new Money(price.getAmount() * quantity, price.getCurrency());
        }
    }
}
