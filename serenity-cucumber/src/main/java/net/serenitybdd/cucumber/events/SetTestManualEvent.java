package net.serenitybdd.cucumber.events;


import io.cucumber.core.plugin.ScenarioContextParallel;
import net.serenitybdd.cucumber.core.plugin.TaggedScenario;
import net.serenitybdd.cucumber.core.plugin.UpdateManualScenario;
import io.cucumber.messages.types.Tag;
import net.serenitybdd.core.di.SerenityInfrastructure;
import net.thucydides.core.steps.events.StepEventBusEventBase;
import net.thucydides.model.webdriver.Configuration;

import java.util.List;

public class SetTestManualEvent extends StepEventBusEventBase {

	private final List<Tag> scenarioTags;
	private final String scenarioId;
	private final ScenarioContextParallel scenarioContext;

	public SetTestManualEvent(ScenarioContextParallel scenarioContext, List<Tag> scenarioTags, String scenarioId) {
		this.scenarioTags = scenarioTags;
		this.scenarioId = scenarioId;
		this.scenarioContext = scenarioContext;
	}
	@Override
	public void play() {
		Configuration systemConfiguration = SerenityInfrastructure.getConfiguration();
		getStepEventBus().testIsManual();
		TaggedScenario.manualResultDefinedIn(scenarioTags).ifPresent(
                testResult ->
                        UpdateManualScenario.forScenario(scenarioContext.getCurrentScenarioDefinition(scenarioId).getDescription())
                                .inContext(getStepEventBus().getBaseStepListener(), systemConfiguration.getEnvironmentVariables())
                                .updateManualScenario(testResult, scenarioTags)
        );
	}
}
