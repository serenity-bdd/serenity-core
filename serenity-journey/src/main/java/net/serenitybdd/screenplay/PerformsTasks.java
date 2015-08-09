package net.serenitybdd.screenplay;

public interface PerformsTasks {
    void start();
    <T extends Task> void attemtpsTo(T... todos);

}
