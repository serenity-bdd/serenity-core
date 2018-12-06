package net.serenitybdd.demos.todos.pages;

import net.serenitybdd.core.pages.PageObject;
import net.thucydides.core.annotations.DefaultUrl;

import java.util.concurrent.TimeUnit;

@DefaultUrl("http://todomvc.com/examples/angularjs/#/")
public class ApplicationHomePage extends PageObject {
    public void waitForTheApplicationToLoad() {
        withTimeoutOf(60, TimeUnit.SECONDS).waitFor(".new-todo");
    }

    public void openApplication() {
        open();
        waitForTheApplicationToLoad();
    }

}
