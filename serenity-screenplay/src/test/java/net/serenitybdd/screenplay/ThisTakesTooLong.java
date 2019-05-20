package net.serenitybdd.screenplay;

public class ThisTakesTooLong  extends AssertionError {
  public ThisTakesTooLong() {
  }

  public ThisTakesTooLong(Object detailMessage) {
    super(detailMessage);
  }

  public ThisTakesTooLong(String message, Throwable cause) {
    super(message, cause);
  }
}
