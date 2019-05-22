package net.serenitybdd.screenplay.shopping.questions;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.shopping.tasks.Checkout;

public class NextPersonToBeServed implements Question<String> {

  private Checkout checkout;

  public NextPersonToBeServed(Checkout checkout) {
    this.checkout = checkout;
  }

  public static NextPersonToBeServed nextPersonToBeServed() {
    return new NextPersonToBeServed(null);
  }

  @Override
  public String answeredBy(Actor actor) {
    return checkout.nextCustomer();
  }

  public NextPersonToBeServed by(Checkout fastCheckout) {
    return new NextPersonToBeServed(fastCheckout);
  }
}
