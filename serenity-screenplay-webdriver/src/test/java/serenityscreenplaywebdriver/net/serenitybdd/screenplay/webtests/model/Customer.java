package serenityscreenplaywebdriver.net.serenitybdd.screenplay.webtests.model;

public class Customer {
    private final String name;
    private final String country;

    public Customer(String name, String country) {
        this.name = name;
        this.country = country;
    }

    public String getName() {
        return name;
    }

    public String getCountry() {
        return country;
    }
}
