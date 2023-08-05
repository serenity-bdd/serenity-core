package net.thucydides.model.requirements.model;

import java.util.Optional;

public class Example {
    private final String description;
    private final Optional<String> cardNumber;

    public Example(String description, Optional<String> cardNumber) {
        this.description = description;
        this.cardNumber = cardNumber;
    }

    public String getDescription() {
        return description;
    }

    public Optional<String> getCardNumber() {
        return cardNumber;
    }

    @Override
    public String toString() {
        if (cardNumber.isPresent()) {
            return description + " [" + cardNumber.get() + "]";
        } else {
            return description;
        }
    }

    public static ExampleBuilder withDescription(String description) {
        return new ExampleBuilder(description);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Example)) return false;

        Example example = (Example) o;

        if (cardNumber != null ? !cardNumber.equals(example.cardNumber) : example.cardNumber != null) return false;
        if (description != null ? !description.equals(example.description) : example.description != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = description != null ? description.hashCode() : 0;
        result = 31 * result + (cardNumber != null ? cardNumber.hashCode() : 0);
        return result;
    }
}
