package net.serenitybdd.screenplay.described;

import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.Task;

import static net.serenitybdd.screenplay.Tasks.instrumented;

public class DescribedTask {

    private RunStep.StepType stepType;
    private final String taskDescription;

    private DescribedTask(RunStep.StepType stepType, String taskDescription) {
        this.stepType = stepType;
        this.taskDescription = taskDescription;
    }

    public static DescribedTask using(RunStep.StepType stepType, String taskDescription) {
        return new DescribedTask(stepType, taskDescription);
    }

    public Task tasksAreRun(Performable... tasks) {
        return instrumented(DescribedTaskRunner.class, stepType, taskDescription, tasks);
    }

    public Task taskIsRun(Performable task) {
        return tasksAreRun(task);
    }

    public Performable runTasks(Performable... tasks) {
        return tasksAreRun(tasks);
    }

    public Performable runTask(Performable tasks) {
        return tasksAreRun(tasks);
    }
}