package com.bddinaction.flyinghigh.jbehave.steps.persona;

import com.bddinaction.flyinghigh.model.FrequentFlyer;

public enum FrequentFlyerPersona {

    Joe("Joe","Bloggs", "12345678"),
    Jill("Jill","Bloggs", "12345678");

    private final String firstName;
    private final String lastName;
    private final String frequentFlyerNumber;

    FrequentFlyerPersona(String firstName, String lastName, String frequentFlyerNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.frequentFlyerNumber = frequentFlyerNumber;
    }

    public FrequentFlyer getFrequentFlyer() {
        return FrequentFlyer.withFrequentFlyerNumber(frequentFlyerNumber).named(firstName, lastName);
    }
}
