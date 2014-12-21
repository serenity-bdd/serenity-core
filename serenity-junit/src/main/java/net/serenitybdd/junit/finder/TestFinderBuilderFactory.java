package net.serenitybdd.junit.finder;


public class TestFinderBuilderFactory {

    public TestFinderBuilder allTests() {
        return TestFinderBuilder.on(FinderType.ALL_TESTS);
    }

    public TestFinderBuilder normalTests() {
        return TestFinderBuilder.on(FinderType.NORMAL_TESTS);
    }

    public TestFinderBuilder dataDrivenTests() {
        return TestFinderBuilder.on(FinderType.DATA_DRIVEN_TESTS);
    }


}
