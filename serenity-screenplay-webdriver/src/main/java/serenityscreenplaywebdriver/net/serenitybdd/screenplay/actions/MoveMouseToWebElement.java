package serenityscreenplaywebdriver.net.serenitybdd.screenplay.actions;

import serenityscreenplay.net.serenitybdd.screenplay.Actor;
import org.openqa.selenium.WebElement;

public class MoveMouseToWebElement extends MoveMouseTo{
   private final WebElement element;

   public MoveMouseToWebElement(WebElement element) {
       this.element = element;
   }

   public <T extends Actor> void performAs(T actor) {
       performMouseMoveAs(actor, element);
   }
}