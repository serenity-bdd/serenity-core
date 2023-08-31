package net.thucydides.model.reports.html;

import net.thucydides.model.reports.TestOutcomes;

public class TestOutcomesContext {

    private static TestOutcomes testOutcomes;

    public static void setTestOutcomes(TestOutcomes allTestOutcomes) {
        testOutcomes = allTestOutcomes;
    }

    public static TestOutcomes getCurrentTestOutcomes() {
        return testOutcomes;
    }
}
