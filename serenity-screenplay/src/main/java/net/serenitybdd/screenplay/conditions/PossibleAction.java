package net.serenitybdd.screenplay.conditions;

import net.serenitybdd.screenplay.Ability;
import net.serenitybdd.screenplay.Performable;

public class PossibleAction {

  private final Class<? extends Ability> ability;
  private final Performable[] tasks;

  public Class<? extends Ability> getAbility() {
    return ability;
  }

  public Performable[] getTasks() {
    return tasks;
  }

  public <T extends Ability> PossibleAction(Class<T> ability, Performable... tasks) {
    this.ability = ability;
    this.tasks = tasks;
  }

  public PossibleAction theyAttemptTo(Performable... tasks) {
    return new PossibleAction(this.ability, tasks);
  }

  public static <T extends Ability> PossibleAction givenTheyCan(Class<T> ability) {
    return new PossibleAction(ability);
  }

}

