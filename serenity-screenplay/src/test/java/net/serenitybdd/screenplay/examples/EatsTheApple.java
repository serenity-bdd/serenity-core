package net.serenitybdd.screenplay.examples;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Performable;

public class EatsTheApple implements Performable {

    public Apple apple;

    public EatsTheApple() {}

    public EatsTheApple(Apple apple) { this.apple = apple; }

    @Override
    public <T extends Actor> void performAs(T actor) {
        apple.eat();
    }
}
