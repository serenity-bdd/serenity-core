package net.serenitybdd.screenplay.questions;

import net.serenitybdd.screenplay.Question;

import java.util.Collection;

public class AggregateQuestions {
    public static Question<Integer> theTotalNumberOf(Question<Collection<String>> listQuestion) {
        return new CountQuestion(listQuestion);
    }

    public static Question<Integer> theSumOf(Question<Collection<Integer>> listQuestion) {
        return new SumQuestion(listQuestion);
    }

    public static Question<Integer> theMaximumOf(Question<Collection<Integer>> listQuestion) {
        return new MaxQuestion(listQuestion);
    }

    public static Question<Integer> theMinimumOf(Question<Collection<Integer>> listQuestion) {
        return new MinQuestion(listQuestion);
    }

    public static Question<Integer> theReverse(Question<Collection<Integer>> listQuestion) {
        return new ReverseQuestion(listQuestion);
    }

}
