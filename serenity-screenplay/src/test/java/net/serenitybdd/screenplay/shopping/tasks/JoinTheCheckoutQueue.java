package net.serenitybdd.screenplay.shopping.tasks;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Performable;

import static net.serenitybdd.screenplay.Tasks.instrumented;

public class JoinTheCheckoutQueue implements Performable {

  public static JoinTheCheckoutQueue joinTheCheckoutQueue() {
    return instrumented(JoinTheCheckoutQueue.class);
  }

  public JoinTheCheckoutQueue of(Checkout c) {
    return instrumented(JoinTheCheckoutQueue.class);
  }

  @Override
  public <T extends Actor> void performAs(T actor) {

  }
}
