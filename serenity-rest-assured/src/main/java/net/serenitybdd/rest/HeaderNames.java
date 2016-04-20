package net.serenitybdd.rest;

/**
 * User: YamStranger
 * Date: 4/1/16
 * Time: 7:59 AM
 */
public enum HeaderNames {

    CONTENT_TYPE("Content-Type"),
    ACCEPT("Accept");

    private String name;

    HeaderNames(final String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    public String asString() {
        return name;
    }
}
