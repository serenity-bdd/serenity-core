package net.serenitybdd.demos.todos.screenplay.model;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public enum TodoStatus {
    Active(FALSE), Completed(TRUE);

    private final Boolean whenChecked;

    TodoStatus(Boolean whenChecked) {
        this.whenChecked = whenChecked;
    }

    public static TodoStatus from(Boolean itemChecked) {
        for (TodoStatus todoStatus : values()) {
            if(todoStatus.whenChecked == itemChecked) { return todoStatus; }
        }
        throw new IllegalArgumentException("Illegal value " + itemChecked);
    }
}
