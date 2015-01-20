package net.serenitybdd.junit.finder;

public class TestFinderBuilder {

    public static TestFinderBuilder on(FinderType finderType) {
        return new TestFinderBuilder(finderType);
    }

    private final FinderType finderType;

    protected TestFinderBuilder(FinderType finderType) {
        this.finderType = finderType;
    }

    public TestFinder inPackage(final String rootPackage) {
        switch (finderType) {
            case NORMAL_TESTS : return new NormalTestFinder(rootPackage);
            case DATA_DRIVEN_TESTS : return new DataDrivenTestFinder(rootPackage);
            default: return new DefaultTestFinder(rootPackage);
        }
    }
}

