package net.serenitybdd.screenplay.described;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.Task;
import net.thucydides.core.annotations.Step;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DescribedTaskRunner implements Task {

    static final Logger LOGGER = LoggerFactory.getLogger(DescribedTaskRunner.class);
    private final String taskDescription;
    private Performable[] performTasks;

    public DescribedTaskRunner(RunStep.StepType stepType, String taskDescription, Performable... performTasks) {
        this.taskDescription = stepType + " " + taskDescription;
        this.performTasks = performTasks;
    }

    @Override
    @Step("#taskDescription")
    public <T extends Actor> void performAs(T actor) {
        LOGGER.debug(taskDescription);
        actor.attemptsTo(performTasks);
    }

}