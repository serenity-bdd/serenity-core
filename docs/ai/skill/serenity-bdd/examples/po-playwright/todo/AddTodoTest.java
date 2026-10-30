package todo;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import net.serenitybdd.annotations.Steps;
import net.serenitybdd.junit5.SerenityJUnit5Extension;
import net.serenitybdd.playwright.PlaywrightSerenity;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SerenityJUnit5Extension.class)
class AddTodoTest {

    @Steps
    TodoSteps todo;

    static Playwright playwright;
    static Browser browser;
    Page page;

    @BeforeAll
    static void startBrowser() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
    }

    @AfterAll
    static void stopBrowser() {
        browser.close();
        playwright.close();
    }

    @BeforeEach
    void openPage() {
        page = browser.newPage();
        PlaywrightSerenity.registerPage(page);   // lets Serenity report screenshots and steps
    }

    @AfterEach
    void closePage() {
        PlaywrightSerenity.unregisterPage(page);
        page.close();
    }

    @Test
    void shouldShowTheNewTodo() {
        todo.openTheApplication();
        todo.addTodo("Buy milk");
        assertThat(todo.todoItems()).contains("Buy milk");
    }
}
