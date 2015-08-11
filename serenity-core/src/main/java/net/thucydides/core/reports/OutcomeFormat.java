package net.thucydides.core.reports;

public enum OutcomeFormat {

    XML(".xml"), JSON(".json"), HTML(".html");

    private String extension;

    private OutcomeFormat(String extension) {
        this.extension = extension;
    }

    public String getExtension() {
        return extension;
    }
}
