package net.serenitybdd.junit5;

import net.serenitybdd.junit5.samples.integration.JUnit5NestedExample;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class WhenDetectingASerenityJUnit5TestClass {

	@Test
    public void a_class_with_the_extension_SerenityJUnit5Extension_should_be_recognized_as_Serenity_test() {
        assertThat(SerenityTestExecutionListener.isSerenityTestClass(JUnit5NestedExample.class), is(true));
        assertThat(SerenityTestExecutionListener.isSerenityTestClass(WhenDetectingASerenityJUnit5TestClass.class), is(false));
    }
}
