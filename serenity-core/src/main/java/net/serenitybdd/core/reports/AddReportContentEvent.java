package net.serenitybdd.core.reports;

import net.thucydides.core.steps.events.StepEventBusEventBase;
import net.thucydides.model.domain.ReportData;

public class AddReportContentEvent extends StepEventBusEventBase {


	private ReportDataSaver reportDataSaver;
	private ReportData reportData;

	public AddReportContentEvent(final ReportDataSaver reportDataSaver,ReportData reportData) {
		this.reportDataSaver = reportDataSaver;
		this.reportData =  reportData;
	}


	@Override
	public void play() {
		reportDataSaver.doAddContents(reportData);
	}

	public String toString() {
		return("EventBusEvent AddReportContentEvent");
	}
}
