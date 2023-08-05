package net.thucydides.model.reports.adaptors.xunit.model;

/**
 * A failure or error as represented in an xUnit testcase result.
 */
public class TestException {
    private final String message;
    private final String type;
    private final String errorOutput;

    public TestException(String message, String errorOutput, String type) {
        this.message = message;
        this.errorOutput = errorOutput;
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public String getType() {
        return type;
    }

    public String getErrorOutput() {
        return errorOutput;
    }

    public Throwable asAssertionFailure() {
        return new AssertionError(message);
    }

    public Throwable asException() {
        return new TestError(message);
    }
}
