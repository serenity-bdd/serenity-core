package net.serenitybdd.demos.todos.screenplay.model;

public class ApplicationInformation {
    private final String title;
    private final String heading;
    private final String about;

    public ApplicationInformation(String title, String heading, String about) {
        this.title = title;
        this.heading = heading;
        this.about = about;
    }

    public String getTitle() {
        return title;
    }

    public String getHeading() {
        return heading;
    }

    public String getAbout() {
        return about;
    }

    @Override
    public String toString() {
        return String.format("{title='%s', heading='%s', about='%s'}", title, heading, about);
    }
}
