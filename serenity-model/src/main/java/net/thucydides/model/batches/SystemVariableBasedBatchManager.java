package net.thucydides.model.batches;

import net.thucydides.model.util.EnvironmentVariables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;

import static net.thucydides.model.ThucydidesSystemProperty.*;

/**
 * Manages running test cases (i.e. test classes) in batches.
 */

public class SystemVariableBasedBatchManager implements BatchManager {

    private final AtomicInteger testCaseCount = new AtomicInteger(0);
    protected final int batchCount;
    protected final int batchNumber;

    private Set<String> registeredTestCases = new CopyOnWriteArraySet<String>();

    private final Logger logger = LoggerFactory.getLogger(SystemVariableBasedBatchManager.class);

    /**
     * The batch manager is initiated using system properties.
     * These properties are "thucydides.batch.size" (or "thucydides.batch.count") and "thucydides.batch.number".
     */
    
    public SystemVariableBasedBatchManager(EnvironmentVariables environmentVariables) {
        this.batchCount = getBatchCountFrom(environmentVariables);
        this.batchNumber = THUCYDIDES_BATCH_NUMBER.integerFrom(environmentVariables, 0);
    }

    private int getBatchCountFrom(EnvironmentVariables environmentVariables) {
        int batchCountValue = THUCYDIDES_BATCH_SIZE.integerFrom(environmentVariables, 0);
        if (batchCountValue == 0) {
            batchCountValue = THUCYDIDES_BATCH_COUNT.integerFrom(environmentVariables, 0);
        }
        return batchCountValue;
    }

    public int getCurrentTestCaseNumber() {
        return testCaseCount.get();
    }

    public void registerTestCase(Class<?> klass) {
        String testCaseName = klass.getName();
        registerTestCaseIfNew(testCaseName);
    }

    public void registerTestCase(String testCaseName) {
        registerTestCaseIfNew(testCaseName);
    }

    private synchronized void registerTestCaseIfNew(String testCaseName) {
        if (!registeredTestCases.contains(testCaseName)) {
            registeredTestCases.add(testCaseName);
            testCaseCount.getAndIncrement();
        }
    }

    @Override
    public boolean shouldExecuteThisTest(int testCount) {
        if (batchCount > 0) {
            return (testCaseCount.get() % batchCount == getActualBatchNumber());
        } else {
            return true;
        }
    }
    
    protected int getActualBatchNumber() {
        return batchNumber % batchCount;
    }
}
