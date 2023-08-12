package net.thucydides.model.requirements.model;

import java.util.Optional;

public class ExampleBuilder {

    private final static Optional<String> NO_CARD_NUMBER = Optional.empty();

    private final String description;

    public ExampleBuilder(String description) {
        this.description = description;
    }

    public Example andCardNumber(String cardNumber) {
        return new Example(description, Optional.of(cardNumber));
    }

    public Example andNoCardNumber() {
        return new Example(description, NO_CARD_NUMBER);
    }
}
