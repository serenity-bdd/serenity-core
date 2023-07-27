package net.thucydides.core.batches;

import net.thucydides.core.util.EnvironmentVariables;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public enum BatchStrategy {

    DIVIDE_EQUALLY(SystemVariableBasedBatchManager.class),
    DIVIDE_BY_TEST_COUNT(TestCountBasedBatchManager.class);

    private Class<? extends BatchManager> batchManagerClass;

    BatchStrategy(Class<? extends BatchManager> batchManagerClass) {
        this.batchManagerClass = batchManagerClass;
    }

    public BatchManager instance(EnvironmentVariables environmentVariables) throws NoSuchMethodException, InvocationTargetException,
            IllegalAccessException, InstantiationException {
        Class<?>[] constructorArgs = {EnvironmentVariables.class};
        Constructor<? extends BatchManager> constructor = batchManagerClass.getConstructor(constructorArgs);
        return constructor.newInstance(environmentVariables);
    }

}
