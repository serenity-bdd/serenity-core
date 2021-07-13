
package net.serenitybdd.screenplay.described;

import static net.serenitybdd.screenplay.described.RunStep.StepType.AND;
import static net.serenitybdd.screenplay.described.RunStep.StepType.GIVEN;
import static net.serenitybdd.screenplay.described.RunStep.StepType.THEN;
import static net.serenitybdd.screenplay.described.RunStep.StepType.WHEN;
import static org.apache.commons.lang3.text.WordUtils.capitalizeFully;

public class RunStep {

    enum StepType {

        GIVEN, WHEN, THEN, AND;

        @Override
        public String toString() {
            return  capitalizeFully(name().replace("_", " ").toLowerCase());
        }
    }

    public static DescribedTask given(String taskDescription) {
        return DescribedTask.using(GIVEN, taskDescription);
    }

    public static DescribedTask and(String taskDescription) {
        return DescribedTask.using(AND, taskDescription);
    }

    public static DescribedTask when(String taskDescription) {
        return DescribedTask.using(WHEN, taskDescription);
    }

    public static DescribedTask then(String taskDescription) {
        return DescribedTask.using(THEN, taskDescription);
    }

}