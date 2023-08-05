package net.thucydides.model.reports.adaptors.specflow;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Split a list of output lines into normal scenarios and table-based scenarios
 */
public class ScenarioSplitter {

    private static final String TITLE_LEAD = "***** ";

    private final List<String> outputLines;

    public static ScenarioSplitter on(List<String> outputLines) {
        return new ScenarioSplitter(outputLines);
    }

    public ScenarioSplitter(List<String> outputLines) {
        this.outputLines = new ArrayList<>(outputLines);
    }

    public List<SpecflowScenario> split() {
        List<SpecflowScenario> scenarios = new ArrayList<>();

        String currentTitle = null;
        SpecflowScenario currentScenario = null;
        for (List<String> blockLines : splitScenarios(outputLines)) {
            String blockTitle = blockLines.get(0);
            List<String> blockSteps = tail(blockLines);
            if (sameTitle(blockTitle, currentTitle)) {
                currentScenario.convertToTable(currentTitle);
                currentScenario.addRow(blockTitle, blockSteps);
            } else {
                currentScenario = new SpecflowScenario(removeParametersFrom(blockTitle));
                scenarios.add(currentScenario);
                currentScenario.addSteps(blockSteps);
            }
            currentTitle = blockTitle;
        }

        return scenarios;
    }

    private boolean sameTitle(String blockTitle, String currentTitle) {
        String blockTitleWithoutParameters = removeParametersFrom(blockTitle);
        String currentTitleWithoutParameters = removeParametersFrom(currentTitle);
        return blockTitleWithoutParameters.equals(currentTitleWithoutParameters);
    }

    private String removeParametersFrom(String title) {
        if (StringUtils.isNotEmpty(title) && title.indexOf("(") > -1) {
            return title.substring(0, title.indexOf("(") );
        } else {
            return title;
        }
    }

    private List<List<String>> splitScenarios(List<String> outputLines) {
        List<List<String>> scenarios = new ArrayList<>();
        List<String> current = null;
        for (String line : outputLines) {
            if (isTitle(line)) {
                if (current != null) {
                    scenarios.add(current);
                }
                current = new ArrayList<>();
                current.add(line);
            } else {
                current.add(line);
            }
        }
        scenarios.add(current);
        return scenarios;
    }

    // COMMON
    //
    private boolean isTitle(String line) {
        return line.trim().startsWith(TITLE_LEAD);
    }

    private List<String> tail(List<String> outlineLines) {
        return new ArrayList<>(outlineLines.subList(1, outlineLines.size()));
    }
}
