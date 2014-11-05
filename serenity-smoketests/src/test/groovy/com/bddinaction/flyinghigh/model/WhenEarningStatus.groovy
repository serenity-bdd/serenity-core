package com.bddinaction.flyinghigh.model

import spock.lang.Specification

import static com.bddinaction.flyinghigh.model.Status.*

class WhenEarningStatus extends Specification {

    def "should earn status based on the number of points earned"() {
        given:
        def member = FrequentFlyer.withFrequentFlyerNumber("12345678")
                .named("Joe", "Jones")
                .withStatusPoints(initialPoints)
                .withStatus(initialStatus);

        when:
        member.earns(earnedPoints).statusPoints()

        then:
        member.status == finalStatus

        where:
        initialStatus | initialPoints | earnedPoints | finalStatus
        Bronze        | 0             | 100          | Bronze
        Bronze        | 0             | 300          | Silver
        Bronze        | 100           | 200          | Silver
        Silver        | 0             | 700          | Gold
        Gold          | 0             | 1500         | Platinum
    }
}
