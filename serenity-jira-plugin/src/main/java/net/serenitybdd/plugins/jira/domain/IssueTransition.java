package net.serenitybdd.plugins.jira.domain;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.text.ParseException;

public class IssueTransition {

    public static final String ID_KEY = "id";
    public static final String NAME_KEY = "name";
    public static final String TRANSITION_KEY = "transition";
    public static final String REOPEN_ISSUE = "Reopen Issue";
    public static final String RESOLVE_ISSUE = "Resolve Issue";

    private String name;
    private String id;

    public IssueTransition(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public static IssueTransition fromJsonString(String jsonTransitionRepresentation) throws ParseException {
        JsonParser parser = new JsonParser();
        JsonObject currentComment = parser.parse(jsonTransitionRepresentation).getAsJsonObject();
        String id = currentComment.getAsJsonPrimitive(ID_KEY).getAsString();
        String name = currentComment.getAsJsonPrimitive(NAME_KEY).getAsString();
        return new IssueTransition(id,name);
    }
}
