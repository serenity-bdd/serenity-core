package net.serenitybdd.screenplay;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public interface Question<ANSWER> {
    ANSWER answeredBy(Actor actor);

    default String getSubject() {
        return "";
    }

    static QuestionBuilder about(String subject) {
        return new QuestionBuilder(subject);
    }

    default Question<Boolean> asBoolean() {
        return Question.about(getSubject()).answeredBy(actor -> Boolean.parseBoolean(String.valueOf(this.answeredBy(actor))));
    }

    static Question<Boolean> not(Question<Boolean> question) {
        return Question.about(question.getSubject()).answeredBy(actor -> !question.answeredBy(actor));
    }

    default Question<String> asString() {
        return Question.about(getSubject()).answeredBy(actor -> String.valueOf(this.answeredBy(actor)));
    }

    default Question<Integer> asInteger() {
        return Question.about(getSubject()).answeredBy(actor -> Integer.parseInt(String.valueOf(this.answeredBy(actor))));
    }

    default Question<Double> asDouble() {
        return Question.about(getSubject()).answeredBy(actor -> Double.parseDouble(String.valueOf(this.answeredBy(actor))));
    }

    default Question<Float> asFloat() {
        return Question.about(getSubject()).answeredBy(actor -> Float.parseFloat(String.valueOf(this.answeredBy(actor))));
    }

    default Question<Long> asLong() {
        return Question.about(getSubject()).answeredBy(actor -> Long.parseLong(String.valueOf(this.answeredBy(actor))));
    }

    default Question<BigDecimal> asBigDecimal() {
        return Question.about(getSubject()).answeredBy(actor -> new BigDecimal(String.valueOf(this.answeredBy(actor))));
    }

    default Question<LocalDate> asADate() {
        return Question.about(getSubject()).answeredBy(actor -> LocalDate.parse(String.valueOf(this.answeredBy(actor))));
    }

    default Question<LocalDate> asADate(String format) {
        return Question.about(getSubject()).answeredBy(actor -> LocalDate.parse(String.valueOf(this.answeredBy(actor)), DateTimeFormatter.ofPattern(format)));
    }

    default <T> Question<T> asEnum(Class<T> enumType) {
        return Question.about(getSubject())
                .answeredBy(actor -> EnumValues.forType(enumType).getValueOf(String.valueOf(this.answeredBy(actor))));
    }

    /**
     * Convert the answer to a question into another form using an arbitrary function.
     *
     * @param transformer
     * @param <T>
     * @return
     */
    default <T> Question<T> map(Function<ANSWER, T> transformer) {
        return Question.about(getSubject()).answeredBy(
                actor -> transformer.apply(answeredBy(actor))
        );
    }

    /**
     * Convert all the matching answers to a question into another form using an arbitrary function.
     */
    default <T> Question<Collection<T>> mapEach(Function<String, T> transformer) {
        return (actor) -> ((List<String>) this.answeredBy(actor)).stream().map(
                value -> (T) transformer.apply(value)
        ).collect(Collectors.toList());
    }


    /**
     * Returns a new question with the specified text as a subject.
     *
     * @param description
     * @return
     */
    default Question<ANSWER> describedAs(String description) {
        return Question.about(description).answeredBy(this::answeredBy);
    }

    default Question<ANSWER> as(Class<ANSWER> type) {
        return actor -> (ANSWER) DefaultConverters.converterFor(type).convert(answeredBy(actor));
    }

    default <T> Question<List<T>> asListOf(Class<T> type) {
        return (actor) -> ((List<T>) this.answeredBy(actor)).stream().map(
                value -> (T) DefaultConverters.converterFor(type).convert(value)
        ).collect(Collectors.toList());
    }

    default <T> Question<Collection<T>> asCollectionOf(Class<T> type) {
        return (actor) -> ((List<T>) this.answeredBy(actor)).stream().map(
                value -> (T) DefaultConverters.converterFor(type).convert(value)
        ).collect(Collectors.toList());
    }

}

