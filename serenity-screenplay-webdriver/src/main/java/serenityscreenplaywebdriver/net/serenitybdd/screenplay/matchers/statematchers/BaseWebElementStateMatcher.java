package serenityscreenplaywebdriver.net.serenitybdd.screenplay.matchers.statematchers;

import serenitycore.net.serenitybdd.core.pages.WebElementState;
import org.hamcrest.TypeSafeMatcher;

public abstract class BaseWebElementStateMatcher<T extends WebElementState> extends TypeSafeMatcher<T> {

    protected WebElementState existing(WebElementState element) {
        return element;
    }
}