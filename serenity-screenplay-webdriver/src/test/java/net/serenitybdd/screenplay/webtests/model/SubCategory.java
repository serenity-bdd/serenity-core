package net.serenitybdd.screenplay.webtests.model;

public enum SubCategory {
    ADDCUSTOMER("Add Customer"),
    ADDSITE("Add Site");

    private String name;

    SubCategory(String name){
        this.name = name;
    }

    public String getName(){
        return this.name;
    }
}
