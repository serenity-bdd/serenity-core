package todo;

import net.serenitybdd.screenplay.targets.Target;

public class TodoList {
    public static final Target NEW_TODO = Target.the("new todo field").locatedBy(".new-todo");
    public static final Target ITEMS = Target.the("todo items").locatedBy(".todo-list li label");
}
