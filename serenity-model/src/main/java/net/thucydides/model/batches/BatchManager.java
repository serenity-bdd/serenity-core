package net.thucydides.model.batches;

public interface BatchManager {
    int getCurrentTestCaseNumber();

    public void registerTestCase(Class<?> testCaseClass);

    public void registerTestCase(String testCaseName);

    boolean shouldExecuteThisTest(int testCount);
}
