package net.thucydides.core.requirements.model;

import com.google.common.base.Optional;

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


}
