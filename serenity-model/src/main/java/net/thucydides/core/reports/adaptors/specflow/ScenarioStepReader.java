package net.thucydides.core.reports.adaptors.specflow;

import net.serenitybdd.core.collect.NewSet;
import net.thucydides.core.model.TestStep;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ScenarioStepReader {


    private static final Set<String> STEP_KEYWORDS = NewSet.of("given", "when", "then", "and");
    private static final String RESULT_LEAD = "-> ";

    public static boolean isResult(String line) {
        return line.trim().startsWith(RESULT_LEAD);
    }

    public TestStep consumeNextStepFrom(List<String> lines) {

        ScenarioStep scenarioStep = new ScenarioStep(consumeLinesFromNextStep(lines));

        TestStep step = TestStep.forStepCalled(scenarioStep.getTitle()).withResult(scenarioStep.getResult());

        if (scenarioStep.getDuration().isPresent()) {
            step.setDuration(scenarioStep.getDuration().get().longValue());
        }

        if (scenarioStep.getException().isPresent()) {
            step.failedWith(scenarioStep.getException().get());
        }

        return step;
    }

    private List<String> consumeLinesFromNextStep(List<String> lines) {
        List<String> stepLines = new ArrayList<>();

        String title = lines.remove(0);
        stepLines.add(title);
        while (!lines.isEmpty() && !isStepTitle(lines.get(0))) {
            stepLines.add(lines.remove(0));
        }
        return stepLines;
    }

    private boolean isStepTitle(String line) {
        String trimmedLowerCaseLine = line.trim().toLowerCase();
        for(String keyword : STEP_KEYWORDS) {
            if (trimmedLowerCaseLine.startsWith(keyword)) {
                return true;
            }
        }
        return false;
    }

}
