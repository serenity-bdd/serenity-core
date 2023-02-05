package net.serenitybdd.plugins.jira.model;


public class CustomField {
    private final String id;
    private final String name;
    private final String type;

    public CustomField(String id, String name, String type) {
        this.id = id;
        this.name = name;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }
}
