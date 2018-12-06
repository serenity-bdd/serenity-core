package net.serenitybdd.demos.todos.pages;

import net.serenitybdd.core.annotations.findby.By;
import net.serenitybdd.core.pages.PageObject;
import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.demos.todos.model.TodoStatus;
import net.serenitybdd.demos.todos.model.TodoStatusFilter;
import net.thucydides.core.annotations.DefaultUrl;
import org.openqa.selenium.Keys;

import java.util.List;
import java.util.stream.Collectors;

@DefaultUrl("http://todomvc.com/examples/angularjs/#/")
public class TodoPage extends PageObject {

    public static final String ACTION_ROW = "//div[@class='view' and contains(.,'%s')]";
    public static final String ACTION_ROW_LABEL = "//label[contains(.,'%s')]";
    public static final String COMPLETE_TICKBOX = ".//input[@ng-model='todo.completed']";

    public void addAnActionCalled(String actionName) {
        $(".new-todo").type(actionName)
                      .then().sendKeys(Keys.ENTER);
    }

    public List<String> getActions() {
        return findAll(".view").stream()
                               .map(WebElementFacade::getText)
                               .map(String::trim)
                               .collect(Collectors.toList());
    }

    public void filterByStatus(TodoStatusFilter status) {
        findBy("#filters")
                .then().findBy(statusFilterLinkFor(status))
                .then().click();
    }

    private String statusFilterLinkFor(TodoStatusFilter status) {
        return String.format(".//a[.='%s']", status.name());
    }

    public void markComplete(String action) {
        inActionRowFor(action).findBy(COMPLETE_TICKBOX).click();
    }

    private WebElementFacade inActionRowFor(String action) {
        return $(String.format(ACTION_ROW, action));
    }

    private WebElementFacade inActionRowLabelFor(String action) {
        return $(String.format(ACTION_ROW_LABEL, action));
    }

    public TodoStatus getStatusFor(String action) {
        WebElementFacade actionRow = inActionRowFor(action);
        return isShownAsCompleted(actionRow) ? TodoStatus.Completed : TodoStatus.Active;
    }

    private boolean isShownAsCompleted(WebElementFacade actionRow) {
        return actionRow.find(By.tagName("label")).getCssValue("text-decoration").equals("line-through");
    }

    public void updateAction(String currentActionName, String newActionName) {
        evaluateJavascript("arguments[0].click()", inActionRowLabelFor(currentActionName));
        inActionRowLabelFor(currentActionName).type(newActionName);
        inActionRowLabelFor(currentActionName).sendKeys(Keys.RETURN);
    }

    public void clearCompletedActions() {
        $(".clear-completed").click();
    }
}
