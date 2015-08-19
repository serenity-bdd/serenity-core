package net.serenitybdd.screenplay;

public interface PerformsTasks {
    <T extends Performable> void attemptsTo(T... todos);
}
