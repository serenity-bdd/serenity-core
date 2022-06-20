package net.serenitybdd.screenplay.examples;

public class Apple {
    private boolean eaten = false;

    public void eat() {
        this.eaten = true;
    }

    public boolean isEaten() {
        return eaten;
    }
}
