package net.serenitybdd.screenplay.questions;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.targets.Target;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class UIStateReaderBuilder<T>{
    private final Target target;
    private final Class<T> type;
    private String subject;
    private final Optional<String> optionalParameter;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public UIStateReaderBuilder(Target target, Class<T> type) {
        this.target = target;
        this.type = type;
        this.subject = target.getName();
        this.optionalParameter = Optional.empty();
    }

    public UIStateReaderBuilder(Target target, Class<T> type, Optional<String> optionalParameter) {
        this.target = target;
        this.type = type;
        this.subject = target.getName();
        this.optionalParameter = optionalParameter;
    }

    public UIStateReaderBuilder<T> describedAs(String subject) {
        this.subject = subject;
        return this;
    }

    /**
     * A convenience method to return a question about a target
     * e.g. Text.of(VetList.VET_NAME).asAString()
     */
    public Question<String> asAString() {
        return Question.about(subject).answeredBy(actor -> ((TargetedUIState<T>) viewedBy(actor)).asString());
    }

    public Question<LocalDate> asADate() {
        return Question.about(subject).answeredBy(actor -> ((TargetedUIState<T>) viewedBy(actor)).asLocalDate());
    }

    public Question<LocalDate> asADate(String format) {
        return Question.about(subject).answeredBy(actor -> ((TargetedUIState<T>) viewedBy(actor)).asLocalDate(format));
    }

    public Question<BigDecimal> asABigDecimal() {
        return Question.about(subject).answeredBy(actor -> ((TargetedUIState<T>) viewedBy(actor)).asBigDecimal());
    }

    public Question<Boolean> asABoolean() {
        return Question.about(subject).answeredBy(actor -> ((TargetedUIState<T>) viewedBy(actor)).asBoolean());
    }

    public Question<Double> asDouble() {
        return Question.about(subject).answeredBy(actor -> ((TargetedUIState<T>) viewedBy(actor)).asDouble());
    }

    public Question<Float> asFloat() {
        return Question.about(subject).answeredBy(actor -> ((TargetedUIState<T>) viewedBy(actor)).asFloat());
    }

    public Question<Long> asLong() {
        return Question.about(subject).answeredBy(actor -> ((TargetedUIState<T>) viewedBy(actor)).asLong());
    }

    public Question<Integer> asInteger() {
        return Question.about(subject).answeredBy(actor -> ((TargetedUIState<T>) viewedBy(actor)).asInteger());
    }

    public <T> Question<T> asEnum(Class<T> enumType) {
        return Question.about(subject)
                .answeredBy(
                        actor -> EnumValues.forType(enumType).getValueOf(((TargetedUIState<T>) viewedBy(actor)).asString()));
    }

    /**
     * A convenience method to return a question about a target
     * e.g. Text.of(VetList.VET_NAME).asACollection()
     */
    public Question<List<String>> asAList() {
        return Question.about(subject).answeredBy(actor -> ((TargetedUIState<String>) viewedBy(actor)).asList());
    }

    public <E> Question<List<E>> asAListOf(Class<E> type) {
        return Question.about(subject).answeredBy(actor -> ((TargetedUIState<E>) viewedBy(actor)).asListOf(type));
    }

    public T viewedBy(Actor actor) {
        try {
            if (optionalParameter.isPresent()) {
                return (T) type.getConstructor(Target.class, Actor.class, String.class).newInstance(target, actor, optionalParameter.get());
            } else {
                return (T) type.getConstructor(Target.class, Actor.class).newInstance(target, actor);
            }
        } catch (Exception e) {
            logger.error("Failed to instantiate UIStateReader of type " + type, e);
            throw new IllegalStateException("Failed to instantiate UIStateReader of type " + type, e);
        }
    }
}
