package net.thucydides.core.model;

public class Rule {

    private String name;
    private String description;

    public Rule(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object other) {
        if(other == null) return false;
        if (! (other instanceof Rule)) return false;
        if(other == this) return true;
        return this.getName().equals(((Rule) other).getName());
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
