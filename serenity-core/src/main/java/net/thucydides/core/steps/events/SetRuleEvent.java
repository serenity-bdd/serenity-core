package net.thucydides.core.steps.events;

import net.thucydides.core.model.Rule;

public class SetRuleEvent extends StepEventBusEventBase {


	private Rule rule;

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
