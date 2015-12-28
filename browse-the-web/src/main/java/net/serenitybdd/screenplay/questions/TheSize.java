package net.serenitybdd.screenplay.questions;

import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.Dimension;

import java.util.List;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;

public class TheSize extends TargetedUIState<Dimension> {

    public TheSize(Target target, Actor actor) {
        super(target,actor);
    }

    public static UIStateReaderBuilder<TheSize> of(Target target) {
        return new UIStateReaderBuilder(target, TheSize.class);
    }

    public Dimension resolve() {
        return target.resolveFor(actor).getSize();
    }

    public List<Dimension> resolveAll() {
        return extract(target.resolveAllFor(actor), on(WebElementFacade.class).getSize());
    }
}
