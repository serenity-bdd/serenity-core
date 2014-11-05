package com.bddinaction.flyinghigh.model

import spock.lang.Specification
import spock.lang.Unroll

import static Status.*

class WhenCheckingMinimumStatusPoints extends Specification {

    @Unroll
    def "should know what the minimum points are for a given status level"() {
        expect:
             Status.statusLevelFor(points) == expectedStatus
        where:
            points  | expectedStatus
            -1      | Bronze
            0       | Bronze
            299     | Bronze
            300     | Silver
            699     | Silver
            700     | Gold
            1499    | Gold
            1500    | Platinum
    }

}

