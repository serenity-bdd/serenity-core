package net.thucydides.model.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class DataTableRow {
    private final List<?> values;  // A list of strings or integers
    private final long lineNumber;
    private TestResult result;

    public DataTableRow(List<?> values) {
        this(values, 0, TestResult.UNDEFINED);
    }

    public DataTableRow(List<?> values,  long lineNumber) {
        this(values, lineNumber, TestResult.UNDEFINED);
    }

    public DataTableRow(List<?> values, long lineNumber, TestResult result) {
        this.values = (values == null) ? new ArrayList<>() : new ArrayList<>(values);
        this.result = result;
        this.lineNumber = lineNumber;
    }

    public List<?> getValues() {
        return (values == null) ? Collections.emptyList() : new ArrayList<>(values);
    }

    public List<String> getStringValues() {

        return getValues().stream().map(
                value -> (value == null) ? "" : value.toString()
        ).collect(Collectors.toList());
    }

    public long getLineNumber() { return lineNumber; }
    public TestResult getResult() {
        return result;
    }

    public void setResult(TestResult result) {
        this.result = result;
    }

    public void updateResult(TestResult newResult) {
        if (newResult == TestResult.UNDEFINED) {
            setResult(newResult);
        } else {
            setResult(TestResultComparison.overallResultFor(this.result, newResult));
        }
    }

    @Override
    public boolean equals(Object other) {
        if(other == this) {
            return true;
        }
        if(!(other instanceof DataTableRow)){
            return false;
        }
        return (this.getValues().equals(((DataTableRow) other).getValues()))
                && (this.lineNumber == ((DataTableRow) other).getLineNumber())
                && (this.result == ((DataTableRow) other).getResult());
    }

    public boolean equalsIgnoringTheResult(Object other) {
        if(other == this) {
            return true;
        }
        if(!(other instanceof DataTableRow)){
            return false;
        }
        return (this.getValues().equals(((DataTableRow) other).getValues()))
                && (this.lineNumber == ((DataTableRow) other).getLineNumber());
    }
    @Override
    public String toString() {
        return "DataTableRow{" +
                "values=" + getValues() +
                ", result=" + result +
                '}';
    }
}
