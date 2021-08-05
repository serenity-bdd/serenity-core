package net.serenitybdd.demos.todos.screenplay.questions;

import net.serenitybdd.demos.todos.screenplay.user_interface.TodoList;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.questions.Text;

import java.util.List;

/**
 * A factory class used to provide different questions about the items displayed in the todo list
 */
public class TheItems {
    public static Question<List<String>> displayed() {
        return Text.of(TodoList.ITEMS)
                .describedAs("the items displayed")
                .asAList();
    }

    public static Question<Integer> leftCount() {
        return Text.of(TodoList.ITEMS_LEFT)
                   .describedAs("the number of items left")
                   .asInteger();
    }
}
