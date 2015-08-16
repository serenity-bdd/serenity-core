package net.serenitybdd.screenplay;

public interface PerformsTasks {
    void start();
    <T extends Performable> void attemptsTo(T... todos);
}
