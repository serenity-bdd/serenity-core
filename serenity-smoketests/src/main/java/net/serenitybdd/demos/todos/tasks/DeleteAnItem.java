package net.serenitybdd.demos.todos.tasks;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Performable;
import net.thucydides.core.annotations.Step;
import org.openqa.selenium.WebElement;

import static net.serenitybdd.demos.todos.pages.components.ToDoList.deleteButtonForItem;
import static net.serenitybdd.screenplay.Tasks.instrumented;
import static net.serenitybdd.screenplay.abilities.BrowseTheWeb.as;

public class DeleteAnItem implements Performable {

    private final String itemName;

    public DeleteAnItem(String itemName) {
        this.itemName = itemName;
    }

    @Step("{0} deletes the item '#itemName'")
    public <T extends Actor> void performAs(T theActor) {
        WebElement deleteButton = deleteButtonForItem(itemName).resolveFor(theActor);
        as(theActor).evaluateJavascript("arguments[0].click()", deleteButton);
    }

    public static DeleteAnItem called(String itemName) {
        return instrumented(DeleteAnItem.class, itemName);
    }
}
