package net.serenitybdd.demos.todos.screenplay.user_interface;

import net.serenitybdd.screenplay.targets.Target;

public class TodoListApp {
    public final static Target MAIN_HEADING = Target.the("main heading").locatedBy("css:h1");
    public final static Target FOOTER       = Target.the("footer").locatedBy(".info");
}
