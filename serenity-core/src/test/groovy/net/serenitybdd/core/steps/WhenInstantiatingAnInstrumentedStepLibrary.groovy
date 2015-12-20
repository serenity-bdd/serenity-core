package net.serenitybdd.core.steps

import spock.lang.Specification

class WhenInstantiatingAnInstrumentedStepLibrary extends Specification {

    public static class ASimpleStepLibrary {}

    def "should instantiate step libraries with a default constructor"() {
        when:
            def stepLibrary = Instrumented.instanceOf(ASimpleStepLibrary).newInstance();
        then:
            stepLibrary != null
    }

    public static class AStepLibraryWithAttributes {
        private final String firstName;
        private final String lastName;

        public AStepLibraryWithAttributes(String firstName, String lastName) {
            this.firstName = firstName
            this.lastName = lastName
        }

        String getFirstName() {
            return firstName
        }

        String getLastName() {
            return lastName
        }
    }

    def "should instantiate step libraries with a parameterised constructor"() {
        when:
            def stepLibrary = Instrumented.instanceOf(AStepLibraryWithAttributes).withProperties("Sarah-Jane","Smith");
        then:
            stepLibrary != null
        and:
            stepLibrary.firstName == "Sarah-Jane" &&
            stepLibrary.lastName == "Smith"


    }

}
