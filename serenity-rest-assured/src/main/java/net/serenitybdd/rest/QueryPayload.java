package net.serenitybdd.rest;

/**
 * Created by john on 18/05/2015.
 */
public class QueryPayload {
    private String content;
    private String contentType;


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
}
