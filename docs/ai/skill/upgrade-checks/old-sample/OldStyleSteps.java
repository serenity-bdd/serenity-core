package demo;

// A class written the way Serenity 3.x code was written: old packages for the annotations and utilities.
import net.thucydides.core.annotations.Step;
import net.thucydides.core.annotations.Steps;
import net.thucydides.core.annotations.Title;
import net.thucydides.core.annotations.WithTag;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.util.NameConverter;
import net.thucydides.core.steps.ScenarioSteps;

public class OldStyleSteps extends ScenarioSteps {

    EnvironmentVariables environmentVariables;

    @Steps
    OldStyleSteps nested;

    @Step("Humanise {0}")
    @Title("Humanise a name")
    @WithTag("old")
    public String humanise(String name) {
        return NameConverter.humanize(name);
    }
}
