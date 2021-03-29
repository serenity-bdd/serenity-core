package serenityscreenplaywebdriver.net.serenitybdd.screenplay.actions;

import serenityscreenplay.net.serenitybdd.screenplay.Performable;
import org.openqa.selenium.interactions.Actions;

import java.util.function.Consumer;

public interface WithChainableActions extends Performable {
    WithChainableActions andThen(Consumer<Actions> nextActions);
}
