package net.serenitybdd.demos.todos.tasks;

import net.serenitybdd.demos.todos.pages.components.ToDoList;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.tasks.Click;

import static net.serenitybdd.screenplay.Tasks.instrumented;

public class CompleteItem implements Performable {

    private final String itemName;

    public CompleteItem(String itemName) {
        this.itemName = itemName;
    }

    public static CompleteItem called(String itemName) {
        return instrumented(CompleteItem.class, itemName);
    }

    @Override
    public <T extends Actor> void performAs(T theActor) {
        theActor.attemptsTo(Click.on(ToDoList.completeButtonFor(itemName)));
    }
}
