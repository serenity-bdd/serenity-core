package net.serenitybdd.screenplay.questions

import net.serenitybdd.screenplay.Actor
import net.serenitybdd.screenplay.Question
import spock.lang.Specification

class WhenWorkingWithAggregateQuestions extends Specification {

    def "counting the number of items in a set"() {
        given:
            Actor tracy = Actor.named("Tracy")

        when:
            Question<Set<String>> whatColours = new Question<Set<String>>() {
                @Override
                Set<String> answeredBy(Actor actor) {
                    return ["Red","Blue","Green"]
                }
            }
        then:
            AggregateQuestions.theTotalNumberOf(whatColours).answeredBy(tracy) == 3

    }

    def "counting the number of items in a list"() {
        given:
        Actor tracy = Actor.named("Tracy")

        when:
        Question<List<String>> whatColours = new Question<List<String>>() {
            @Override
            List<String> answeredBy(Actor actor) {
                return ["Red","Blue","Green"]
            }
        }
        then:
        AggregateQuestions.theTotalNumberOf(whatColours).answeredBy(tracy) == 3

    }

    def "getting the sum of the items in a list"() {
        given:
        Actor tracy = Actor.named("Tracy")

        when:
        Question<Set<String>> ages = new Question<Set<Integer>>() {
            @Override
            Set<String> answeredBy(Actor actor) {
                return [10,20,30]
            }
        }
        then:
        AggregateQuestions.theSumOf(ages).answeredBy(tracy) == 60

    }

    def "getting the sum of an empty list"() {
        given:
        Actor tracy = Actor.named("Tracy")

        when:
        Question<Set<String>> ages = new Question<Set<Integer>>() {
            @Override
            Set<String> answeredBy(Actor actor) {
                return []
            }
        }
        then:
        AggregateQuestions.theSumOf(ages).answeredBy(tracy) == 0

    }


    def "getting the max of the items in a list"() {
        given:
        Actor tracy = Actor.named("Tracy")

        when:
        Question<Set<String>> ages = new Question<Set<Integer>>() {
            @Override
            Set<String> answeredBy(Actor actor) {
                return [10,20,30]
            }
        }
        then:
        AggregateQuestions.theMaximumOf(ages).answeredBy(tracy) == 30

    }

    def "getting the min of the items in a list"() {
        given:
        Actor tracy = Actor.named("Tracy")

        when:
        Question<Set<String>> ages = new Question<Set<Integer>>() {
            @Override
            Set<String> answeredBy(Actor actor) {
                return [10,20,30]
            }
        }
        then:
        AggregateQuestions.theMinimumOf(ages).answeredBy(tracy) == 10

    }


    def "getting the reverse of the items in a list"() {
        given:
        Actor tracy = Actor.named("Tracy")

        when:
        Question<List<String>> ages = new Question<List<String>>() {
            @Override
            ArrayList<String> answeredBy(Actor actor) {
                return new ArrayList<String>("10","20","30")
            }
        }
        then:
        AggregateQuestions.theReverse(ages).answeredBy(tracy) == ["30","20","10"]

    }

}
