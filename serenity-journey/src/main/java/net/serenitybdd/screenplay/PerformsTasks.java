package net.serenitybdd.screenplay;

public interface PerformsTasks {
    void start();
    <T extends Task> void attemptsTo(T... todos);
}
