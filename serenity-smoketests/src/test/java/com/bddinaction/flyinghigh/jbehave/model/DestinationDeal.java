package com.bddinaction.flyinghigh.jbehave.model;

public class DestinationDeal {
    private final String destination;
    private final int price;

    public DestinationDeal(String destination, int price) {
        this.destination = destination;
        this.price = price;
    }

    public String getDestination() {
        return destination;
    }

    public int getPrice() {
        return price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DestinationDeal that = (DestinationDeal) o;

        if (price != that.price) return false;
        if (destination != null ? !destination.equals(that.destination) : that.destination != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = destination != null ? destination.hashCode() : 0;
        result = 31 * result + price;
        return result;
    }
}
