package net.thucydides.model.requirements.reports;

import net.thucydides.model.domain.TestResult;
import net.thucydides.model.util.Inflector;

public class TestCaseResultCount {
    private final TestResult result;
    private int count;

    public TestCaseResultCount(TestResult result) {
        this.result = result;
        this.count = 0;
    }

    public void increment() {
        count++;
    }

    public TestResult getResult() {
        return result;
    }

    public String getNumberOfTestCases() {
        return count + " " + Inflector.getInstance().of(count).times(result.getAdjective().toLowerCase() + " test case").inPluralForm().toString();
    }

    public int getCount() {
        return count;
    }
}
