package net.thucydides.core.steps;

import org.junit.Test;
import org.openqa.selenium.WebDriverException;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class WhenRecordingWebdriverExceptions {

    @Test
    public void should_retrieve_error_message_from_hamcrest_assertion_error() {
        try {
            assertThat("a", is("b"));
        } catch(AssertionError error) {
            Throwable convertedError = ErrorConvertor.convertToAssertion(error);
            assertThat(convertedError.getMessage(), containsString("Expected: is \"b\""));

        }
    }

    @Test
    public void should_retrieve_error_message_from_hamcrest_assertion_error_when_the_assertion_is_the_cause() {
        try {
            assertThat("a", is("b"));
        } catch(AssertionError assertionError) {
            WebDriverException exception = new WebDriverException(assertionError);
            Throwable convertedError = ErrorConvertor.convertToAssertion(exception);
            assertThat(convertedError.getMessage(), containsString("Expected: is \"b\""));

        }
    }


    @Test
    public void should_retrieve_error_message_from_hamcrest_assertion_error_for_more_complex_errors() {
        try {
            List<String> colors = Arrays.asList("red","blue","green");
            assertThat(colors, hasItem("yellow"));
        } catch(AssertionError assertionError) {
            WebDriverException exception = new WebDriverException(assertionError);
            Throwable convertedError = ErrorConvertor.convertToAssertion(exception);
            assertThat(convertedError.getMessage(), allOf(containsString("red"),
                                                          containsString("blue"),
                                                          containsString("green")));
        }
    }


}
