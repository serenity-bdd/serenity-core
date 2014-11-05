package com.bddinaction.flyinghigh.model;

public class Flight {
    private final String flightNumber;
    private final Airport departure;
    private final Airport destination;
    private final String time;

    public Flight(String flightNumber, Airport departure, Airport destination, String time) {
        this.flightNumber = flightNumber;
        this.departure = departure;
        this.destination = destination;
        this.time = time;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public Airport getDeparture() {
        return departure;
    }

    public Airport getDestination() {
        return destination;
    }

    public String getTime() {
        return time;
    }
}
