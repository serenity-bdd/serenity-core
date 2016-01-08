package net.serenitybdd.screenplay;

import net.thucydides.core.steps.StepEventBus;

public abstract class BaseConsequence<T> implements Consequence<T> {

    private Class<? extends Error> complaintType;
    private String complaintDetails;

    protected Error errorFrom(Throwable actualError) {
        if (actualError instanceof Error) {
            return (Error) actualError;
        }
        return new Error(actualError);
    }

    protected void throwComplaintTypeErrorIfSpecified(Error actualError) {
        if (complaintType != null) {
            throw Complaint.from(complaintType, complaintDetails, actualError);
        }
    }

    protected boolean thisStepShouldBeIgnored() {
        return (StepEventBus.getEventBus().currentTestIsSuspended() || StepEventBus.getEventBus().aStepInTheCurrentTestHasFailed());
    }

    @Override
    public BaseConsequence<T> orComplainWith(Class<? extends Error> complaintType) {
        return orComplainWith(complaintType, null);
    }

    @Override
    public BaseConsequence<T> orComplainWith(Class<? extends Error> complaintType, String complaintDetails) {
        this.complaintType = complaintType;
        this.complaintDetails = complaintDetails;
        return this;
    }

}
