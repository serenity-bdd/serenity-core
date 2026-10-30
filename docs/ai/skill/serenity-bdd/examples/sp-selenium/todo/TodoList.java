package todo;

import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.By;

public class TodoList {
    // locatedBy(String) treats a string as XPath whenever it compiles as one, so a CSS selector such as
    // "input.new-todo" (a valid XPath name) is NOT found. Use located(By.cssSelector(..)) for those.
    public static final Target NEW_TODO = Target.the("new todo field").located(By.cssSelector("input.new-todo"));
    public static final Target ITEMS = Target.the("todo items").locatedBy(".todo-list li label");
}
