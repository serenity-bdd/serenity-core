package net.serenitybdd.screenplay.actions;

import net.serenitybdd.markers.CanBeSilent;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import org.openqa.selenium.devtools.DevTools;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Perform an action directly using the DevTools API
 * For example:
 * <pre>
 *     <code>
 *         actor.attemptsTo(
 *             WithDevTools.perform(
 *                 devTools -> devTools.createSession()
 *             )
 *         );
 *     </code>
 * </pre>
 */
public class WithDevTools implements Performable, CanBeSilent {

    private final Consumer<DevTools> action;
    private boolean isSilent = false;

    public WithDevTools(Consumer<DevTools> action) {
        this.action = action;
    }

    public static WithDevTools perform(Consumer<DevTools> action) {
        return new WithDevTools(action);
    }


    public static Question<Object> ask(Function<DevTools, Object> question) {
        return actor -> question.apply(BrowseTheWeb.as(actor).getDevTools());
    }

    ;

    @Override
    public <T extends Actor> void performAs(T actor) {
        action.accept(BrowseTheWeb.as(actor).getDevTools());
    }

    @Override
    public boolean isSilent() {
        return isSilent;
    }

    public Performable withNoReporting() {
        this.isSilent = true;
        return this;
    }
}
