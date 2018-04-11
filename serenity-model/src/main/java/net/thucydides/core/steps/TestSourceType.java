package net.thucydides.core.steps;

public enum TestSourceType {

    TEST_SOURCE_JUNIT("JUnit"),TEST_SOURCE_JBEHAVE("JBehave"),TEST_SOURCE_CUCUMBER("Cucumber");

    private final String value;

    TestSourceType(String value) {
        this.value = value;
    }

    public String getValue(){
        return value;
    }
}
