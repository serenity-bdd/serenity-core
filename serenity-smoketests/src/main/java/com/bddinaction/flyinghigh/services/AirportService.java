package com.bddinaction.flyinghigh.services;

import com.bddinaction.flyinghigh.model.Airport;

/**
 * A description goes here.
 * User: john
 * Date: 29/12/2013
 * Time: 11:10 PM
 */
public interface AirportService {
    Airport findAirportByCode(String airportCode);
}
