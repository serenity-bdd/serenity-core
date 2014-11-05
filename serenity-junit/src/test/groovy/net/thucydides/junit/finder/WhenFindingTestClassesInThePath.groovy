package net.thucydides.junit.finder

import net.thucydides.junit.sampletests.thucydidestests.SampleDataDrivenTestCase
import net.thucydides.junit.sampletests.thucydidestests.SampleNonThucydidesTestCase
import net.thucydides.junit.sampletests.thucydidestests.SampleTestCase
import spock.lang.Specification

class WhenFindingTestClassesInThePath extends Specification {

    TestFinder testFinder

    def "should be able to find all the test cases annotated with the ThucydidesRunner test runner"() {

        given:

            testFinder = TestFinder.thatFinds().allTests().inPackage("net.thucydides.junit.sampletests")

        when:

            def testClasses = testFinder.getClasses()

        then:

            testClasses.contains(SampleTestCase) && testClasses.contains(SampleDataDrivenTestCase)

    }

    def "should be able to count the Thucydides tests"() {

        when:

            testFinder = TestFinder.thatFinds().allTests().inPackage("net.thucydides.junit.sampletests")

        then:

            testFinder.countTestMethods() == 3

    }

    def "should order the set of all test cases by name"() {

        given:
            testFinder = TestFinder.thatFinds().allTests().inPackage("net.thucydides.junit.sampletests")

        when:
            def testClasses = testFinder.getClasses()

        then:
            testClasses == [SampleDataDrivenTestCase, SampleTestCase]
    }

    def "should order the set of all test methods by name"() {

        given:

        testFinder = TestFinder.thatFinds().allTests().inPackage("net.thucydides.junit.sampletests")

        when:

        def testMethods = testFinder.allTestMethods

        then:

        testMethods.collect{ it.name } == ["testSomething", "testSomethingElse", "testSomethingWithData"]

    }

    def "should be able to find just the non-data-driven test cases annotated with the ThucydidesRunner test runner"() {

        given:

            testFinder = TestFinder.thatFinds().normalTests().inPackage("net.thucydides.junit.sampletests")

        when:

            def testClasses = testFinder.getClasses()

        then:

            testClasses == [SampleTestCase]

    }

    def "should be able to find just non-data-driven test cases annotated with the ThucydidesRunner test runner"() {

        given:

            testFinder = TestFinder.thatFinds().dataDrivenTests().inPackage("net.thucydides.junit.sampletests")

        when:

            def testClasses = testFinder.getClasses()

        then:

            testClasses == [SampleDataDrivenTestCase]

    }

    def "should skip test cases that are not annotated with the ThucydidesRunner test runner"() {

        given:

            testFinder = TestFinder.thatFinds().allTests().inPackage("net.thucydides.junit.sampletests")

        when:

            def testClasses = testFinder.getClasses();

        then:

            !testClasses.contains(SampleNonThucydidesTestCase)

    }

    def "should be able to list all the test methods in all of the Thucydides test cases"() {

        given:

            testFinder = TestFinder.thatFinds().allTests().inPackage("net.thucydides.junit.sampletests")

        when:

            def testMethods = testFinder.getAllTestMethods()

        then:

            testMethods.size() == 3

    }

    def "should be able to list the test methods in just the normal Thucydides test cases"() {

        given:

            testFinder = TestFinder.thatFinds().normalTests().inPackage("net.thucydides.junit.sampletests")

        when:

            def testMethods = testFinder.getAllTestMethods()

        then:

            testMethods.size() == 2

    }

    def "should be able to list the test methods in just the data-driven Thucydides test cases"() {

        given:

            testFinder = TestFinder.thatFinds().dataDrivenTests().inPackage("net.thucydides.junit.sampletests")

        when:

            def testMethods = testFinder.getAllTestMethods()

        then:

            testMethods.size() == 1

    }

    

    def "should be able to search for tests by name"() {

        given:

            testFinder = TestFinder.thatFinds().normalTests().inPackage("net.thucydides.junit.sampletests")

        when:

            def matchingTestMethods = testFinder.findTestMethods().withNameContaining("SomethingElse")

        then:

            matchingTestMethods.size() == 1 && matchingTestMethods.get(0).getName() == "testSomethingElse"

    }

    

    def "should be able to count the normal tests in a given package"() {

        given:

            testFinder = TestFinder.thatFinds().normalTests().inPackage("net.thucydides.junit.sampletests")

        when:

            def testCount = testFinder.countTestMethods()

        then:

            testCount == 2

    }

    

    def "should be able to count the data-driven tests in a given package"() {

        given:

            testFinder = TestFinder.thatFinds().dataDrivenTests().inPackage("net.thucydides.junit.sampletests")

        when:

            def testCount = testFinder.countTestMethods()

        then:

            testCount == 5

    }
}
