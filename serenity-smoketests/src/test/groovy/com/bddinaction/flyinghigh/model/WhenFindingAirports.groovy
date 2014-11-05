package com.bddinaction.flyinghigh.model

import com.bddinaction.flyinghigh.services.AirportService
import spock.lang.Ignore

class WhenFindingAirports {

    AirportService airportService;

    @Ignore("Pending")
    def "should find airport by airport code"() {
        given:
            // TODO: create an airport service
        when:
            def airport = airportService.findAirportByCode("SYD")
        then:
            airport.name == "Sydney"
    }

}
