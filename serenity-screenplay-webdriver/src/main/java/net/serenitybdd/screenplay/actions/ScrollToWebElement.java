package net.serenitybdd.screenplay.actions;

import net.serenitybdd.screenplay.Actor;
import org.openqa.selenium.WebElement;

public class ScrollToWebElement extends ScrollTo{
   private final WebElement element;

   public ScrollToWebElement(WebElement element) {
       this.element = element;
   }

   public <T extends Actor> void performAs(T actor) {
       performScrollTo(actor, element);
   }
}