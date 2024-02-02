package net.serenitybdd.demos.todos.pageobjects.pages;

import net.serenitybdd.core.annotations.findby.By;
import net.serenitybdd.core.pages.PageObject;
import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.demos.todos.pageobjects.model.TodoStatus;
import net.serenitybdd.demos.todos.pageobjects.model.TodoStatusFilter;
import net.serenitybdd.annotations.DefaultUrl;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.FindBys;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@DefaultUrl("https://todo-app.serenity-js.org/#/")
public class TodoListPage extends PageObject {

    // -----------------------------------------------------------------------------------------------------------------
    // SELECTORS

    private static final String MAIN_HEADING = "css:h1";
    private static final String FOOTER = ".info";
    private static final String NEW_TODO_INPUT_FIELD = ".new-todo";
    private static final String ITEM_ROW = "//div[@class='view' and contains(.,'%s')]";
    private static final String ITEM_ROW_LABEL = "//label[contains(.,'%s')]";
    private static final String COMPLETE_TICKBOX = ".//input[@ng-model='todo.completed']";
    private static final String DELETE_BUTTON = "//button[@class='destroy']";
    private static final String FILTERS = ".filters";
    private static final String SELECTED_FILTER = ".filters li .selected";
    private static final String ITEMS_LEFT_COUNT = ".todo-count strong";
    private static final String TOGGLE_ALL = "#toggle-all";
    private static final String CLEAR_COMPLETED = ".clear-completed";

    // -----------------------------------------------------------------------------------------------------------------
    // ACTIONS

    @FindBys({@FindBy(tagName = "a")})
    List<WebElement> testFindBys;

    @FindBy(css = "#todo-count")
    public WebElement itemsLeft;

    public void openApplication() {
        open();
        waitForTheApplicationToLoad();
    }

    private void waitForTheApplicationToLoad() {
        withTimeoutOf(60, TimeUnit.SECONDS).waitFor(NEW_TODO_INPUT_FIELD);
    }

    public void addATodoItemCalled(String itemName) {
        $(NEW_TODO_INPUT_FIELD).type(itemName)
                .then().sendKeys(Keys.ENTER);
    }

    public void filterByStatus(TodoStatusFilter status) {
        findBy(FILTERS)
                .then().findBy(statusFilterLinkFor(status))
                .then().click();
    }

    private String statusFilterLinkFor(TodoStatusFilter status) {
        return String.format(".//a[.='%s']", status.name());
    }

    public void markAsComplete(String todoItem) {
        inItemRowFor(todoItem).findBy(COMPLETE_TICKBOX).click();
    }

    public void delete(String todoItem) {
        evaluateJavascript("arguments[0].click()", inItemRowLabelFor(todoItem).findBy(DELETE_BUTTON));
    }

    private WebElementFacade inItemRowFor(String todoItem) {
        return $(String.format(ITEM_ROW, todoItem));
    }

    private WebElementFacade inItemRowLabelFor(String todoItem) {
        return $(String.format(ITEM_ROW_LABEL, todoItem));
    }

    private boolean isShownAsCompleted(WebElementFacade itemRow) {
        return itemRow.find(By.tagName("label")).getCssValue("text-decoration").contains("line-through");
    }

    public void updateItem(String currentItemName, String newItemName) {
        evaluateJavascript("arguments[0].click()", inItemRowLabelFor(currentItemName));
        inItemRowLabelFor(currentItemName).type(newItemName);
        inItemRowLabelFor(currentItemName).sendKeys(Keys.RETURN);
    }

    public void clearCompletedItems() {
        $(CLEAR_COMPLETED).click();
    }

    public void toggleAll() {
        evaluateJavascript("arguments[0].click();",$("#toggle-all"), $(TOGGLE_ALL));
    }


    // -----------------------------------------------------------------------------------------------------------------
    // GETTERS

    public String heading() {
        return $(MAIN_HEADING).getText();
    }

    public String placeholderText() {
        return $(NEW_TODO_INPUT_FIELD).getAttribute("placeholder");
    }

    public int numberOfItemsLeft() {
        return Integer.valueOf($(ITEMS_LEFT_COUNT).getText());
    }

    public boolean canClearCompletedItems() {
        return $(CLEAR_COMPLETED).isCurrentlyVisible();
    }

    public TodoStatus statusOf(String todoItem) {
        WebElementFacade itemRow = inItemRowFor(todoItem);
        return isShownAsCompleted(itemRow) ? TodoStatus.Completed : TodoStatus.Active;
    }

    public TodoStatusFilter currentlySelectedFilter() {
        return TodoStatusFilter.valueOf(findBy(SELECTED_FILTER).getText());
    }

    public List<String> displayedItems() {
        return findAll(".view").stream()
                .map(WebElementFacade::getText)
                .collect(Collectors.toList());
    }

    public String footer() {
        return $(FOOTER).getText();
    }
}
