package net.serenitybdd.screenplay;

public class AnonymousTaskRunnable extends AnonymousPerformableRunnable implements Task {
    public AnonymousTaskRunnable(String title, Runnable actions) {
        super(title, actions);
    }
}
