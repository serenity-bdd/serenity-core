package net.serenitybdd.screenplay.actions;

import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import org.openqa.selenium.devtools.DevTools;

import java.util.function.Function;

public class DevToolsQuery {

    public static DevToolsQuery ask() {
        return new DevToolsQuery();
    }
    public <T> Question<T> about(Function<DevTools, Object> question) {
        return actor -> (T) question.apply(BrowseTheWeb.as(actor).getDevTools());
    }
}
