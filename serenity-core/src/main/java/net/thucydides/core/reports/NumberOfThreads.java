package net.thucydides.core.reports;

public class NumberOfThreads {

    static final double BLOCKING_COEFFICIENT_FOR_IO = 0.9;

    public static int forIOOperations() {
        final int numberOfCores = Runtime.getRuntime().availableProcessors();
        return (int) (numberOfCores / (1 - BLOCKING_COEFFICIENT_FOR_IO));
    }
}
