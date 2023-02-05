package net.serenitybdd.plugins.jira.domain;


import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;


public class IssueComment {

    public static final String BODY_KEY = "body";
    public static final String SELF_KEY = "self";
    public static final String AUTHOR_KEY = "author";
    public static final String UPDATE_AUTHOR_KEY = "author";
    public static final String CREATED_KEY = "created";
    public static final String UPDATED_KEY = "updated";

    private String author;

    private String body;

    private Calendar created;

    private String groupLevel;

    private Long id;

    private String roleLevel;

    private String updateAuthor;

    private String self;

    private Calendar updated;

    public IssueComment() {
    }

    public IssueComment(String body) {
        this.body = body;
    }


    public IssueComment(String self, Long id, String body, String author) {
        this.self = self;
        this.id = id;
        this.body = body;
        this.author = author;
    }

    public IssueComment(
            String self,
            String author,
            String body,
            Calendar created,
            String groupLevel,
            Long id,
            String roleLevel,
            String updateAuthor,
            Calendar updated) {
        this.self = self;
        this.author = author;
        this.body = body;
        this.created = created;
        this.groupLevel = groupLevel;
        this.id = id;
        this.roleLevel = roleLevel;
        this.updateAuthor = updateAuthor;
        this.updated = updated;
    }


    /**
     * Gets the author value for this Comment.
     *
     * @return author
     */
    public String getAuthor() {
        return author;
    }


    /**
     * Sets the author value for this Comment.
     */
    public void setAuthor(String author) {
        this.author = author;
    }


    /**
     * Gets the body value for this Comment.
     */
    public String getBody() {
        return body;
    }


    /**
     * Sets the body value for this Comment.
     */
    public void setBody(String body) {
        this.body = body;
    }


    /**
     * Gets the created value for this Comment.
     */
    public Calendar getCreated() {
        return created;
    }


    /**
     * Sets the created value for this Comment.
     */
    public void setCreated(Calendar created) {
        this.created = created;
    }


    /**
     * Gets the groupLevel value for this Comment.
     */
    public String getGroupLevel() {
        return groupLevel;
    }


    /**
     * Sets the groupLevel value for this Comment.
     */
    public void setGroupLevel(String groupLevel) {
        this.groupLevel = groupLevel;
    }


    /**
     * Gets the id value for this Comment.
     */
    public Long getId() {
        return id;
    }


    /**
     * Sets the id value for this Comment.
     */
    public void setId(Long id) {
        this.id = id;
    }


    /**
     * Gets the roleLevel value for this Comment.
     */
    public String getRoleLevel() {
        return roleLevel;
    }


    /**
     * Sets the roleLevel value for this Comment.
     */
    public void setRoleLevel(String roleLevel) {
        this.roleLevel = roleLevel;
    }


    /**
     * Gets the updateAuthor value for this Comment.
     */
    public String getUpdateAuthor() {
        return updateAuthor;
    }


    /**
     * Sets the updateAuthor value for this Comment.
     */
    public void setUpdateAuthor(String updateAuthor) {
        this.updateAuthor = updateAuthor;
    }


    /**
     * Gets the updated value for this Comment.
     */
    public Calendar getUpdated() {
        return updated;
    }

    public String getSelf() {
        return self;
    }

    public IssueComment withText(String text) {
        return new IssueComment(self, id, text, author);
    }

    public IssueComment withAuthor(String author) {
        return new IssueComment(self, id, body, author);
    }

    public static IssueComment fromJsonString(String jsonCommentRepresentation) throws ParseException{
        JsonParser parser = new JsonParser();
        JsonObject currentComment = parser.parse(jsonCommentRepresentation).getAsJsonObject();
        JsonObject authorJsonObject = currentComment.getAsJsonObject(AUTHOR_KEY);
        Author author = Author.fromJsonString(authorJsonObject.toString());
        String self = currentComment.getAsJsonPrimitive(SELF_KEY).getAsString();
        String body = currentComment.getAsJsonPrimitive(BODY_KEY).getAsString();
        JsonObject updateAuthorJsonObject = currentComment.getAsJsonObject(UPDATE_AUTHOR_KEY);
        Author updateAuthor = Author.fromJsonString(updateAuthorJsonObject.toString());
        String createdDate = currentComment.getAsJsonPrimitive(CREATED_KEY).getAsString();
        String updatedDate = currentComment.getAsJsonPrimitive(UPDATED_KEY).getAsString();
        Calendar createdCalendar =  GregorianCalendar.getInstance();
        //2015-06-29T10:10:43.947+0200
        createdCalendar.setTime(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").parse(createdDate));
        Calendar updatedCalendar =  GregorianCalendar.getInstance();
        updatedCalendar.setTime(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").parse(updatedDate));
        return new IssueComment(self, author.getAccountId(),body,createdCalendar , "", 0L,"",updateAuthor.getAccountId(),updatedCalendar);
    }
}