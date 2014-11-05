package net.thucydides.core.reports.adaptors.specflow;

import ch.lambdaj.function.convert.Converter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.thucydides.core.model.*;
import net.thucydides.core.reports.adaptors.common.FilebasedOutcomeAdaptor;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import static ch.lambdaj.Lambda.convert;

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
            List<TestOutcome> outcomes = Lists.newArrayList();
            for(File outputFile : source.listFiles()) {
                outcomes.addAll(outcomesFromFile(outputFile));
            }
            return outcomes;
        } else {
            return outcomesFromFile(source);
        }
    }

    private List<TestOutcome> outcomesFromFile(File outputFile) throws IOException {
        List<String> outputLines = FileUtils.readLines(outputFile, Charset.defaultCharset());
        return convert(scenarioOutputsFrom(outputLines), toTestOutcomes());
    }

    private Converter<List<String>, TestOutcome> toTestOutcomes() {
        return new Converter<List<String>, TestOutcome>() {

            @Override
            public TestOutcome convert(List<String> outputLines) {
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
        };
    }

    private void recordRowSteps(TestOutcome outcome, SpecflowScenario scenario) {
        for(SpecflowTableRow row : scenario.getRows()) {
            List<TestStep> rowSteps = stepsFrom(row.getRowSteps());
            SpecflowScenarioTitleLine rowTitle = new SpecflowScenarioTitleLine(row.getRowTitle());
            TestResult rowResult = TestResultList.of(getTestResults(rowSteps)).getOverallResult();

            DataTableRow dataTableRow = new DataTableRow(rowTitle.getArguments());
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
        List<String> headers = Lists.newArrayList();
        for(int i = 0; i < titleLine.getArguments().size(); i++) {
            headers.add(Character.toString( (char) (65 + i)));
        }
        return headers;
    }

    private List<TestStep> stepsFrom(List<String> scenarioOutput) {
        List<TestStep> discoveredSteps = Lists.newArrayList();
        ScenarioStepReader stepReader = new ScenarioStepReader();
        List<String> lines = Lists.newArrayList(scenarioOutput);
        while (!lines.isEmpty()) {
            discoveredSteps.add(stepReader.consumeNextStepFrom(lines));
        }
        return ImmutableList.copyOf(discoveredSteps);
    }

    private List<List<String>> scenarioOutputsFrom(List<String> outputLines) {
        List<List<String>> scenarios = Lists.newArrayList();

        List<String> currentScenario = null;
        SpecflowScenarioTitleLine currentTitle = null;
        for (String line : outputLines) {
            if (isTitle(line)) {
                SpecflowScenarioTitleLine newTitleLine = new SpecflowScenarioTitleLine(line);
                if (currentTitle == null || !newTitleLine.getTitleName().equals(currentTitle.getTitleName())) {
                    currentTitle = new SpecflowScenarioTitleLine(line);
                    currentScenario = Lists.newArrayList();
                    scenarios.add(currentScenario);
                }
            }
            if (currentScenario != null) {
                currentScenario.add(line);
            }
        }
        return ImmutableList.copyOf(scenarios);
    }

    private boolean isTitle(String line) {
        return line.trim().startsWith(TITLE_LEAD);
    }

    private List<TestResult> getTestResults(List<TestStep> testSteps) {
        return convert(testSteps, new ExtractTestResultsConverter());
    }

    private static class ExtractTestResultsConverter implements Converter<TestStep, TestResult> {
        public TestResult convert(final TestStep step) {
            return step.getResult();
        }
    }
}
