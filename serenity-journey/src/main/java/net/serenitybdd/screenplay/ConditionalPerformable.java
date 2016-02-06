package net.serenitybdd.screenplay;

public class ConditionalPerformable implements Performable {
    private final Question<Boolean> shouldNotPerform;
    private final Performable task;

    public ConditionalPerformable(Question<Boolean> shouldNotPerform, Performable task) {
        this.shouldNotPerform = shouldNotPerform;
        this.task = task;
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        if (shouldNotPerform.answeredBy(actor)) { return; }

        task.performAs(actor);
    }
}
