package net.thucydides.core.steps.events;


import io.cucumber.core.plugin.ScenarioContextParallel;
import io.cucumber.core.plugin.TaggedScenario;
import io.cucumber.core.plugin.UpdateManualScenario;
import io.cucumber.messages.types.Tag;
import net.serenitybdd.core.di.SerenityInfrastructure;
import net.thucydides.core.webdriver.Configuration;

import java.util.List;

public class SetTestManualEvent extends StepEventBusEventBase {

	private List<Tag> scenarioTags;
	private String scenarioId;
	private ScenarioContextParallel scenarioContext;

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
