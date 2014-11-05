package com.bddinaction.flyinghigh.jbehave.model;

public enum FrequentFlyerMember {

    Jane("jane@acme.com","Jane","Smith","s3cr3t"), Joe("joe@acme.com","Joe","Bloggs","s3cr3t2"), Bill("bill@acme.com","Bill","Smith","s3cr3t");

    private final String email;
    private final String firstName;
    private final String lastName;
    private final String password;

    private FrequentFlyerMember(String email, String firstName, String lastName, String password) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPassword() {
        return password;
    }
}
