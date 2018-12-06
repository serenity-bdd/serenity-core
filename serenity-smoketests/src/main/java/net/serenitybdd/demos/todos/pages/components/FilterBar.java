package net.serenitybdd.demos.todos.pages.components;

import net.serenitybdd.screenplay.targets.Target;

public class FilterBar {
    public static final Target CLEAR_COMPLETED = Target.the("Clear completed button").locatedBy(".clear-completed");

    public static Target filterCalled(String name) {
        String FILTER_BUTTON = "//a[.='%s']";
        return Target.the(name + " filter").locatedBy(String.format(FILTER_BUTTON, name));
    }
}
