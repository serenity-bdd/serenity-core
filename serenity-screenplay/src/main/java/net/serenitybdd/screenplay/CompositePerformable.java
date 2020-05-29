package net.serenitybdd.screenplay;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CompositePerformable implements Performable {

    private final List<Performable> todoList;

    private CompositePerformable(List<Performable> todoList) {
        this.todoList = todoList;
    }

    public static Performable from(Performable firstPerformable, Performable nextPerformable) {
        List<Performable> todoList = new ArrayList<>();
        todoList.addAll(flattened(firstPerformable));
        todoList.addAll(flattened(nextPerformable));
        return new CompositePerformable(todoList);
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        todoList.forEach(actor::attemptsTo);
    }

    private static List<Performable> flattened(Performable performable) {
        if (performable instanceof CompositePerformable) {
            return ((CompositePerformable) performable).todoList;
        } else {
            return Collections.singletonList(performable);
        }
    }
}
