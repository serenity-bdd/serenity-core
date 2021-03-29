package serenityscreenplay.net.serenitybdd.screenplay;

import serenityscreenplay.net.serenitybdd.screenplay.actors.OnStage;

/**
 * Implement this Interface when you wish an {@link Ability} to be torn down upon calling
 * {@link OnStage#drawTheCurtain()}
 */
public interface HasTeardown {
    void tearDown();
}
