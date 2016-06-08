package net.serenitybdd.screenplay

import org.hamcrest.Matchers
import spock.lang.Specification

/**
 * Created by john on 8/06/2016.
 */
class WhenEvaluatingAConsequenceWithAnEmbeddedTask extends Specification {

    class DisplayedErrors implements Question<List<String>> {
        @Override
        List<String> answeredBy(Actor actor) { ["invalid account number"] }
    }

    class EnterBankAccountNumber implements Task {

        boolean wasPerformed

        @Override
        def <T extends Actor> void performAs(T actor) {
            wasPerformed = true;
        }
    }

    def "you can ask to perform a task before asking the question in a consequence"() {

        given:
            def theDisplayedErrors = new DisplayedErrors()
            def enterABankAccountNumber = new EnterBankAccountNumber()
            def alice = new Actor("Alice")
        when:
            Consequence consequence = GivenWhenThen.seeThat(theDisplayedErrors, Matchers.contains("invalid account number")).
                                              whenAttemptingTo(enterABankAccountNumber)

            consequence.evaluateFor(alice)
        then:
            enterABankAccountNumber.wasPerformed
    }

    def "you can provide a business rule that will appear in the reports"() {

        given:
            def theDisplayedErrors = new DisplayedErrors()
            def enterABankAccountNumber = new EnterBankAccountNumber()
        when:
            Consequence consequence = GivenWhenThen.seeThat(theDisplayedErrors, Matchers.contains("invalid account number")).
                    whenAttemptingTo(enterABankAccountNumber).
                    because("The account number must be numerical")
        then:
            consequence.toString() == "The account number must be numerical"
    }

    class EnterBankAccountNumberWithAValue implements Task, RecordsInputs {

        String value;

        public String getValue() { return value }

        @Override
        def <T extends Actor> void performAs(T actor) {
        }

        @Override
        String getInputValues() {
            return value
        }
    }


    def "you can provide a value that will appear with the business rule in the reports"() {

        given:
            def theDisplayedErrors = new DisplayedErrors()
            def enterABankAccountNumberWithAValueOfQwerty = new EnterBankAccountNumberWithAValue(value: "qwerty")
        when:
            Consequence consequence = GivenWhenThen.seeThat(theDisplayedErrors, Matchers.contains("invalid account number")).
                    whenAttemptingTo(enterABankAccountNumberWithAValueOfQwerty).
                    because("The account number must be numerical")
        then:
            consequence.toString() == "The account number must be numerical [qwerty]"
    }

    class ThereAreErrors implements Question<Boolean> {
        @Override
        Boolean answeredBy(Actor actor) { true }
    }

    def "you can provide a value for a boolean question that will appear with the business rule in the reports"() {

        given:
            def thereAreErrors = new ThereAreErrors()
            def enterABankAccountNumberWithAValueOfQwerty = new EnterBankAccountNumberWithAValue(value: "qwerty")
        when:
            Consequence consequence = GivenWhenThen.seeThat(thereAreErrors, Matchers.contains("invalid account number")).
                    whenAttemptingTo(enterABankAccountNumberWithAValueOfQwerty).
                    because("The account number must be numerical")
        then:
            consequence.toString() == "The account number must be numerical [qwerty]"
    }


    def "should report sensible error messages for assertions"() {

        given:
            def alice = Actor.named("Alice")
            def theDisplayedErrors = new DisplayedErrors()
            def enterABankAccountNumberWithAValueOfQwerty = new EnterBankAccountNumberWithAValue(value: "!@£%^")
        and:
            Consequence consequence = GivenWhenThen.seeThat(theDisplayedErrors, Matchers.contains("invalid account name")).
                whenAttemptingTo(enterABankAccountNumberWithAValueOfQwerty).
                because("The account name must not contain symbols")
        when:
            consequence.evaluateFor(alice)
        then:
            AssertionError ex = thrown()
        and:
            ex.message.contains("invalid account name") && ex.message.contains("invalid account number")
    }

}
