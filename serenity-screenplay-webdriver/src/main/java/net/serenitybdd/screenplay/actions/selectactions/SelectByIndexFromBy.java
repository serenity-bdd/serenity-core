package net.serenitybdd.screenplay.actions.selectactions;

import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.screenplay.actions.ByAction;
import org.openqa.selenium.By;

import java.util.Collections;
import java.util.List;

public class SelectByIndexFromBy extends ByAction {
    private List<Integer> indexes;

    public SelectByIndexFromBy() {}

    public SelectByIndexFromBy(Integer index, By... locators) {
        super(locators);
        this.indexes = Collections.singletonList(index);
    }

    public SelectByIndexFromBy(List<Integer> indexes, By... locators) {
        super(locators);
        this.indexes = indexes;
    }

    @Step("{0} selects index #index")
    public <T extends Actor> void performAs(T theUser) {
        indexes.forEach(
            index -> BrowseTheWeb.as(theUser).find(locators).selectByIndex(index)
        );
    }
}
