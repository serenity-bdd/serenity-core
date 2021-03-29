package serenityscreenplaywebdriver.net.serenitybdd.screenplay.actions;

import serenityscreenplay.net.serenitybdd.screenplay.Actor;
import serenityscreenplaywebdriver.net.serenitybdd.screenplay.targets.Target;

public class MoveMouseToTarget extends MoveMouseTo {
   private final Target target;

   public MoveMouseToTarget(Target target) {
       this.target = target;
   }

   public <T extends Actor> void performAs(T actor) {
       performMouseMoveAs(actor, target.resolveFor(actor));
   }
}