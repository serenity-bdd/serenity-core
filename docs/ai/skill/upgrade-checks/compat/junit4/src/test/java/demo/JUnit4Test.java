package demo;

import net.serenitybdd.annotations.Step;
import net.serenitybdd.annotations.Steps;
import net.serenitybdd.junit.runners.SerenityRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SerenityRunner.class)
public class JUnit4Test {

    public static class Maths {
        @Step("Add {0} and {1}")
        public int add(int a, int b) { return a + b; }
    }

    @Steps
    Maths maths;

    @Test
    public void addsNumbers() {
        assertThat(maths.add(2, 3)).isEqualTo(5);
    }
}
