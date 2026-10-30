package todo;

import net.serenitybdd.annotations.Step;
import net.serenitybdd.core.steps.UIInteractionSteps;

import java.util.List;

public class TodoSteps extends UIInteractionSteps {

    TodoPage todoPage;   // page objects are injected into step libraries

    @Step("Open the todo application")
    public void openTheApplication() {
        todoPage.open();
    }

    @Step("Add the todo '{0}'")
    public void addTodo(String item) {
        todoPage.addTodo(item);
    }

    public List<String> todoItems() {
        return todoPage.todoItems();
    }
}
