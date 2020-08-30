package net.thucydides.core.model.stacktrace;

import net.serenitybdd.core.exceptions.UnrecognisedException;
import org.assertj.core.api.Assertions;
import org.junit.Test;

/**
 * @author Robert Zimmermann
 */
public class FailureCauseTest {
    @Test
    public void should_include_original_exception_message() {
        Throwable testExc = new IllegalStateException("Important Message");
        defaultExpectations(testExc, IllegalStateException.class, "Important Message");
    }

    @Test
    public void should_include_original_exception_message_msg_null() {
        Throwable testExc = new IllegalStateException();
        defaultExpectations(testExc, IllegalStateException.class, null);
    }

    @Test
    public void should_still_support_no_msg_exception_types() {
        Throwable testExc = new FailureCauseNoMsgException();
        defaultExpectations(testExc, FailureCauseNoMsgException.class, null);
    }

    @Test
    public void should_report_unresolvable_exception_types() {
        Throwable testExc = new InnerClassException();
        defaultExpectations(testExc, UnrecognisedException.class, null);
    }

    private void defaultExpectations(Throwable testExc, Class<?> expectedInstance, String expectedMessage) {
        FailureCause classUnderTest = new FailureCause(testExc);

        Throwable restoredException = classUnderTest.toException();
        Assertions.assertThat(restoredException).isExactlyInstanceOf(expectedInstance);
        Assertions.assertThat(restoredException).hasMessage(expectedMessage);
    }

    public class InnerClassException extends RuntimeException {
        private static final long serialVersionUID = 1L;

        public InnerClassException() {
            super();
        }
    }
}
