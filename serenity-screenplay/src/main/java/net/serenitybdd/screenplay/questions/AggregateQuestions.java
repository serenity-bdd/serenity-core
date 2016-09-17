package net.serenitybdd.screenplay.questions;

import net.serenitybdd.screenplay.Question;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public class AggregateQuestions {

    public static <T> Question<Integer> theTotalNumberOf(Question<? extends Collection<T>> listQuestion) {
        return new CountQuestion(listQuestion);
    }

    public static Question<Integer> theSumOf(Question<? extends Collection<Integer>> listQuestion) {
        return new SumQuestion(listQuestion);
    }

    public static <T extends Object & Comparable<? super T>> Question<T> theMaximumOf(Question<? extends Collection<T>> listQuestion) {
        return new MaxQuestion<>(listQuestion);
    }

    public static <T extends Object & Comparable<? super T>> Question<T> theMaximumOf(Question<? extends Collection<T>> listQuestion,
                                                                                      Comparator<? super T> comparator) {
        return new MaxQuestion<>(listQuestion, comparator);
    }

    public static <T extends Object & Comparable<? super T>> Question<T> theMinimumOf(Question<? extends Collection<T>> listQuestion) {
        return new MinQuestion<>(listQuestion);
    }

    public static <T extends Object & Comparable<? super T>> Question<T> theMinimumOf(Question<? extends Collection<T>> listQuestion,
                                                                                      Comparator<? super T> comparator) {
        return new MinQuestion<>(listQuestion,comparator);
    }

    public static <T> Question<List<T>> theReverse(Question<? extends List<T>> listQuestion) {
        return new ReverseQuestion<>(listQuestion);
    }

    public static <T extends Object & Comparable<? super T>> Question<List<T>> theSorted(Question<? extends List<T>> listQuestion) {
        return new SortedQuestion<>(listQuestion);
    }

    public static <T extends Object & Comparable<? super T>> Question<List<T>> theSorted(Question<? extends List<T>> listQuestion,
                                                                                         Comparator<? super T> comparator) {
        return new SortedQuestion<>(listQuestion, comparator);
    }

}
