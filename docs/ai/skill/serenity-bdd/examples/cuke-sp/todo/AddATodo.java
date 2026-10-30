package todo;

import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.actions.Enter;
import org.openqa.selenium.Keys;

public class AddATodo {
    public static Performable called(String item) {
        return Task.where("{0} adds a todo called " + item,
                Enter.theValue(item).into(TodoList.NEW_TODO).thenHit(Keys.ENTER));
    }
}
