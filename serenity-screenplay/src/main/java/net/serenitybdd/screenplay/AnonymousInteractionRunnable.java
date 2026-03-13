package net.serenitybdd.screenplay;

public class AnonymousInteractionRunnable extends AnonymousPerformableRunnable implements Interaction {
    public AnonymousInteractionRunnable(String title, Runnable actions) {
        super(title, actions);
    }
}
