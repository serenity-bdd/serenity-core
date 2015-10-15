package net.serenitybdd.demos.todos.tasks;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.targets.Target;
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
        WebElement deleteButton = as(theActor).findBy(pathTo(deleteButtonForItem(itemName)));
        as(theActor).evaluateJavascript("arguments[0].click()", deleteButton);
    }

    private String pathTo(Target target) {
        return target.getCssOrXPathSelector();
    }

    public static DeleteAnItem called(String itemName) {
        return instrumented(DeleteAnItem.class, itemName);
    }
}
