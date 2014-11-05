package net.thucydides.junit.runners;

import org.junit.Test;

public class PersonTestScenario {

    private String name;
    private String address;
    private String phone;
    private String dateOfBirth;


    /**
     * Use the toString() method as a qualifier for the data sets.
     */
    public String toString() {
        return name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    @Test
    public void test_something_about_people() {
    }
}
