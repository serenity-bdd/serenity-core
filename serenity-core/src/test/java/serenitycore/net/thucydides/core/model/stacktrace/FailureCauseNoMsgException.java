package serenitycore.net.thucydides.core.model.stacktrace;

/**
 * Support Class for FailureCauseTest.
 * Could not be inner class as then the test would turn into an UnrecognisedException
 * 
 * @author Robert Zimmermann
 */
public class FailureCauseNoMsgException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public FailureCauseNoMsgException() {
        super();
    }
}
