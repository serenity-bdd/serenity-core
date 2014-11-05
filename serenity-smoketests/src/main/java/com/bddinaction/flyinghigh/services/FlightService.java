package com.bddinaction.flyinghigh.services;

import com.bddinaction.flyinghigh.model.Flight;

/**
 * A description goes here.
 * User: john
 * Date: 29/12/2013
 * Time: 10:59 PM
 */
public interface FlightService {
    Flight findFlightByNumber(String airportCode, String flightNumber);
}
