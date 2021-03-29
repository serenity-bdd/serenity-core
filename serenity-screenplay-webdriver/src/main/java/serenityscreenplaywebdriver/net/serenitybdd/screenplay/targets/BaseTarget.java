package serenityscreenplaywebdriver.net.serenitybdd.screenplay.targets;

import serenitycore.net.serenitybdd.core.pages.WebElementFacade;
import serenityscreenplay.net.serenitybdd.screenplay.Actor;

import java.util.List;

public abstract class BaseTarget {

    protected final String targetElementName;

    public BaseTarget(String targetElementName) {
        this.targetElementName = targetElementName;
    }

    @Override
    public String toString() {
        return targetElementName;
    }

    public static TargetBuilder the(String targetElementName) {
        return new TargetBuilder(targetElementName);
    }

    public abstract WebElementFacade resolveFor(Actor actor);

    public abstract List<WebElementFacade> resolveAllFor(Actor actor);

    public abstract BaseTarget called(String name);
}
