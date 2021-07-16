package net.serenitybdd.junit;

public enum SerenityJUnitSystemProperties {

    CONCURRENT_THREADS("serenity.concurrent.threads");

    private final String name;

    SerenityJUnitSystemProperties(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
