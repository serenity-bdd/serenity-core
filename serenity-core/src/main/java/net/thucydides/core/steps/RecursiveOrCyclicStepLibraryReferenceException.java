package net.thucydides.core.steps;

public class RecursiveOrCyclicStepLibraryReferenceException extends RuntimeException {
    public RecursiveOrCyclicStepLibraryReferenceException(String message) {
        super(message);
    }
}
