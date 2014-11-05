package net.thucydides.core.batches;


/**
 * Exception container for invalid batchng strategy
 */
public class UnsupportedBatchStrategyException extends RuntimeException {
    public UnsupportedBatchStrategyException(String message, Exception e) {
    }
}
