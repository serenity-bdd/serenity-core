package net.serenitybdd.junit5.datadriven.samples;

public enum Books {

    BDD_IN_ACTION("BDD in Action"),
    JUNIT_IN_ACTION("JUnit in Action"),
    SPRING_IN_ACTION("Spring in Action");

    private final String bookTitle;

    Books(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public String value() {
        return bookTitle;
    }
}
