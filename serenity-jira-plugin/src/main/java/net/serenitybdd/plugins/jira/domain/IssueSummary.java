package net.serenitybdd.plugins.jira.domain;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

public class IssueSummary {

    public static final String PROJECT_KEY = "project";
    public static final String TYPE_ID_KEY = "id";
    public static final String TYPE_KEY = "issuetype";
    public static final String SUMMARY_KEY = "summary";
    public static final String DESCRIPTION_KEY = "description";
    public static final String FIELDS_KEY = "fields";
    public static final String COMMENTS_KEY = "comments";
    public static final String TRANSITIONS_KEY = "transitions";
    public static final String STATE_OPEN = "Open";
    public static final String STATE_RESOLVED = "Resolved";
    public static final String STATE_CLOSED = "Closed";

    private URI self;
    private Long id;
    private String key;
    private String summary;
    private String description;
    private String type;
    private String status;
    private List<String> labels;
    private List<String> fixVersions;
    private Map<String, Object> customFieldValues;
    private Map<String, String> renderedFieldValues;
    private String project;
    private String reporter;

    private List<IssueComment> comments;

    public IssueSummary(){

    }

    public IssueSummary(URI self, Long id, String key, String summary, String description, Map<String, String> renderedFieldValues, String type, String status) {
        this(self, id, key, summary, description, renderedFieldValues, type, status,
                new ArrayList<String>(), new ArrayList<String>(), new HashMap<String, Object>());
    }

    public IssueSummary(URI self, Long id, String key, String summary, String description, Map<String, String> renderedFieldValues,
                        String type, String status, List<String> labels, List<String> fixVersions, Map<String, Object> customFields) {
        this.self = self;
        this.id = id;
        this.key = key;
        this.summary = summary;
        this.description = description;
        this.renderedFieldValues = renderedFieldValues;
        this.type = type;
        this.status = status;
        this.labels = ImmutableList.copyOf(labels);
        this.fixVersions = ImmutableList.copyOf(fixVersions);
        this.customFieldValues = ImmutableMap.copyOf(customFields);
    }

    public IssueSummary(URI self, Long id, String key, String summary, String description, Map<String, String> renderedFieldValues,
                        String type, String status, List<String> labels, List<String> fixVersions, Map<String, Object> customFields,
                        List<IssueComment> comments) {
        this.self = self;
        this.id = id;
        this.key = key;
        this.summary = summary;
        this.description = description;
        this.renderedFieldValues = renderedFieldValues;
        this.type = type;
        this.status = status;
        this.labels = ImmutableList.copyOf(labels);
        this.fixVersions = ImmutableList.copyOf(fixVersions);
        this.customFieldValues = ImmutableMap.copyOf(customFields);
        this.comments = ImmutableList.copyOf(comments);
    }

    public URI getSelf() {
        return self;
    }

    public Long getId() {
        return id;
    }

    public List<String> getLabels() {
        return labels;
    }

    public List<String> getFixVersions() {
        return fixVersions;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getReporter() {
        return reporter;
    }

    public void setReporter(String reporter) {
        this.reporter = reporter;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "IssueSummary{" +
                "key='" + key + '\'' +
                ", summary='" + summary + '\'' +
                '}';
    }

    public Optional<CustomFieldCast> customField(String fieldName) {
        if (customFieldValues.get(fieldName) == null) {
            return Optional.empty();
        } else {
            return Optional.of(new CustomFieldCast(customFieldValues.get(fieldName)));
        }
    }

    public RenderedView getRendered() {
        return new RenderedView(renderedFieldValues);
    }

    public static IssueSummary fromJsonString(String jsonIssueRepresentation) {
        JsonParser parser = new JsonParser();
        JsonObject jsonObject = parser.parse(jsonIssueRepresentation).getAsJsonObject();
        Long id = jsonObject.getAsJsonPrimitive("id").getAsLong();
        String key = jsonObject.getAsJsonPrimitive("key").getAsString();
        URI self = null;
        try {
            self = new URI(jsonObject.getAsJsonPrimitive("self").getAsString());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        String type = null;
		if (jsonObject.get("fields") != null && jsonObject.get("fields").getAsJsonObject().get("issuetype") != null
				&& jsonObject.get("fields").getAsJsonObject().get("issuetype").getAsJsonObject()
						.getAsJsonPrimitive("name") != null)
			type = jsonObject.get("fields").getAsJsonObject().get("issuetype").getAsJsonObject()
					.getAsJsonPrimitive("name").getAsString();
        return new IssueSummary(self,id, key, null,null, null, type, null);
    }

}
