package net.serenitybdd.screenplay

public class SomethingBadHappenedException extends Error {

    public SomethingBadHappenedException(String msg) {
        super(msg)
    }

    public SomethingBadHappenedException(String msg, Throwable e) {
        super(msg, e)
    }
}
