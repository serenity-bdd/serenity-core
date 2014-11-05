package com.bddinaction.flyinghigh.model

import spock.lang.Specification

import static Status.*

class WhenManagingFrequentFlyerMemberStatusUpdates extends Specification {

    def "A frequent flyer requires a first name, a last name, and a frequent flyer number"() {
        when:
            def member = FrequentFlyer.withFrequentFlyerNumber("12345678").named("Joe","Bloggs")
        then:
            member.frequentFlyerNumber == "12345678"
        and:
            member.firstName == "Joe"
        and:
            member.lastName == "Bloggs"
    }

    def "a new frequent flyer should have Bronze status"() {
        given:
            def member = FrequentFlyer.withFrequentFlyerNumber("12345678").named("Joe","Bloggs")
        when:
            def status = member.status
        then:
            status == Status.Bronze
    }


    def "should be able to upgrade a frequent flyer status"() {
        given:
            def member = FrequentFlyer.withFrequentFlyerNumber("12345678").named("Joe","Bloggs")
        when:
            def updatedMember = member.withStatus(Status.Silver)
        then:
            updatedMember.status == Status.Silver
    }

    def "a member should be able to earn extra status points"() {
        given:
            def member = FrequentFlyer.withFrequentFlyerNumber("12345678").
                    named("Joe","Bloggs").
                    withStatusPoints(initialPoints)
        when:
            member.earns(additionalPoints).statusPoints()
        then:
            member.statusPoints == expectedPoints
        where:
            initialPoints | additionalPoints  | expectedPoints
            0             | 100               | 100
            100           | 50                | 150
    }

    def "should upgrade status when enough status points are acquired"() {
        given: "a frequent flyer member with some points"
            def member = FrequentFlyer.withFrequentFlyerNumber("12345678").
                    named("Joe","Bloggs").
                    withStatusPoints(initialPoints).
                    withStatus(initialStatus)
        when: "he earns some extra points on a flight"
            member.earns(extraPoints).statusPoints()
        then: "he may or may not be upgraded to a new status"
            member.getStatus() == expectedStatus
        where:
            initialStatus | initialPoints | extraPoints | expectedStatus
            Bronze        | 0             | 299         | Bronze
            Bronze        | 0             | 300         | Silver
            Silver        | 0             | 699         | Silver
            Silver        | 0             | 700         | Gold
            Gold          | 0             | 1499        | Gold
            Gold          | 0             | 1500        | Platinum
    }

}
