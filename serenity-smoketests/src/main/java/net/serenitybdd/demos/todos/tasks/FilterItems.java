package net.serenitybdd.demos.todos.tasks;

import net.serenitybdd.demos.todos.model.TodoStatusFilter;
import net.serenitybdd.demos.todos.pages.components.FilterBar;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.actions.Click;
import net.thucydides.core.annotations.Step;

import static net.serenitybdd.screenplay.Tasks.instrumented;

public class FilterItems implements Performable {

    final TodoStatusFilter filter;

    public FilterItems(TodoStatusFilter filter) {
        this.filter = filter;
    }

    @Step("{0} filters items by status #status")
    public <T extends Actor> void performAs(T theActor) {
        theActor.attemptsTo(Click.on(FilterBar.filterCalled(filter.name())));
    }

    public static FilterItems byStatus(TodoStatusFilter status) {
        return instrumented(FilterItems.class, status);
    }
}
