package todo;

import net.serenitybdd.annotations.DefaultUrl;
import net.serenitybdd.core.pages.PageObject;
import net.serenitybdd.core.pages.WebElementFacade;
import org.openqa.selenium.support.FindBy;

import java.util.List;

// Do not declare your own open(): PageObject already has open() and it uses @DefaultUrl.
@DefaultUrl("https://todo-app.serenity-js.org")
public class TodoPage extends PageObject {

    @FindBy(css = ".new-todo")
    WebElementFacade newTodoField;

    public void addTodo(String item) {
        newTodoField.typeAndEnter(item);
    }

    public List<String> todoItems() {
        return $$(".todo-list li label").stream().map(WebElementFacade::getText).toList();
    }
}
