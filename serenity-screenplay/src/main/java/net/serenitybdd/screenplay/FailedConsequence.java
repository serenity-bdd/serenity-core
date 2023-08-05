package net.serenitybdd.screenplay;

import net.serenitybdd.model.exceptions.SerenityManagedException;

public class FailedConsequence {
        private final Consequence consequence;
        private final RuntimeException runtimeExceptionCause;
        private final Error errorCause;

        public FailedConsequence(Consequence consequence, Throwable cause) {
            this.consequence = consequence;
            if (cause instanceof Error) {
                this.errorCause = (Error) cause;
                this.runtimeExceptionCause = null;
            } else if (cause instanceof RuntimeException) {
                this.errorCause = null;
                this.runtimeExceptionCause = (RuntimeException) cause;
            } else {
                this.errorCause = null;
                this.runtimeExceptionCause = (RuntimeException) SerenityManagedException.detachedCopyOf(cause);
            }
        }

        public Consequence getConsequence() {
            return consequence;
        }

        public Throwable getCause() {
            return (runtimeExceptionCause != null) ? runtimeExceptionCause : errorCause;
        }

    public void throwException() {
        if (runtimeExceptionCause != null) {
            throw runtimeExceptionCause;
        }
        throw errorCause;
    }
}
