package net.thucydides.core.reports.adaptors.specflow;

import net.serenitybdd.core.collect.NewList;
import net.thucydides.core.model.*;
import net.thucydides.core.reports.adaptors.common.FilebasedOutcomeAdaptor;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Loads TestOutcomes from a specflow output file
 */
public class SpecflowAdaptor extends FilebasedOutcomeAdaptor {

    private static final String TITLE_LEAD = "***** ";

    /**
     * Loads TestOutcomes from a SpecFlow output file or a directory containing output files.
     *
     * This is the console output, not the XML output file, which does not contain the details about each step
     * execution.
     */
    @Override
    public List<TestOutcome> loadOutcomesFrom(File source) throws IOException {
        if (source.isDirectory()) {
            List<TestOutcome> outcomes = new ArrayList<>();
            for(File outputFile : source.listFiles()) {
                outcomes.addAll(outcomesFromFile(outputFile));
            }
            return outcomes;
        } else {
            return outcomesFromFile(source);
        }
    }

    private List<TestOutcome> outcomesFromFile(File outputFile) throws IOException {
        List<String> outputLines = Files.readAllLines(outputFile.toPath(), Charset.defaultCharset());
        return scenarioOutputsFrom(outputLines).stream().map(this::toTestOutcome).collect(Collectors.toList());
    }

    private TestOutcome toTestOutcome(List<String> outputLines) {
        SpecflowScenarioTitleLine titleLine = new SpecflowScenarioTitleLine(outputLines.get(0));
        Story story = Story.called(titleLine.getStoryTitle()).withPath(titleLine.getStoryPath());
        TestOutcome outcome = TestOutcome.forTestInStory(titleLine.getScenarioTitle(), story);

        for(SpecflowScenario scenario : ScenarioSplitter.on(outputLines).split()) {
            if (scenario.usesDataTable()) {
                DataTable dataTable = DataTable.withHeaders(headersFrom(titleLine)).build();
                outcome.useExamplesFrom(dataTable);
                recordRowSteps(outcome, scenario);
            } else {
                outcome.recordSteps(stepsFrom(scenario.getSteps()));
            }
        }

        return outcome;
    }

    private void recordRowSteps(TestOutcome outcome, SpecflowScenario scenario) {
        for(SpecflowTableRow row : scenario.getRows()) {
            List<TestStep> rowSteps = stepsFrom(row.getRowSteps());
            SpecflowScenarioTitleLine rowTitle = new SpecflowScenarioTitleLine(row.getRowTitle());
            TestResult rowResult = TestResultList.overallResultFrom(getTestResults(rowSteps));

            DataTableRow dataTableRow = new DataTableRow(rowTitle.getArguments(), 0);
            dataTableRow.setResult(rowResult);
            outcome.addRow(dataTableRow);

            outcome.recordStep(TestStep.forStepCalled(rowTitle.getRowTitle()).withResult(rowResult));
            outcome.startGroup();
            outcome.recordSteps(rowSteps);
            outcome.endGroup();
        }
    }

    private List<String> headersFrom(SpecflowScenarioTitleLine titleLine) {
        // TODO: This should eventually come from the .feature file
        List<String> headers = new ArrayList();
        for(int i = 0; i < titleLine.getArguments().size(); i++) {
            headers.add(Character.toString( (char) (65 + i)));
        }
        return headers;
    }

    private List<TestStep> stepsFrom(List<String> scenarioOutput) {
        List<TestStep> discoveredSteps = new ArrayList();
        ScenarioStepReader stepReader = new ScenarioStepReader();
        List<String> lines = NewList.copyOf(scenarioOutput);
        while (!lines.isEmpty()) {
            discoveredSteps.add(stepReader.consumeNextStepFrom(lines));
        }
        return NewList.copyOf(discoveredSteps);
    }

    private List<List<String>> scenarioOutputsFrom(List<String> outputLines) {
        List<List<String>> scenarios = new ArrayList<>();

        List<String> currentScenario = null;
        SpecflowScenarioTitleLine currentTitle = null;
        for (String line : outputLines) {
            if (isTitle(line)) {
                SpecflowScenarioTitleLine newTitleLine = new SpecflowScenarioTitleLine(line);
                if (currentTitle == null || !newTitleLine.getTitleName().equals(currentTitle.getTitleName())) {
                    currentTitle = new SpecflowScenarioTitleLine(line);
                    currentScenario = new ArrayList<>();
                    scenarios.add(currentScenario);
                }
            }
            if (currentScenario != null) {
                currentScenario.add(line);
            }
        }
        return NewList.copyOf(scenarios);
    }

    private boolean isTitle(String line) {
        return line.trim().startsWith(TITLE_LEAD);
    }

    private List<TestResult> getTestResults(List<TestStep> testSteps) {
        return testSteps.stream().map(TestStep::getResult).collect(Collectors.toList());
    }
}
