package net.thucydides.junit;

public enum ThucydidesJUnitSystemProperties {

    CONCURRENT_THREADS("thucydides.concurrent.threads");

    private final String name;

    ThucydidesJUnitSystemProperties(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
