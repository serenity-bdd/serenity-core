package net.thucydides.model.reports.adaptors.xunit.model;

/**
 * Details about a skipped test
 */
public class Skip {
    private final String type;

    public Skip(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
