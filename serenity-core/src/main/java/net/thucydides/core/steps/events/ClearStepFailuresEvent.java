package net.thucydides.core.steps.events;


/**
 * Event to clear step failures and assumption violations when starting a new example
 * in a parameterized test or Scenario Outline.
 * <p>
 * This event clears:
 * - Step failure flag
 * - Assumption violation flag
 * - Test suspended flag
 * <p>
 * This ensures each example starts with clean state and previous skipped/ignored
 * examples don't affect subsequent passing examples.
 * <p>
 * See: https://github.com/serenity-bdd/serenity-core/issues/3691
 */
public class ClearStepFailuresEvent extends StepEventBusEventBase {


	@Override
	public void play() {
		getStepEventBus().clearStepFailures();
		getStepEventBus().clearAssumptionViolated();
		getStepEventBus().unsuspend();
	}
}
