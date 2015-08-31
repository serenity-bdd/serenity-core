package net.serenitybdd.screenplay;

import net.serenitybdd.screenplay.exceptions.IgnoreStepException;

import java.util.Map;

import static com.google.common.collect.Maps.*;

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

    public <T extends Ability> Actor can(T doSomething) {
        abilities.put(doSomething.getClass(), doSomething);
        return this;
    }

    public <T extends Ability> T abilityTo(Class<? extends T> doSomething) {
        return (T) abilities.get(doSomething);
    }

    @SafeVarargs
    public final <T extends Performable> void has(T... todos) {
        attemptsTo(todos);
    }

    @SafeVarargs
    public final <T extends Performable> void attemptsTo(T... todos) {
        for (Performable todo : todos) {
            perform(todo);
        }
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
        } finally {
            eventBusInterface.updateOverallResult();
        }

    }

    @SafeVarargs
    public final <T> void should(Consequence<T>... consequences) {
        for (Consequence<T> consequence : consequences) {
            check(consequence);
        }
    }

    private boolean anOutOfStepErrorOccurred() {
        return eventBusInterface.getStepCount() > taskTally.getPerformedTaskCount();
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

    @SuppressWarnings("unchecked")
    public <T> T sawAsThe(String key) {
        return (T) recall(key);
    }

    @SuppressWarnings("unchecked")
    public <T> T gaveAsThe(String key) {
        return (T) recall(key);
    }
}
