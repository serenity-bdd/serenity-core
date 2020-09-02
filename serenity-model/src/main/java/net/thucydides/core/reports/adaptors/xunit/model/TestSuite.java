package net.thucydides.core.reports.adaptors.xunit.model;


import net.serenitybdd.core.collect.NewList;

import java.util.ArrayList;
import java.util.List;

public class TestSuite {

    private String name;
    private List<TestCase> testCases = new ArrayList<>();

    private static List<TestCase> NO_TEST_CASES = new ArrayList<>();

    private TestSuite(String name, List<TestCase> testCases) {
        this.name = name;
        this.testCases = testCases;
    }

    public static TestSuite named(String name) {
        return new TestSuite(name, NO_TEST_CASES);
    }

    public String getName() {
        return name;
    }

    public List<TestCase> getTestCases() {
        return NewList.copyOf(testCases);
    }

    public TestSuite withTestCases(List<TestCase> newTestCases) {
        return new TestSuite(name, newTestCases);
    }
}
