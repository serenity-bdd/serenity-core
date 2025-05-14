package net.serenitybdd.core.reports;

import net.thucydides.core.steps.events.StepEventBusEventBase;

public class AddReportScreenshotEvent extends StepEventBusEventBase {


	private final String screenshotName;
	private final byte[] screenshot;

	public AddReportScreenshotEvent(String screenshotName,byte[] screenshot) {
		this.screenshotName = screenshotName;
		this.screenshot = screenshot;
	}


	@Override
	public void play() {
		getStepEventBus().recordScreenshot(screenshotName, screenshot);
	}

	public String toString() {
		return("EventBusEvent AddReportScreenshotEvent " + screenshotName);
	}
}
