package net.serenitybdd.core.reports;

import net.thucydides.model.domain.ReportData;
import net.thucydides.model.domain.TestOutcome;
import net.thucydides.model.domain.TestResult;
import net.thucydides.model.domain.TestStep;
import net.thucydides.core.steps.StepEventBus;
import net.thucydides.core.steps.session.TestSession;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Optional;

import static net.thucydides.model.ThucydidesSystemProperty.SERENITY_REPORT_ENCODING;

public class ReportDataSaver implements WithTitle, AndContent, FromFile {

    private final StepEventBus eventBus;
    private String title;
    private boolean fileIsDownloadable = false;
    private boolean isEvidence = false;

    public ReportDataSaver(StepEventBus eventBus) {
        this.eventBus = eventBus;
    }

    public AndContent withTitle(String title) {
        this.title = title;
        return this;
    }

    @Override
    public void andContents(String contents) {
        ReportData reportData = ReportData.withTitle(title).andContents(contents).asEvidence(isEvidence);
        addReportContent(reportData);
    }

    private void addReportContent(ReportData reportData) {
        if(!TestSession.isSessionStarted()) {
            doAddContents(reportData);
        } else {
            TestSession.addEvent(new AddReportContentEvent(this,reportData));
        }
    }

    public void doAddContents(ReportData reportData) {
        eventBus.getBaseStepListener().latestTestOutcome().ifPresent(
            outcome -> currentStepOrBackgroundIn(outcome)
                .withReportData(reportData)
        );
    }

    private TestStep currentStepOrBackgroundIn(TestOutcome outcome) {
        if (outcome.currentStep().isPresent()) {
            return outcome.currentStep().get();
        } else {
            return outcome.recordStep(TestStep.forStepCalled("Background")
                           .withResult(TestResult.SUCCESS)).currentStep().get();
        }
    }

    @Override
    public void fromFile(Path source) throws IOException {
        Charset encoding = Charset.forName(SERENITY_REPORT_ENCODING.from(eventBus.getEnvironmentVariables(), StandardCharsets.UTF_8.name()));
        fromFile(source, encoding);
    }

    @Override
    public void fromFile(Path source, Charset encoding) throws IOException {
        ReportData reportData = (fileIsDownloadable) ?
            ReportData.withTitle(title).fromPath(source).asEvidence(isEvidence) :
            ReportData.withTitle(title).fromFile(source, encoding).asEvidence(isEvidence);
        addReportContent(reportData);
    }

    @Override
    public FromFile downloadable() {
        this.fileIsDownloadable = true;
        return this;
    }

    @Override
    public WithTitle asEvidence() {
        this.isEvidence = true;
        return this;
    }

}
