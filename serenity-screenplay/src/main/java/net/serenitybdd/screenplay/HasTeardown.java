package net.serenitybdd.screenplay;

/**
 * Implement this Interface when you wish an {@link Ability} to be torn down upon calling OnStage.drawTheCurtain()
 */
public interface HasTeardown {
    void tearDown();
}
