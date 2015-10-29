package net.serenitybdd.screenplay;

import net.serenitybdd.core.Serenity;
import net.serenitybdd.core.eventbus.Broadcaster;
import net.serenitybdd.screenplay.events.ActorBeginsPerformanceEvent;
import net.serenitybdd.screenplay.events.ActorEndsPerformanceEvent;
import net.serenitybdd.screenplay.exceptions.IgnoreStepException;

import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;

public class Actor implements PerformsTasks {

    private final String name;

    private final PerformedTaskTally taskTally = new PerformedTaskTally();
    private EventBusInterface eventBusInterface = new EventBusInterface();

    private Map<String, Object> notepad = newHashMap();
    private Map<Class, Ability> abilities = newHashMap();

    public Actor(String name) {
        this.name = name;
    }

    public String toString() {
        return name;
    }

    public static Actor named(String name) {
        return new Actor(name);
    }

    public String getName() {
        return name;
    }

    public <T extends Ability> Actor can(T doSomething) {
        doSomething.asActor(this);
        abilities.put(doSomething.getClass(), doSomething);
        return this;
    }

    @SuppressWarnings("unchecked")
    public <T extends Ability> T abilityTo(Class<? extends T> doSomething) {
        return (T) abilities.get(doSomething);
    }

    public final void has(Performable... todos) {
        attemptsTo(todos);
    }

    public final void attemptsTo(Performable... tasks) {
        beginPerformance();
        for (Performable task : tasks) {
            perform(task);
        }
        endPerformance();
    }

    private void beginPerformance() {
        Broadcaster.getEventBus().post(new ActorBeginsPerformanceEvent(name));
    }

    private void endPerformance() {
        Broadcaster.getEventBus().post(new ActorEndsPerformanceEvent(name));
    }

    public <ANSWER> ANSWER asksFor(Question<ANSWER> question) {
        return question.answeredBy(this);
    }

    private <T extends Performable> void perform(T todo) {
        try {
            taskTally.newTask();
            todo.performAs(this);

            if (anOutOfStepErrorOccurred()) {
                eventBusInterface.mergePreviousStep();
            }
        } catch (Throwable e) {
            eventBusInterface.reportStepFailureFor(todo, e);
            if (Serenity.shouldThrowErrorsImmediately()) {
                throw e;
            }
        } finally {
            eventBusInterface.updateOverallResult();
        }

    }

    public final void can(Consequence... consequences) {
        should(consequences);
    }

    public final void should(Consequence... consequences) {
        beginPerformance();
        for (Consequence consequence : consequences) {
            check(consequence);
        }
        endPerformance();
    }

    private boolean anOutOfStepErrorOccurred() {
        return eventBusInterface.aStepHasFailed()
               && eventBusInterface.getStepCount() > taskTally.getPerformedTaskCount();
    }

    private <T> void check(Consequence<T> consequence) {
        try {
            eventBusInterface.reportNewStepWithTitle(consequence.toString());
            consequence.evaluateFor(this);
            eventBusInterface.reportStepFinished();
        } catch (IgnoreStepException e) {
            eventBusInterface.reportStepIgnored();
        } catch (Throwable e) {
            eventBusInterface.reportStepFailureFor(consequence, e);
            if (Serenity.shouldThrowErrorsImmediately()) {
                throw e;
            }
        }
    }

    public <ANSWER> void remember(String key, Question<ANSWER> question) {
        ANSWER answer = this.asksFor(question);
        notepad.put(key, answer);
    }

    public void remember(String key, Object value) {
        notepad.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public <T> T recall(String key) {
        return (T) notepad.get(key);
    }

    public <T> T sawAsThe(String key) {
        return recall(key);
    }

    public <T> T gaveAsThe(String key) {
        return recall(key);
    }
}
