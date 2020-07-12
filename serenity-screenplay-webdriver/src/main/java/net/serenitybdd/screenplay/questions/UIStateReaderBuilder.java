package net.serenitybdd.screenplay.questions;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.targets.Target;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

public class UIStateReaderBuilder<T>{
    private final Target target;
    private final Class<T> type;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public UIStateReaderBuilder(Target target, Class<T> type) {
        this.target = target;
        this.type = type;
    }

    /**
     * A convenience method to return a question about a target
     * e.g. Text.of(VetList.VET_NAME).asAString()
     */
    public Question<String> asAString() {
        return actor -> ((TargetedUIState<T>) viewedBy(actor)).asString();
    }

    public Question<LocalDate> asADate() {
        return actor -> ((TargetedUIState<T>) viewedBy(actor)).asLocalDate();
    }

    public Question<LocalDate> asADate(String format) {
        return actor -> ((TargetedUIState<T>) viewedBy(actor)).asLocalDate(format);
    }

    public Question<BigDecimal> asABigDecimal() {
        return actor -> ((TargetedUIState<T>) viewedBy(actor)).asBigDecimal();
    }

    public Question<Boolean> asABoolean() {
        return actor -> ((TargetedUIState<T>) viewedBy(actor)).asBoolean();
    }

    public Question<Double> asDouble() {
        return actor -> ((TargetedUIState<T>) viewedBy(actor)).asDouble();
    }

    public Question<Float> asFloat() {
        return actor -> ((TargetedUIState<T>) viewedBy(actor)).asFloat();
    }

    public Question<Long> asLong() {
        return actor -> ((TargetedUIState<T>) viewedBy(actor)).asLong();
    }

    public Question<Integer> asInteger() {
        return actor -> ((TargetedUIState<T>) viewedBy(actor)).asInteger();
    }
    /**
     * A convenience method to return a question about a target
     * e.g. Text.of(VetList.VET_NAME).asACollection()
     */
    public Question<Collection<String>> asAList() {
        return actor -> ((TargetedUIState<String>) viewedBy(actor)).asList();
    }

    public <E> Question<Collection<E>> asAListOf(Class<E> type) {
        return actor -> ((TargetedUIState<E>) viewedBy(actor)).asListOf(type);
    }

    public T viewedBy(Actor actor) {
        try {
            return (T) type.getConstructor(Target.class, Actor.class).newInstance(target, actor);
        } catch (Exception e) {
            logger.error("Failed to instantiate UIStateReader of type " + type, e);
            throw new IllegalStateException("Failed to instantiate UIStateReader of type " + type, e);
        }
    }
}
