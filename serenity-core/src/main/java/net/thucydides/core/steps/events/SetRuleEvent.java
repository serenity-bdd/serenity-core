package net.thucydides.core.steps.events;

import net.thucydides.model.domain.Rule;

public class SetRuleEvent extends StepEventBusEventBase {


	private final Rule rule;

	public SetRuleEvent(final Rule rule) {
		this.rule = rule;
	}


	@Override
	public void play() {
		getStepEventBus().setRule(rule);
	}

	public String toString() {
		return("EventBusEvent SET_RULE " + rule);
	}
}
