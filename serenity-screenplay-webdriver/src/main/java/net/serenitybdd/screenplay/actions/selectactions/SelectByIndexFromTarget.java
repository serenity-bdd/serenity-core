package net.serenitybdd.screenplay.actions.selectactions;

import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Interaction;
import net.serenitybdd.screenplay.targets.Target;
import net.serenitybdd.annotations.Step;

public class SelectByIndexFromTarget implements Interaction {
    private Target target;
    private Integer[] indexes;

    public SelectByIndexFromTarget() {}

    public SelectByIndexFromTarget(Target target, Integer... indexes) {
        this.target = target;
        this.indexes = indexes;
    }

    @Step("{0} selects index #index in #target")
    public <T extends Actor> void performAs(T theUser) {
        WebElementFacade dropdown = target.resolveFor(theUser);
        for(Integer index : indexes) {
            dropdown.selectByIndex(index);
        }
    }


}
