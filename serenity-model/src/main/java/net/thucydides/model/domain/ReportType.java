package net.thucydides.model.domain;

/**
 * The report namer knows how to find names for these types of reports
 */
public enum ReportType {
    /** report name with no suffix. */
    ROOT(""),

    /** XML reports. */
    XML("xml"),

    /** CSV files. */
    CSV("csv"),

    /** HTML reports. */
    HTML("html"),

    /** JSON reports. */
    JSON("json"),

    /** JUnit XML reports. */
    JUNIT("xml");

    private String suffix;

    ReportType(final String suffix) {
        this.suffix = suffix;
    }

    @Override
    public String toString() {
        return suffix;
    }
}
