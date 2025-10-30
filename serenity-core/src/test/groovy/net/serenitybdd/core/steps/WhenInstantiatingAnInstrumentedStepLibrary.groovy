package net.serenitybdd.core.steps

import net.serenitybdd.annotations.Steps
import net.serenitybdd.core.pages.PageObject
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


    public static class AStepLibraryOuterClass {

        public static class AnInnerPageObject extends PageObject {}

        public static class AnInnerStepLibrary {
            AnInnerPageObject innerPageObject;
        }

        @Steps
        AnInnerStepLibrary innerStepLibrary;
    }

    def "nested step libraries can also be instrumented"() {
        when:
        def stepLibraryContainer = Instrumented.instanceOf(AStepLibraryOuterClass).newInstance();
        then:
        stepLibraryContainer.innerStepLibrary != null
    }

    public static class AStepLibraryWithAttributesAndDefaultConstructor {
        private final String firstName;
        private final String lastName;

        public AStepLibraryWithAttributesAndDefaultConstructor(String firstName, String lastName) {
            this.firstName = firstName
            this.lastName = lastName
        }

        public AStepLibraryWithAttributesAndDefaultConstructor() {
            this("default", "default")
        }

        String getFirstName() {
            return firstName
        }

        String getLastName() {
            return lastName
        }
    }

    def "should instantiate step librariary with a parameterised constructor if parameterised and default constructor provided"() {
        when:
            def stepLibrary = Instrumented.instanceOf(AStepLibraryWithAttributesAndDefaultConstructor)
                .withProperties("Sarah-Jane", "Smith");
        then:
            stepLibrary != null
        and:
            stepLibrary.firstName == "Sarah-Jane" &&
                stepLibrary.lastName == "Smith"
    }

    def "should instantiate step librariary with a default constructor if parameterised and default constructor provided"() {
        when:
            def stepLibrary = Instrumented.instanceOf(AStepLibraryWithAttributesAndDefaultConstructor)
                .newInstance();
        then:
            stepLibrary != null
        and:
            stepLibrary.firstName == "default" &&
                stepLibrary.lastName == "default"
    }

    public static class AStepLibraryWithMultipleConstructors {
        private final String firstName;
        private final String lastName;

        public AStepLibraryWithMultipleConstructors(String firstName, String lastName) {
            this.firstName = firstName
            this.lastName = lastName
        }

        public AStepLibraryWithMultipleConstructors() {
            this("default", "default")
        }

        public AStepLibraryWithMultipleConstructors(String firstName) {
            this(firstName, "default")
        }

        String getFirstName() {
            return firstName
        }

        String getLastName() {
            return lastName
        }
    }

    def "should instantiate step librariary with multiple constructors based on amount of parameters"() {
        when:
            def stepLibrary = Instrumented.instanceOf(AStepLibraryWithMultipleConstructors)
                .withProperties("Sarah-Jane");
        then:
            stepLibrary != null
        and:
            stepLibrary.firstName == "Sarah-Jane" &&
                stepLibrary.lastName == "default"
    }
}
