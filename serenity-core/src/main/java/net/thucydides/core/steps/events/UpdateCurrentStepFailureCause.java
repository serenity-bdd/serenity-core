package net.thucydides.core.steps.events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UpdateCurrentStepFailureCause extends StepEventBusEventBase {


	private static final Logger LOGGER = LoggerFactory.getLogger(UpdateCurrentStepFailureCause.class);

	private Throwable failure;

	public UpdateCurrentStepFailureCause() {}

	public UpdateCurrentStepFailureCause(Throwable failure) {
		this.failure =  failure;
	}


	@Override
	public void play() {
	 	LOGGER.debug("SRP:Update current step failure cause: " + failure);
		getStepEventBus().getBaseStepListener().lastStepFailedWith(failure);
	}

	public String toString() {
		return("EventBusEvent UPDATE_CURRENT_STEP_FAILURE_CAUSE ");
	}
}
