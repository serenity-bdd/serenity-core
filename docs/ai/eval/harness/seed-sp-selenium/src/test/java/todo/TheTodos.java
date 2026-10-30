package todo;

import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.questions.Text;

import java.util.Collection;

public class TheTodos {
    // Text.ofEach(..) is a Question<Collection<String>> - not List<String>
    public static Question<Collection<String>> displayed() {
        return Text.ofEach(TodoList.ITEMS);
    }
}
