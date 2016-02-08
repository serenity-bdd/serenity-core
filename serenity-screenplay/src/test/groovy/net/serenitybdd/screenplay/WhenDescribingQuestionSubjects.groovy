package net.serenitybdd.screenplay

import net.serenitybdd.screenplay.annotations.Subject
import spock.lang.Specification

/**
 * Created by john on 16/08/2015.
 */
class WhenDescribingQuestionSubjects  extends Specification {

    class AnswersSomeQuestion implements Question<String> {

        @Subject("some answer")
        public String answeredBy(Actor actor) {}
    }

    def "you can use the @Subject annotation on the answeredBy method to provide a tailored description of a question subject"() {
        given:
            def answersSomeQuestion = new AnswersSomeQuestion()
        when:
            def subject = QuestionSubject.fromClass(AnswersSomeQuestion).andQuestion(answersSomeQuestion).subject();
        then:
            subject == "some answer"
    }


    class AnswersSomeQuestionWithoutAnnotation implements Question<String> {

        public String answeredBy(Actor actor) {}
    }

    def "If the @Subject annotation is not used a humanized version of the class name should be used instead"() {
        given:
        def answersSomeQuestion = new AnswersSomeQuestionWithoutAnnotation()
        when:
        def subject = QuestionSubject.fromClass(AnswersSomeQuestionWithoutAnnotation).andQuestion(answersSomeQuestion).subject();
        then:
        subject == "answers some question without annotation"
    }


    @Subject("some answer")
    class AnswersSomeQuestionWithClassAnnotation implements Question<String> {

        public String answeredBy(Actor actor) {}
    }


    def "you can put the @Subject annotation at the class level"() {
        given:
        def answersSomeQuestion = new AnswersSomeQuestionWithClassAnnotation()
        when:
        def subject = QuestionSubject.fromClass(AnswersSomeQuestionWithClassAnnotation).andQuestion(answersSomeQuestion).subject();
        then:
        subject == "some answer"

    }

    class AnswersSomeQuestionWithVariables implements Question<String> {

        String color = "red"

        @Subject("my favorite color is #color")
        public String answeredBy(Actor actor) {}
    }


    def "you can use the #field notation to inject field values into the answer text"() {
        given:
        def answersSomeQuestion = new AnswersSomeQuestionWithVariables()
        when:
        def subject = QuestionSubject.fromClass(AnswersSomeQuestionWithVariables).andQuestion(answersSomeQuestion).subject();
        then:
        subject == "my favorite color is red"

    }

    @Subject("my favorite color is #color")
    class AnswersSomeQuestionWithVariablesInTheClassAnnotation implements Question<String> {

        String color = "red"

        public String answeredBy(Actor actor) {}
    }


    def "you can use the #field notation to inject field values into the answer text at the class level"() {
        given:
        def answersSomeQuestion = new AnswersSomeQuestionWithVariables()
        when:
        def subject = QuestionSubject.fromClass(AnswersSomeQuestionWithVariablesInTheClassAnnotation).andQuestion(answersSomeQuestion).subject();
        then:
        subject == "my favorite color is red"

    }

}
