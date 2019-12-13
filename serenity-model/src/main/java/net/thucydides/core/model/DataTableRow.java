package net.thucydides.core.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DataTableRow {
    private final List<?> values;  // A list of strings or integers
    private final int lineNumber;
    private TestResult result;

    public DataTableRow(List<?> values) {
        this(values, 0, TestResult.UNDEFINED);
    }

    public DataTableRow(List<?> values,  int lineNumber) {
        this(values, lineNumber, TestResult.UNDEFINED);
    }

    public DataTableRow(List<?> values, int lineNumber, TestResult result) {
        this.values = new ArrayList<>(values);
        this.result = result;
        this.lineNumber = lineNumber;
    }

    public List<?> getValues() {
        return new ArrayList<>(values);
    }

    public List<String> getStringValues() {

        return values.stream().map(
                value -> (value == null) ? "" : value.toString()
        ).collect(Collectors.toList());
    }

    public int getLineNumber() { return lineNumber; }
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
    public String toString() {
        return "DataTableRow{" +
                "values=" + values +
                ", result=" + result +
                '}';
    }
}
