package io.cucumber.gherkin;

import io.cucumber.messages.IdGenerator;

public class IncrementingIdGenerator implements IdGenerator {
    private int next = 0;

    @Override
    public String newId() {
        return Integer.toString(next++);
    }

}