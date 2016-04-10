package net.serenitybdd.screenplay.authoring;

import net.serenitybdd.screenplay.Actor;

/**
 * Created by john on 8/04/2016.
 */
public interface RoleAction<T extends Actor> {
    void doIt(T actor);
}
