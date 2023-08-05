package net.serenitybdd.screenplay;

import net.serenitybdd.screenplay.annotations.AnnotatedTitle;
import net.serenitybdd.screenplay.annotations.Subject;

import java.lang.reflect.Method;
import java.util.Optional;

import static net.thucydides.model.util.NameConverter.humanize;
import static org.apache.commons.lang3.StringUtils.lowerCase;

public class QuestionSubject<T> {

    private final Class<? extends Question> questionClass;
    private Question<T> question;

    @SuppressWarnings("unchecked")
    public static <T> QuestionSubject<T> fromClass(Class<?> questionClass) {
        return new QuestionSubject(questionClass);
    }

    public QuestionSubject(Class<? extends Question> questionClass) {
        this.questionClass = Uninstrumented.versionOf(questionClass);
    }

    public QuestionSubject andQuestion(Question question) {
        this.question = question;
        return this;
    }

    private Optional<String> annotatedSubject() {
        if (annotationOnMethodOf(questionClass).isPresent()) {
            return java.util.Optional.of(annotationOnMethodOf(questionClass)).get();
        }
        return annotatedSubjectFromClass(questionClass);
    }

    private Optional<String> annotatedSubjectFromClass(Class<?> questionClass) {
        if (questionClass.getAnnotation(Subject.class) != null) {
            return java.util.Optional.of(annotationOnClass(questionClass)).get();
        }

        if (questionClass.getSuperclass() != null) {
            return annotatedSubjectFromClass(questionClass.getSuperclass());
        }

        return Optional.empty();
    }

    private Optional<String> annotationOnMethodOf(Class<? extends Question> questionClass) {
        try {
            Method answeredBy = questionClass.getMethod("answeredBy", Actor.class);
            if (answeredBy.getAnnotation(Subject.class) != null) {
                String annotatedTitle = answeredBy.getAnnotation(Subject.class).value();
                annotatedTitle = AnnotatedTitle.injectFieldsInto(annotatedTitle).using(question);
                return Optional.of(annotatedTitle);
            }
        } catch (NoSuchMethodException e) {
            return Optional.empty();
        }
        return Optional.empty();
    }

    private Optional<String> annotationOnClass(Class<?> questionClass) {
        if (questionClass.getAnnotation(Subject.class) != null) {
            String annotatedTitle = questionClass.getAnnotation(Subject.class).value();
            annotatedTitle = AnnotatedTitle.injectFieldsInto(annotatedTitle).using(question);
            return Optional.of(annotatedTitle);
        }
        return Optional.empty();
    }

    private Optional<String> subjectFromQuestionInterface() {
        if (question.getSubject() == null || question.getSubject().isEmpty()) { return Optional.empty(); }
        return Optional.of(question.getSubject());
    }

    public String subject() {
        return subjectFromQuestionInterface().orElse(
                annotatedSubject().orElse(
                        lowerCase(humanize(questionClass.getSimpleName()))));
    }

}
