package net.serenitybdd.core.webdriver.integration;

import com.paulhammant.ngwebdriver.ByAngular;
import net.serenitybdd.core.pages.PageObject;
import net.thucydides.core.annotations.DefaultUrl;
import net.thucydides.core.webdriver.WebDriverFacade;
import net.thucydides.core.webdriver.WebDriverFactory;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.List;
import java.util.stream.Collectors;

public class WhenBrowsingAnAngularJSApp {

    WebDriver driver;

    @DefaultUrl("http://todomvc.com/examples/angularjs/#/")
    static class TodoApp extends PageObject {

        public void addTodoItem(String todo) {
            find(ByAngular.model("newTodo")).sendKeys(todo, Keys.ENTER);
        }

        public List<String> visibleTodoItems() {
            return findAll(ByAngular.repeater("todo in todos"))
                    .stream()
                    .map(WebElement::getText)
                    .collect(Collectors.toList());
        }

    }

    TodoApp todoApp;

    @Before
    public void openDriver() {
        driver = new WebDriverFacade(ChromeDriver.class, new WebDriverFactory());
        todoApp = new TodoApp().withDriver(driver);
    }

    @Test
    public void shouldWaitForAngularToFinish() {

        todoApp.open();

        todoApp.addTodoItem("Walk the dog");

        todoApp.waitForAngularRequestsToFinish();

        Assertions.assertThat(todoApp.visibleTodoItems()).contains("Walk the dog");
    }

}
