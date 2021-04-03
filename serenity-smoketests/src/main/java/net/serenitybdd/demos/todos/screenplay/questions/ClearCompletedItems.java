package net.serenitybdd.demos.todos.screenplay.questions;

import net.serenitybdd.demos.todos.screenplay.user_interface.TodoList;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.annotations.Subject;
import net.serenitybdd.screenplay.questions.Visibility;

import static net.serenitybdd.screenplay.questions.ValueOf.the;

@Subject("the 'Clear Completed' option")
public class ClearCompletedItems implements Question<ElementAvailability> {
    @Override
    public ElementAvailability answeredBy(Actor actor) {
        return ElementAvailability.from(
                TodoList.CLEAR_COMPLETED.resolveFor(actor).isCurrentlyVisible()
        );
    }
    public static ClearCompletedItems option() {
        return new ClearCompletedItems();
    }
}