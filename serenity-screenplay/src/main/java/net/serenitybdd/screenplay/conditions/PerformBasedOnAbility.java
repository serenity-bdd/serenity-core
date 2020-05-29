package net.serenitybdd.screenplay.conditions;

import net.serenitybdd.markers.IsSilent;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.NoMatchingAbilityException;
import net.serenitybdd.screenplay.Performable;

import java.util.Arrays;

import static net.serenitybdd.screenplay.Tasks.instrumented;

public class PerformBasedOnAbility implements Performable, IsSilent {

  private final PossibleAction[] possibleActions;

  public PerformBasedOnAbility(PossibleAction... possibleActions) {

    this.possibleActions = possibleActions;
  }

  public static PerformBasedOnAbility checkInOrder(PossibleAction... possibleActions) {
    return instrumented(PerformBasedOnAbility.class, (Object) possibleActions);
  }

  @Override
  public <T extends Actor> void performAs(T actor) {
    Performable[] firstTasksMatchingAbility = Arrays.stream(possibleActions)
        .filter(task -> actor.abilityTo(task.getAbility()) != null)
        .findFirst().orElseThrow(() -> new NoMatchingAbilityException(actor.getName()))
        .getTasks();
    actor.attemptsTo(firstTasksMatchingAbility);
  }
}
