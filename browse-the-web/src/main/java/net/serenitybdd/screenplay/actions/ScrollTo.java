package net.serenitybdd.screenplay.actions;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import org.openqa.selenium.WebElement;

public abstract class ScrollTo implements Performable {

    protected Boolean alignToTop;

    public ScrollTo andAlignToTop() {
        this.alignToTop = true;
        return this;
    }

    public ScrollTo andAlignToBottom() {
        this.alignToTop = false;
        return this;
    }

    protected void performScrollTo(Actor actor, WebElement element) {
        BrowseTheWeb.as(actor).evaluateJavascript("arguments[0].scrollIntoView(arguments[1]);", element, alignToTop);

    }
}