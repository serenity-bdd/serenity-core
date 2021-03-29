package serenityscreenplaywebdriver.net.serenitybdd.screenplay.actions;

import serenityscreenplay.net.serenitybdd.screenplay.Actor;
import serenityscreenplaywebdriver.net.serenitybdd.screenplay.targets.Target;

public class ScrollToTarget extends ScrollTo {
   private final Target target;

   public ScrollToTarget(Target target) {
       this.target = target;
   }

   public <T extends Actor> void performAs(T actor) {
       performScrollTo(actor, target.resolveFor(actor));
   }
}