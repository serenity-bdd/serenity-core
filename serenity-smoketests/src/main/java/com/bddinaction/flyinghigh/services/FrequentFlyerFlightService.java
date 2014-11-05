package com.bddinaction.flyinghigh.services;

import com.bddinaction.flyinghigh.model.Airport;
import com.bddinaction.flyinghigh.model.Flight;

public class FrequentFlyerFlightService implements FlightService {

    private final AirportService airportService;

    public FrequentFlyerFlightService(AirportService airportService) {
        this.airportService = airportService;
    }

    @Override
    public Flight findFlightByNumber(String airportCode, String flightNumber) {
        Airport departureAirport = airportService.findAirportByCode(airportCode);
        return new Flight(flightNumber,departureAirport, new Airport("SYD","Sydney"), "06:00");
    }
}
