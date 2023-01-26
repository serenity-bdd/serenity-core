package net.serenitybdd.core.reports;

import net.thucydides.core.steps.events.StepEventBusEventBase;

public class AddReportContentEvent extends StepEventBusEventBase {


	private ReportDataSaver reportDataSaver;
	private String contents;

	public AddReportContentEvent(final ReportDataSaver reportDataSaver,String contents) {
		this.reportDataSaver = reportDataSaver;
		this.contents =  contents;
	}


	@Override
	public void play() {
		reportDataSaver.doAddContents(contents);
	}

	public String toString() {
		return("EventBusEvent AddReportContentEvent");
	}
}
