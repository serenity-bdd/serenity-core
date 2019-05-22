package net.serenitybdd.screenplay;

import net.serenitybdd.screenplay.actors.OnStage;

/**
 * Implement this Interface when you wish an {@link Ability} to be torn down upon calling
 * {@link OnStage#drawTheCurtain()}
 */
public interface HasTeardown {
    void tearDown();
}
