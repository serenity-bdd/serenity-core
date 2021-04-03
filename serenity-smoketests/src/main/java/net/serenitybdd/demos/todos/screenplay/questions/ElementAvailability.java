package net.serenitybdd.demos.todos.screenplay.questions;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public enum ElementAvailability {
    Available(TRUE), Unavailable(FALSE);

    private final Boolean isAvailable;

    ElementAvailability(Boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    public static ElementAvailability from(Boolean visibility) {
        for (ElementAvailability elementAvailability : values()) {
            if(visibility == elementAvailability.isAvailable) { return elementAvailability; }
        }
        throw new IllegalArgumentException("Unknown value " + visibility);
    }
}