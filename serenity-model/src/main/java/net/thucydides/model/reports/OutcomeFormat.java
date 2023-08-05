package net.thucydides.model.reports;

public enum OutcomeFormat {

    XML(".xml"), JSON(".json"), HTML(".html");

    private String extension;

    OutcomeFormat(String extension) {
        this.extension = extension;
    }

    public String getExtension() {
        return extension;
    }
}
