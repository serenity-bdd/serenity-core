package net.serenitybdd.screenplay.actions;

import net.serenitybdd.screenplay.Performable;
import org.openqa.selenium.interactions.Actions;

import java.util.function.Consumer;

public interface WithChainableActions extends Performable {
    WithChainableActions andThen(Consumer<Actions> nextActions);
}
