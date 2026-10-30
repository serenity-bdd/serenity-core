package todo;

import net.serenitybdd.annotations.Managed;
import net.serenitybdd.annotations.Steps;
import net.serenitybdd.junit5.SerenityJUnit5Extension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.WebDriver;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SerenityJUnit5Extension.class)
class AddTodoTest {

    @Managed(driver = "chrome")
    WebDriver driver;

    @Steps
    TodoSteps todo;

    @Test
    void shouldShowTheNewTodo() {
        todo.openTheApplication();
        todo.addTodo("Buy milk");
        assertThat(todo.todoItems()).contains("Buy milk");
    }
}
