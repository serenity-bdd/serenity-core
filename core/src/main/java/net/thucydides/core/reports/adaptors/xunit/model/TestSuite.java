package net.thucydides.core.reports.adaptors.xunit.model;


import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import java.util.List;

public class TestSuite {

    private String name;
    private List<TestCase> testCases = Lists.newArrayList();

    private static List<TestCase> NO_TEST_CASES = Lists.newArrayList();

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
        return ImmutableList.copyOf(testCases);
    }

    public TestSuite withTestCases(List<TestCase> newTestCases) {
        return new TestSuite(name, newTestCases);
    }
}
