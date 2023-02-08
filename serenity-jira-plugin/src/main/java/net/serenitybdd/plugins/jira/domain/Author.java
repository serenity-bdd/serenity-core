package net.serenitybdd.plugins.jira.domain;


import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Author {

    public static final String SELF_KEY = "self";
    public static final String ACCOUNT_ID_KEY = "accountId";
    public static final String DISPLAY_NAME_KEY = "displayName";
    public static final String ACTIVE_KEY = "active";

    private String self;
    private String accountId;
    private String displayName;
    private boolean active;

    public Author(String self, String accountId, String displayName, boolean active) {
        this.self = self;
        this.accountId = accountId;
        this.displayName = displayName;
        this.active = active;
    }

    public static Author fromJsonString(String jsonIssueRepresentation) {
        JsonObject authorJson = new JsonParser().parse(jsonIssueRepresentation).getAsJsonObject();
        String self = authorJson.getAsJsonPrimitive(SELF_KEY).getAsString();
        String accountId = authorJson.getAsJsonPrimitive(ACCOUNT_ID_KEY).getAsString();
        String displayName = authorJson.getAsJsonPrimitive(DISPLAY_NAME_KEY).getAsString();
        boolean active = authorJson.getAsJsonPrimitive(ACTIVE_KEY).getAsBoolean();
        return new Author(self, accountId, displayName, active);
    }

    public String getSelf() {
        return self;
    }

    public void setSelf(String self) {
        this.self = self;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
