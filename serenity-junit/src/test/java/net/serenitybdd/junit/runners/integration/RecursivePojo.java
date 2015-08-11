package net.serenitybdd.junit.runners.integration;

/**
 * Created by john on 11/06/2015.
 */
public class RecursivePojo {
    private RecursivePojo parent;
    private String name;

    public RecursivePojo(RecursivePojo parent, String name) {
        this.parent = parent;
        this.name = name;
    }

    public RecursivePojo getParent() {
        return parent;
    }

    public void setParent(RecursivePojo parent) {
        this.parent = parent;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        final String TAB = "    ";

        StringBuilder retValue = new StringBuilder();

        retValue.append("RecursivePojo ( ").append("intField = ")
                .append(name).append(TAB).append("parent = ")
                .append(parent).append(TAB).append(" )");

        return retValue.toString();
    }
}
