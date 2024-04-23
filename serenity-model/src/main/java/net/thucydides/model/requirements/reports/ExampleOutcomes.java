package net.thucydides.model.requirements.reports;

import net.thucydides.model.domain.TestOutcome;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ExampleOutcomes {

    public static List<ExampleOutcome> from(TestOutcome outcome) {
        if (outcome.isDataDriven()) {
            return outcome.getTestSteps().stream().map(
                            testStep -> new ExampleOutcome(
                                    stepTitleIn(testStep.getDescription()),
                                    stepSubtitleIn(testStep.getDescription()),
                                    testStep.getResult(),
                                    testStep.getStartTime(),
                                    testStep.getDuration(),
                                    testStep.getChildren().size(),
                                    testStep.getAllStepsText()
                            )).collect(Collectors.toList());
        } else {
            return new ArrayList<>();
        }
    }


    private static String stepTitleIn(String description) {
        return (description.contains("({")) ? (description.substring(0, description.indexOf("({"))) : description;
    }

    private static String stepSubtitleIn(String description) {
        return (description.contains("({")) ? trimmedDescription(description.substring(description.indexOf("({"))) : "";
    }

    private static String trimmedDescription(String description) {
        int parametersStart = description.indexOf("{") + 1;
        int parametersEnd = description.lastIndexOf("}");
        return description.substring(parametersStart, parametersEnd);
    }
}
