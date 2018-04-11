package net.thucydides.core.batches;

public interface BatchManager {
    int getCurrentTestCaseNumber();

    public void registerTestCase(Class<?> testCaseClass);

    public void registerTestCase(String testCaseName);

    boolean shouldExecuteThisTest(int testCount);
}
