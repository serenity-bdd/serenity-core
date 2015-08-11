package net.thucydides.core.reports.adaptors.xunit.model;


import com.google.common.base.Optional;

public class TestCase {
    private String name;
    private String classname;
    private double time;
    private Optional<TestException> failure;
    private Optional<TestException> error;
    private Optional<Skip> skipped;

    private final static Optional<TestException> NO_FAILURE = Optional.absent();
    private final static Optional<TestException> NO_ERROR = Optional.absent();
    private final static Optional<Skip> NOT_SKIPPED = Optional.absent();

    private TestCase(String name,
                     String classname,
                     double time,
                     Optional<TestException> failure,
                     Optional<TestException> error,
                     Optional<Skip> skip) {
        this.name = name;
        this.classname = classname;
        this.time = time;
        this.failure = failure;
        this.error = error;
        this.skipped = skip;
    }

    public static TestCase withName(String name) {
        return new TestCase(name, "", 0.0, NO_FAILURE, NO_ERROR, NOT_SKIPPED);
    }

    public TestCase andClassname(String classname) {
        return new TestCase(name, classname, time, error, failure, skipped);
    }

    public TestCase andTime(double time) {
        return new TestCase(name, classname, time, error, failure, skipped);
    }

    public TestCase withFailure(TestException failure) {
        return new TestCase(name, classname, time, Optional.of(failure), error, skipped);
    }

    public TestCase withError(TestException error) {
        return new TestCase(name, classname, time, failure, Optional.of(error), skipped);
    }


    public TestCase wasSkipped(String message) {
        return new TestCase(name, classname, time, failure, error, Optional.of(new Skip(message)));
    }

    public String getName() {
        return name;
    }

    public String getClassname() {
        return classname;
    }

    public double getTime() {
        return time;
    }

    public Optional<TestException> getFailure() {
        return failure;
    }

    public Optional<TestException> getError() {
        return error;
    }

    public Optional<Skip> getSkipped() {
        return skipped;
    }

}
