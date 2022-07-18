package net.serenitybdd.screenplay.actions;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.targets.Target;

public class MoveMouseToTarget extends ChainablePerformable {
   private final Target target;

   public MoveMouseToTarget(Target target) {
       this.target = target;
   }

   public <T extends Actor> void performAs(T actor) {
       addActionAtStart(actions -> actions.moveToElement(target.resolveFor(actor)));
       performSubsequentActionsAs(actor);
   }
}
