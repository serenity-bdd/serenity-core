package net.serenitybdd.screenplay.questions;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.annotations.Subject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Subject("the minimum value of #listQuestion")
public class SortedQuestion<T extends Object & Comparable<? super T>> implements Question<List<T>> {

    private final Question<? extends List<T>> listQuestion;
    private final Comparator<? super T> comparator;

    public SortedQuestion(Question<? extends List<T>> listQuestion) {
        this(listQuestion, null);
    }

    public SortedQuestion(Question<? extends List<T>> listQuestion, Comparator<? super T> comparator) {
        this.listQuestion = listQuestion;
        this.comparator = comparator;
    }

    @Override
    public List<T> answeredBy(Actor actor) {
        List<T> sortedItems = new ArrayList<>(listQuestion.answeredBy(actor));
        Collections.sort(sortedItems, comparator);
        return sortedItems;
    }
}