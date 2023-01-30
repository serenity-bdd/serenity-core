package swaglabs.actions.errors;

import net.serenitybdd.screenplay.targets.Target;
import net.serenitybdd.screenplay.ui.PageElement;

public class ErrorMessages {
    public static Target CURRENTLY_VISIBLE = PageElement.withCSSClass("error-message-container");
}