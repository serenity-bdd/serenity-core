package net.thucydides.core.steps.events;

import net.thucydides.core.model.TestTag;

import java.util.List;

public class AddTagsToCurrentTestEvent extends StepEventBusEventBase {

	private List<TestTag> tags;


	public AddTagsToCurrentTestEvent( List<TestTag> tags) {
		this.tags = tags;
	}


	@Override
	public void play() {
		getStepEventBus().addTagsToCurrentTest(tags);
	}

	public String toString() {
		return "EventBusEvent ADD_TAGS_TO_CURRENT_TEST_EVENT " + tags;
	}
}
