package net.serenitybdd.screenplay.actions;

import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.By;

/**
 * Selenium drag-and-drop interaction, e.g.
 * actor.attemptsTo(Drag.from("#draggable").to("#droppable"));
 */
public class Drag {

    public static DragInteraction from(Target from) {
        return new DragInteraction(from);
    }

    public static DragInteraction from(By byLocator) {
        Target from = Target.the(byLocator.toString()).located(byLocator);
        return new DragInteraction(from);
    }

    public static DragInteraction from(String xpathOrCssLocator) {
        Target from = Target.the(xpathOrCssLocator).locatedBy(xpathOrCssLocator);
        return new DragInteraction(from);
    }
}
