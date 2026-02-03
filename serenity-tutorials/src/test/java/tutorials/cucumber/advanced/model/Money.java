package tutorials.cucumber.advanced.model;

import java.util.Objects;

/**
 * Represents a monetary amount with currency.
 * Used by custom parameter type to transform currency strings in Gherkin steps.
 */
public class Money {
    private final double amount;
    private final String currency;

    public Money(double amount, String currency) {
        this.amount = amount;
        this.currency = currency;
    }

    public double getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public Money add(Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException("Cannot add different currencies");
        }
        return new Money(this.amount + other.amount, this.currency);
    }

    public Money subtract(Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException("Cannot subtract different currencies");
        }
        return new Money(this.amount - other.amount, this.currency);
    }

    public Money applyDiscount(int percentage) {
        double discountedAmount = amount * (100 - percentage) / 100.0;
        return new Money(discountedAmount, currency);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Money money = (Money) o;
        return Double.compare(money.amount, amount) == 0
            && Objects.equals(currency, money.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount, currency);
    }

    @Override
    public String toString() {
        String symbol = switch (currency) {
            case "USD" -> "$";
            case "EUR" -> "€";
            case "GBP" -> "£";
            default -> currency + " ";
        };
        return symbol + String.format("%.2f", amount);
    }
}
