package net.serenitybdd.demos.todos.pages.components;

import net.serenitybdd.screenplay.targets.Target;

public class ToDoList {
    public static Target NEW_TODO_FIELD = Target.the("New Todo Field").locatedBy("#new-todo");
    public static String TODO_ITEMS = ".view label";

    public static Target deleteButtonForItem(String itemName) {
        String DELETE_BUTTON = "//*[@class='view' and contains(.,'%s')]//button[@class='destroy']";
        return Target.the("Delete button").locatedBy(String.format(DELETE_BUTTON, itemName));
    }

    public static Target completeButtonFor(String itemName) {
        String COMPLETE_BUTTON = "//*[@class='view' and contains(.,'%s')]//input[@type='checkbox']";
        return Target.the("Complete button").locatedBy(String.format(COMPLETE_BUTTON, itemName));
    }

}
