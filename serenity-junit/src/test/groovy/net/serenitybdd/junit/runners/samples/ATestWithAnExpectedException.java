package net.serenitybdd.junit.runners.samples;

import net.serenitybdd.junit.runners.SerenityRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(SerenityRunner.class)
public class ATestWithAnExpectedException {

    @Test(expected = IllegalStateException.class)
    public void shouldThrowAnIllegalStateException() {
        throw new NullPointerException();
    }
}
