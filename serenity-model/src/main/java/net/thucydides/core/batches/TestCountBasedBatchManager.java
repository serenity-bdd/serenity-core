package net.thucydides.core.batches;

import net.thucydides.core.util.EnvironmentVariables;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class TestCountBasedBatchManager extends SystemVariableBasedBatchManager {
    private Map<Integer, AtomicInteger> batchNumberTestCount = new HashMap<>();
    private Map<Integer, Integer> testCaseNumberBatchNumber = new HashMap<>();

    public TestCountBasedBatchManager(EnvironmentVariables environmentVariables) {
        super(environmentVariables);

        for (int i=0; i< batchCount; i++) {
            batchNumberTestCount.put(i, new AtomicInteger(0));
        }
    }

    public boolean shouldExecuteThisTest(int testCount) {
        return (batchCount > 0) ? shouldExecuteThisTestInCurrentBatch(testCount) : true;
    }

    private synchronized boolean shouldExecuteThisTestInCurrentBatch(int testCount) {
        if (!testCaseNumberBatchNumber.containsKey(getCurrentTestCaseNumber())) {
            addTestCaseToOptimalBatch();
        }

        incTestMethodsSizeInBatch(testCount);

        return (testCaseNumberBatchNumber.get(getCurrentTestCaseNumber()) == getActualBatchNumber());
    }

    private void addTestCaseToOptimalBatch() {
        testCaseNumberBatchNumber.put(getCurrentTestCaseNumber(), getOptimalBatchForNewTestCase());
    }

    private int getOptimalBatchForNewTestCase() {
        int batchNumber = 0;
        AtomicInteger testMethodsSize = batchNumberTestCount.get(batchNumber);

        for (Map.Entry<Integer, AtomicInteger> entry : batchNumberTestCount.entrySet()) {
            if (testMethodsSize.get() > entry.getValue().get()) {
                batchNumber = entry.getKey();
                testMethodsSize = entry.getValue();
            }
        }

        return batchNumber;
    }

    private void incTestMethodsSizeInBatch(int testCount) {
        int batchNumber = testCaseNumberBatchNumber.get(getCurrentTestCaseNumber());
        batchNumberTestCount.get(batchNumber).getAndAdd(testCount);
    }
}
