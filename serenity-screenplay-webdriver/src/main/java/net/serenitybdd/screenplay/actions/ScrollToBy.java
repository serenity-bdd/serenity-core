package net.serenitybdd.screenplay.actions;

import com.google.common.collect.ImmutableList;
import net.serenitybdd.screenplay.Actor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

public class ScrollToBy extends ScrollTo{
    private final List<By> locators;

   public ScrollToBy(By... locators) {
       this.locators = ImmutableList.copyOf(locators);
   }

   public <T extends Actor> void performAs(T actor) {
       WebElement element = WebElementLocator.forLocators(locators).andActor(actor);
       performScrollTo(actor, element);
   }
}