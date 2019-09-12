package net.serenitybdd.core.model

import net.thucydides.core.model.ManualTestEvidence
import spock.lang.Specification

class WhenRecordingTestEvidence extends Specification {

    def "a simple link should be recorded with the label 'Test Evidence'"() {

        when:
            def evidence = ManualTestEvidence.from("http://my.evidence.link")
        then:
            evidence.link == "http://my.evidence.link"
            evidence.label == "Test Evidence"
    }

    def "a relative link should be recorded with the label 'Test Evidence'"() {

        when:
            def evidence = ManualTestEvidence.from("assets/evidence.png")
        then:
            evidence.link == "assets/evidence.png"
            evidence.label == "Test Evidence"
    }

    def "Labels can be provided in square brackets"() {

        when:
            def evidence = ManualTestEvidence.from("[My_Evidence]assets/evidence.png")
        then:
            evidence.link == "assets/evidence.png"
            evidence.label == "My Evidence"
    }

    def "Labels can be provided using the markdown notation"() {

        when:
            def evidence = ManualTestEvidence.from("[My Evidence](http://my.evidence.link)")
        then:
            evidence.link == "http://my.evidence.link"
            evidence.label == "My Evidence"
    }

    def "Links at the end of an expression are honored even if the label is wrong"() {

        when:
        def evidence = ManualTestEvidence.from("[My_Evidence(http://my.evidence.link)")
        then:
        evidence.link == "http://my.evidence.link"
        evidence.label == "[My Evidence"
    }

    def "Weird formats are ignored"() {

        when:
        def evidence = ManualTestEvidence.from("[My_Evidence[[http://my.evidence.link)")
        then:
        evidence.link == "[My_Evidence[[http://my.evidence.link)"
        evidence.label == "Test Evidence"
    }
}
