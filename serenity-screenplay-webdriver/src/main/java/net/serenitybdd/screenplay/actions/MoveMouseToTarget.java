package net.serenitybdd.screenplay.actions;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.targets.Target;

public class MoveMouseToTarget extends MoveMouseTo {
   private final Target target;

   public MoveMouseToTarget(Target target) {
       this.target = target;
   }

   public <T extends Actor> void performAs(T actor) {
       performMouseMoveAs(actor, target.resolveFor(actor));
   }
}