package net.serenitybdd.core.steps

import net.thucydides.core.annotations.Step
import spock.lang.Specification

import static org.assertj.core.api.Java6Assertions.assertThat

class WhenReturningValuesFromStepMethodsAfterATestFailure extends Specification {

    static class Person {
        private String name;
        private int age;
    }

    static class ASimpleStepLibrary {

        void failingStep() {
            assertThat(1).isEqualTo(0)
        }

        @Step
        String returnsAString() {
            return "foo"
        }

        @Step
        int returnsAPrimitiveInteger() {
            return 99
        }

        @Step
        Integer returnsAnInteger() {
            return 99
        }

        @Step
        long returnsAPrimitiveLong() {
            return 99999999L
        }

        @Step
        Long returnsALong() {
            return 99999999L
        }

        @Step
        double returnsAPrimitiveDouble() {
            return 3.14
        }

        @Step
        Double returnsADouble() {
            return 3.14
        }

        @Step
        BigDecimal returnsABigDecimal() {
            return new BigDecimal("42")
        }

        @Step
        Boolean returnsABoolean() {
            return Boolean.FALSE
        }

        @Step
        boolean returnsAPrimitiveBoolean() {
            return false
        }

        @Step
        Person returnsADomainObject() {
            return new Person([name:"Bill", age:40])
        }

    }

    ASimpleStepLibrary stepLibrary = Instrumented.instanceOf(ASimpleStepLibrary).newInstance();

    def setup() {
        stepLibrary.failingStep();
    }

    def "should return empty strings"() {
        expect:
        stepLibrary.returnsAString() == ""
    }

    def "should return 0 for integers"() {
        expect:
        stepLibrary.returnsAnInteger() == 0 && stepLibrary.returnsAPrimitiveInteger() == 0
    }

    def "should return 0 for longs"() {
        expect:
        stepLibrary.returnsALong() == 0L && stepLibrary.returnsAPrimitiveLong() == 0L
    }

    def "should return 0 for doubles"() {
        expect:
        stepLibrary.returnsADouble() == 0.0 && stepLibrary.returnsAPrimitiveDouble() == 0.0
    }

    def "should return 0 for big decimals"() {
        expect:
        stepLibrary.returnsABigDecimal().intValue() == 0
    }

    def "should return false for booleans"() {
        expect:
        stepLibrary.returnsABoolean() == Boolean.FALSE && stepLibrary.returnsAPrimitiveBoolean() == false
    }

    def "should return mocked out domain objects"() {
        expect:
        stepLibrary.returnsADomainObject() != null
    }
}

