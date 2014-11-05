package com.bddinaction.flyinghigh.model

import com.bddinaction.flyinghigh.services.AirportService
import com.bddinaction.flyinghigh.services.FrequentFlyerFlightService
import spock.lang.Specification

class WhenRetrievingFlightDetails extends Specification {

    def airportService

    def setup() {
        airportService = Mock(AirportService)
        airportService.findAirportByCode("MEL") >> new Airport("MEL", "Melbourne")
        airportService.findAirportByCode("SYD") >> new Airport("SYD", "Sydney")
    }

    def " Find flight details by flight number"() {
        given: "I need to know the details of flight number FH-101"
            def flightService = new FrequentFlyerFlightService(airportService);
            def flightNumber = "FH-101"
            def airport = "MEL"
        when: "I request the details about this flight"
            def flightDetails = flightService.findFlightByNumber(airport, flightNumber);
        then: "I should receive the correct flight details"
            flightDetails.flightNumber == "FH-101" &&
            flightDetails.departure.name == "Melbourne" &&
            flightDetails.departure.code == "MEL" &&
            flightDetails.destination.name == "Sydney" &&
            flightDetails.destination.code == "SYD" &&
            flightDetails.time == "06:00"
    }
}
