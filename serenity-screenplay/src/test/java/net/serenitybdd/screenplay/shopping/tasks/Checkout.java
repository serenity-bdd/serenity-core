package net.serenitybdd.screenplay.shopping.tasks;

public class Checkout  implements Runnable{

  private final int waitTimeInSeconds;
  private volatile String nextCustomer = "Bob";

  public Checkout(int waitTimeInSeconds) {
    this.waitTimeInSeconds = waitTimeInSeconds;
  }

  public static Checkout fastCheckout() {
    Checkout checkout = new Checkout(2);
    checkout.startTimer();
    return checkout;
  }

  public static Checkout slowCheckout() {
    Checkout checkout = new Checkout(7);
    checkout.startTimer();
    return checkout;
  }

  public String nextCustomer() {
    return nextCustomer;
  }


  private void startTimer(){
    new Thread(() -> {
      try {
        Thread.sleep(waitTimeInSeconds * 1000);
        run();
      } catch (InterruptedException ignored) {}
    }).start();
  }

  @Override
  public void run() {
    nextCustomer = "Dana";
  }
}


