package todo;

import com.microsoft.playwright.Page;

import java.util.List;

/** A plain class that wraps a Playwright Page. It does not extend Serenity's PageObject. */
public class TodoPage {

    private final Page page;

    public TodoPage(Page page) {
        this.page = page;
    }

    public void open() {
        page.navigate("https://todo-app.serenity-js.org");
    }

    public void addTodo(String item) {
        page.locator(".new-todo").fill(item);
        page.locator(".new-todo").press("Enter");
    }

    public List<String> todoItems() {
        return page.locator(".todo-list li label").allInnerTexts();
    }
}
