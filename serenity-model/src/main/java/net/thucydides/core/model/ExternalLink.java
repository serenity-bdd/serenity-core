package net.thucydides.core.model;

public class ExternalLink {
    private String url;
    private String type;

    public ExternalLink() {}

    public ExternalLink(String url, String type) {
        this.url = url;
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public String getType() {
        return type;
    }
}
