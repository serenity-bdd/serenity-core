package tutorials.cucumber.advanced.parameters;

import io.cucumber.java.ParameterType;
import tutorials.cucumber.advanced.model.Money;
import tutorials.cucumber.advanced.model.UserRole;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Custom Cucumber parameter types that transform step arguments into domain objects.
 *
 * Usage in steps:
 *   Given an order placed on {date}       -> LocalDate
 *   When a {role} approves the request    -> UserRole enum
 *   Then the total is {money}             -> Money object
 */
public class CustomParameterTypes {

    /**
     * Transforms date strings into LocalDate objects.
     * Supports: "today", "tomorrow", "yesterday", or ISO dates like "2024-01-15"
     *
     * @param value the date string from the step
     * @return a LocalDate object
     */
    @ParameterType("today|tomorrow|yesterday|\\d{4}-\\d{2}-\\d{2}")
    public LocalDate date(String value) {
        return switch (value) {
            case "today" -> LocalDate.now();
            case "tomorrow" -> LocalDate.now().plusDays(1);
            case "yesterday" -> LocalDate.now().minusDays(1);
            default -> LocalDate.parse(value, DateTimeFormatter.ISO_LOCAL_DATE);
        };
    }

    /**
     * Transforms currency strings into Money objects.
     * Supports: $100.50, €50, £75.99
     *
     * @param value the money string from the step
     * @return a Money object with amount and currency
     */
    @ParameterType("[$€£][\\d,]+\\.?\\d*")
    public Money money(String value) {
        String currencySymbol = value.substring(0, 1);
        String currency = switch (currencySymbol) {
            case "$" -> "USD";
            case "€" -> "EUR";
            case "£" -> "GBP";
            default -> "USD";
        };
        double amount = Double.parseDouble(value.substring(1).replace(",", ""));
        return new Money(amount, currency);
    }

    /**
     * Transforms role strings into UserRole enum values.
     * Case-insensitive matching.
     *
     * @param value the role string from the step
     * @return a UserRole enum value
     */
    @ParameterType("admin|manager|user|guest")
    public UserRole role(String value) {
        return UserRole.valueOf(value.toUpperCase());
    }

    /**
     * Transforms "enabled" or "disabled" into boolean.
     *
     * @param value "enabled" or "disabled"
     * @return true for enabled, false for disabled
     */
    @ParameterType("enabled|disabled")
    public Boolean featureState(String value) {
        return "enabled".equals(value);
    }

    /**
     * Transforms ordinal strings into integers.
     * Supports: 1st, 2nd, 3rd, 4th, etc.
     *
     * @param value the ordinal string
     * @return the integer value
     */
    @ParameterType("\\d+(?:st|nd|rd|th)")
    public Integer ordinal(String value) {
        return Integer.parseInt(value.replaceAll("\\D", ""));
    }
}
