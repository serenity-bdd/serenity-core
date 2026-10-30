package todo;

import com.microsoft.playwright.Page;
import net.serenitybdd.annotations.Step;
import net.serenitybdd.playwright.PlaywrightSerenity;

import java.util.List;

public class TodoSteps {

    private TodoPage todoPage() {
        Page page = PlaywrightSerenity.getCurrentPage();   // the page registered by the test
        return new TodoPage(page);
    }

    @Step("Open the todo application")
    public void openTheApplication() {
        todoPage().open();
    }

    @Step("Add the todo '{0}'")
    public void addTodo(String item) {
        todoPage().addTodo(item);
    }

    public List<String> todoItems() {
        return todoPage().todoItems();
    }
}
