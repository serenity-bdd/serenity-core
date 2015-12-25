package net.serenitybdd.screenplay.actions;

import net.serenitybdd.core.pages.PageObject;
import net.serenitybdd.screenplay.Action;

/**
 * A convenience class to make it easier to use the Serenity WebDriver API in Action classes.
 */
public abstract class WebAction extends PageObject implements Action {
    public WebAction() {}
}
