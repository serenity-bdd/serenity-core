package net.thucydides.core.model

import spock.lang.Specification

import javax.validation.Validation
import javax.validation.ValidatorFactory

/**
 * Created by john on 21/06/2014.
 */
class WhenValidatingTestOutcomeObjects extends Specification {

    def "should reject a test outcome with no method name"() {
        given:
            def testOutcome = new TestOutcome(null);
        when:
            ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
            def validator = factory.getValidator();
            def violations = validator.validate(testOutcome)
        then:
            !violations.isEmpty()

    }
}