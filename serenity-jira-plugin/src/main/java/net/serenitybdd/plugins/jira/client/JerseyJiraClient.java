package net.serenitybdd.plugins.jira.client;

import com.beust.jcommander.internal.Maps;
import com.google.common.base.Preconditions;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.gson.*;
import net.serenitybdd.plugins.jira.domain.*;
import net.serenitybdd.plugins.jira.model.CascadingSelectOption;
import net.serenitybdd.plugins.jira.model.CustomField;
import net.serenitybdd.plugins.jira.model.JQLException;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.ExecutionException;

import static java.util.Collections.EMPTY_LIST;

/**
 * A JIRA client using the new REST interface
 */
@SuppressWarnings("unchecked")
public class JerseyJiraClient {

    private static final String ADD_COMMENT = "rest/api/latest/issue/%s/comment";
    private static final String UPDATE_COMMENT = "rest/api/latest/issue/%s/comment/%s";
    private static final String REST_SEARCH = "rest/api/latest/search";
    private static final String VERSIONS_SEARCH = "rest/api/latest/project/%s/versions";
    private static final String ISSUE = "rest/api/latest/issue/";
    private static final String PROJECT = "rest/api/latest/project";
    private static final String GET_TRANSITIONS = "rest/api/latest/issue/%s/transitions";

    private static final int REDIRECT_REQUEST = 302;
    private static final String DEFAULT_ISSUE_TYPE = "Bug";
    private static final int WITH_NO_BATCHES = 0;
    private final String url;
    private final String username;
    private final String password;
    private final int batchSize;
    private final String project;
    private final List<String> customFields;
    private Map<String, CustomField> customFieldsIndex;
    private Map<String, String> customFieldNameIndex;
    private String metadataIssueType;
    private LoadingCache<String, Optional<IssueSummary>> issueSummaryCache;

    private Map<LoadingStrategy, LoadingCache<String, List<IssueSummary>>> issueQueryCachePerStrategy;

    private final Logger logger = LoggerFactory.getLogger(JerseyJiraClient.class);

    private final static int DEFAULT_BATCH_SIZE = 100;
    private final static int OK = 200;
    private final static int CREATE_ISSUE_OK = 201;
    private final static int DELETE_ISSUE_OK = 204;


    public JerseyJiraClient(String url, String username, String password, String project) {
        this(url, username, password, DEFAULT_BATCH_SIZE, project);
    }

    public JerseyJiraClient(String url, String username, String password, String project, List<String> customFields) {
        this(url, username, password, DEFAULT_BATCH_SIZE, project, DEFAULT_ISSUE_TYPE, customFields);
    }


    public JerseyJiraClient(String url, String username, String password, int batchSize,
                            String project,
                            String metadataIssueType,
                            List<String> customFields) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.batchSize = batchSize;
        this.project = project;
        this.metadataIssueType = metadataIssueType;
        this.customFields = ImmutableList.copyOf(customFields);
        this.issueSummaryCache = CacheBuilder.newBuilder()
                .maximumSize(1000)
                .build(new FindByKeyLoader(this));

        LoadingCache<String, List<IssueSummary>> issueQueryCache = CacheBuilder.newBuilder()
                .maximumSize(1000)
                .build(new FindByJQLLoader(this));

        LoadingCache<String, List<IssueSummary>> batchedIssueQueryCache = CacheBuilder.newBuilder()
                .maximumSize(1000)
                .build(new FindByJQLLoaderUsingBatches(this));

        this.issueQueryCachePerStrategy = ImmutableMap.of(
                LoadingStrategy.LOAD_IN_SINGLE_QUERY, issueQueryCache,
                LoadingStrategy.LOAD_IN_BATCHES, batchedIssueQueryCache);

    }

    public JerseyJiraClient(String url, String username, String password, int batchSize, String project) {
        this(url, username, password, batchSize, project, DEFAULT_ISSUE_TYPE, EMPTY_LIST);
    }

    public JerseyJiraClient usingCustomFields(List<String> customFields) {
        return new JerseyJiraClient(url, username, password, batchSize, project, metadataIssueType, customFields);
    }

    public JerseyJiraClient usingMetadataIssueType(String metadataIssueType) {
        return new JerseyJiraClient(url, username, password, batchSize, project, metadataIssueType, customFields);
    }

    public List<IssueSummary> findByJQL(String query, LoadingStrategy loadingStrategy) throws JQLException {
        try {
            Preconditions.checkNotNull(query, "JIRA key cannot be null");
            return issueQueryCachePerStrategy.get(loadingStrategy).get(query);
        } catch (ExecutionException e) {
            throw new JQLException(e.getCause());
        } catch (RuntimeException runtimeException) {
            throw new JQLException(runtimeException.getCause());
        }
    }


    /**
     * Load the issue keys for all of the issues matching the specified JQL query
     *
     * @param query A valid JQL query
     * @return a list of JIRA issue keys
     */
    public List<IssueSummary> findByJQL(String query) throws JQLException {
        return findByJQL(query, LoadingStrategy.LOAD_IN_SINGLE_QUERY);
    }

    protected List<IssueSummary> loadByJQLBatches(String query) {
        int total = countByJQL(query);
        List<IssueSummary> issues = new ArrayList<>();
        int startAt = 0;
        while (issues.size() < total) {
            String jsonResponse = getJSONResponse(query, startAt, batchSize);
            JsonObject responseObject = new JsonParser().parse(jsonResponse).getAsJsonObject();
            JsonArray issueEntries = (JsonArray) responseObject.get("issues");
            for (int i = 0; i < issueEntries.size(); i++) {
                JsonObject issueObject = issueEntries.get(i).getAsJsonObject();
                issues.add(convertToIssueSummary(issueObject));
            }
            startAt = startAt + getBatchSize();
        }
        return issues;
    }

    protected List<IssueSummary> loadByJQL(String query) {
        List<IssueSummary> issues = new ArrayList<>();
        String jsonResponse = getJSONResponse(query, 0, WITH_NO_BATCHES);
        JsonObject responseObject = new JsonParser().parse(jsonResponse).getAsJsonObject();
        JsonArray issueEntries = (JsonArray) responseObject.get("issues");
        if (issueEntries != null) {
            for (int i = 0; i < issueEntries.size(); i++) {
                JsonObject issueObject = issueEntries.get(i).getAsJsonObject();
                issues.add(convertToIssueSummary(issueObject));
            }
        }
        return issues;
    }


    public List<Version> findVersionsForProject(String projectName) {
        String versionData = getJSONProjectVersions(projectName);
        return convertJSONVersions(versionData);
    }

    private List<Version> convertJSONVersions(String versionData) {
        List<Version> versions = new ArrayList<>();
        JsonArray versionEntries = new JsonParser().parse(versionData).getAsJsonArray();
        for (int i = 0; i < versionEntries.size(); i++) {
            JsonObject issueObject = versionEntries.get(i).getAsJsonObject();
            versions.add(convertToVersion(issueObject));
        }
        return versions;
    }

    public WebTarget buildWebTargetFor(String path) {
        return restClient().target(url).path(path);
    }

    private String getJSONResponse(String query, int startAt, int batchSize) {

        String fields = "key,status,summary,description,comment,issuetype,labels,fixVersions";
        fields = addCustomFieldsTo(fields);

        WebTarget target = buildWebTargetFor(REST_SEARCH)
                .queryParam("jql", query)
                .queryParam("startAt", startAt)
                .queryParam("expand", "renderedFields")
                .queryParam("fields", fields);

        if (batchSize > 0) {
            target = target.queryParam("maxResults", batchSize);
        }
        Response response = target.request().get();
        checkValid(response);
        return response.readEntity(String.class);
    }

    private String addCustomFieldsTo(String fields) {

        for (String customField : customFields) {
            if (getCustomFieldsIndex().containsKey(customField)) {
                fields = fields + "," + getCustomFieldsIndex().get(customField).getId();
            }
        }
        return fields;
    }

    private String getJSONProjectVersions(String projectName) {
        String url = String.format(VERSIONS_SEARCH, projectName);
        WebTarget target = buildWebTargetFor(url);
        Response response = target.request().get();
        checkValid(response);
        return response.readEntity(String.class);
    }

    public java.util.Optional<IssueSummary> findByKey(String key) throws JQLException {
        try {
            Preconditions.checkNotNull(key, "JIRA key cannot be null");
            return issueSummaryCache.get(key);
        } catch (ExecutionException e) {
            throw new JQLException(e.getCause());
        } catch (RuntimeException runtimeException) {
            throw new JQLException(runtimeException.getCause());
        }
    }

    public Optional<IssueSummary> loadByKey(String key) {

        Optional<String> jsonResponse = readFieldValues(url, ISSUE + key);
        if (jsonResponse.isPresent()) {
            JsonObject responseObject = new JsonParser().parse(jsonResponse.get()).getAsJsonObject();
            return Optional.of(convertToIssueSummary(responseObject));
        }
        return Optional.empty();
    }

    private Version convertToVersion(JsonObject issueObject) {
        return new Version(uriFrom(issueObject),
                issueObject.getAsJsonPrimitive("id").getAsLong(),
                stringValueOf(issueObject.get("name")),
                booleanValueOf(issueObject.get("archived")),
                booleanValueOf(issueObject.get("released")));
    }

    private IssueSummary convertToIssueSummary(JsonObject issueObject) {

        JsonObject fields = (JsonObject) issueObject.get("fields");
        JsonObject renderedFields = (JsonObject) issueObject.get("renderedFields");
        JsonObject issueType = (JsonObject) fields.get("issuetype");
        JsonObject issueStatus = (JsonObject) fields.get("status");
        JsonObject comments = (JsonObject) fields.get("comment");
        Map<String, String> renderedFieldValues = renderedFieldValuesFrom(renderedFields);
        return new IssueSummary(uriFrom(issueObject),
                issueObject.getAsJsonPrimitive("id").getAsLong(),
                stringValueOf(issueObject.get("key")),
                stringValueOf(fields.get("summary")),
                stringValueOf(optional(fields, "description")),
                renderedFieldValues,
                stringValueOf(issueType.get("name")),
                stringValueOf(issueStatus.get("name")),
                toList((JsonArray) fields.get("labels")),
                toListOfVersions((JsonArray) fields.get("fixVersions")),
                customFieldValuesIn(fields, renderedFields),
                commentsIn(comments));

    }

    private List<IssueComment> commentsIn(JsonObject comments) {
        List<IssueComment> issueComments = Lists.newArrayList();

        JsonArray commentList = comments.getAsJsonArray("comments");

        for (int i = 0; i < commentList.size(); i++) {
            JsonObject fieldObject = commentList.get(i).getAsJsonObject();
            issueComments.add(convertToComment(fieldObject));
        }

        return issueComments;
    }

    private IssueComment convertToComment(JsonObject fieldObject) {
        return new IssueComment(fieldObject.getAsJsonPrimitive("self").getAsString(),
                fieldObject.getAsJsonPrimitive("id").getAsLong(),
                fieldObject.getAsJsonPrimitive("body").getAsString(),
                fieldObject.getAsJsonObject("author").getAsJsonPrimitive("accountId").getAsString());
    }

    private Map<String, String> renderedFieldValuesFrom(JsonObject renderedFields) {
        Map<String, String> renderedFieldMap = Maps.newHashMap();
        Set<Map.Entry<String, JsonElement>> entries = renderedFields.entrySet();
        for (Map.Entry<String, JsonElement> currentEntry : entries) {
            String fieldName = currentEntry.getKey();
            JsonElement element = currentEntry.getValue();
            if (!(element.isJsonNull())) {
                String renderedValue = "";
                if (element.isJsonPrimitive()) {
                    renderedValue = element.getAsJsonPrimitive().getAsString();
                } else if (element.isJsonObject()) {
                    renderedValue = element.toString();
                }
                if (getCustomFieldNameIndex().containsKey(fieldName)) {
                    fieldName = getCustomFieldNameIndex().get(currentEntry.getKey());
                }
                renderedFieldMap.put(fieldName, renderedValue);
            }
        }
        return renderedFieldMap;
    }

    private Map<String, Object> customFieldValuesIn(JsonObject fields, JsonObject renderedFields) {
        Map<String, Object> customFieldValues = Maps.newHashMap();
        for (String customFieldName : customFields) {
            CustomField customField = getCustomFieldsIndex().get(customFieldName);
            if (customFieldDefined(fields, renderedFields, customField)) {
                Object customFieldValue = readFieldValue(fields, customField);
                customFieldValues.put(customFieldName, customFieldValue);
            }
        }
        return customFieldValues;
    }

    private boolean customFieldDefined(JsonObject fields, JsonObject renderedFields, CustomField customField) {
        if (customField != null) {
            return (hasCustomFieldValue(fields, customField) || hasCustomFieldValue(renderedFields, customField));
        } else {
            return false;
        }
    }

    private boolean hasCustomFieldValue(JsonObject fields, CustomField customField) {
        return (fields.has(customField.getId())) && (!fields.get(customField.getId()).equals(null));
    }

    private Object readFieldValue(JsonObject fields, CustomField customField) {

        String fieldId = customField.getId();
        String fieldValue = "";
        if (fieldIsDefined(fields, fieldId)) {
            if (fields.get(fieldId).isJsonPrimitive()) {
                fieldValue = fields.getAsJsonPrimitive(fieldId).getAsString();
            } else if (fields.get(fieldId).isJsonObject()) {
                fieldValue = fields.getAsJsonObject(fieldId).toString();
            }
        }

        if (isJSON(fieldValue)) {
            JsonObject field = new JsonParser().parse(fieldValue).getAsJsonObject();

            if (customField.getType().equals("string") || customField.getType().equals("option")) {
                return field.equals(JsonNull.INSTANCE) ? "" : field.getAsJsonPrimitive("value").getAsString();
            } else if (customField.getType().equals("array") || customField.getType().equals("option-with-child")) {
                return readListFrom(field);
            }
        }
        return fieldValue;
    }

    private boolean fieldIsDefined(JsonObject fields, String fieldId) {
        return (fields.has(fieldId) && (!fields.get(fieldId).isJsonNull()));
    }

    private boolean isJSON(String fieldValue) {
        return fieldValue.trim().startsWith("{");
    }

    private List<String> readListFrom(JsonObject jsonField) {
        List<String> values = Lists.newArrayList();
        values.add(jsonField.getAsJsonPrimitive("value").getAsString());
        if (jsonField.has("child")) {
            values.addAll(readListFrom(jsonField.getAsJsonObject("child")));
        }
        return values;
    }

    private List<CustomField> convertToCustomFields(JsonArray customFieldsList) {

        List<CustomField> customFields = Lists.newArrayList();

        for (int i = 0; i < customFieldsList.size(); i++) {
            JsonObject fieldObject = customFieldsList.get(i).getAsJsonObject();
            customFields.add(convertToCustomField(fieldObject));
        }
        return customFields;

    }

    private CustomField convertToCustomField(JsonObject fieldObject) {
        return new CustomField(fieldObject.getAsJsonPrimitive("id").getAsString(),
                fieldObject.getAsJsonPrimitive("name").getAsString(),
                fieldTypeOf(fieldObject));
    }

    private String fieldTypeOf(JsonObject fieldObject) {
        if (fieldObject.has("schema")) {
            return fieldObject.getAsJsonObject("schema").getAsJsonPrimitive("type").getAsString();
        } else {
            return "string";
        }
    }

    private JsonElement optional(JsonObject fields, String fieldName) {
        return (fields.has(fieldName) ? fields.get(fieldName) : null);
    }

    private List<String> toList(JsonArray array) {
        List<String> list = Lists.newArrayList();
        if (array == null) {
            return list;
        }
        for (int i = 0; i < array.size(); i++) {
            list.add(stringValueOf(array.get(i)));
        }
        return list;
    }

    private List<String> toListOfVersions(JsonArray array) {
        List<String> list = Lists.newArrayList();
        if (array == null) {
            return list;
        }
        for (int i = 0; i < array.size(); i++) {
            JsonObject versionObject = (JsonObject) array.get(i);
            list.add(versionObject.getAsJsonPrimitive("name").getAsString());
        }
        return list;
    }

    private URI uriFrom(JsonObject issueObject) {
        try {
            return new URI(issueObject.getAsJsonPrimitive("self").getAsString());
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Self field not a valid URL");
        }
    }

    public Integer countByJQL(String query) {
        WebTarget target = buildWebTargetFor(REST_SEARCH).queryParam("jql", query).queryParam("maxResults", 0);
        Response response = target.request().get();

        if (isEmpty(response)) {
            return 0;
        } else {
            checkValid(response);
        }

        String jsonResponse = response.readEntity(String.class);

        int total;

        JsonObject responseObject = new JsonParser().parse(jsonResponse).getAsJsonObject();
        total = responseObject.getAsJsonPrimitive("total").getAsInt();

        logger.debug("Count by JQL for {}", query);

        return total;
    }

    private Optional<String> readFieldValues(String url, String path) {
        WebTarget target = restClient().target(url)
                .path(path)
                .queryParam("expand", "renderedFields");

        Response response = target.request().get();

        if (response.getStatus() == REDIRECT_REQUEST) {
            response = Redirector.forPath(path).usingClient(restClient()).followRedirectsIn(response);
        }

        if (resourceDoesNotExist(response)) {
            return Optional.empty();
        } else {
            checkValid(response);
            return Optional.of(response.readEntity(String.class));
        }
    }

    private Optional<String> readFieldMetadata(String url, String path) {
        WebTarget target = restClient().target(url)
                .path(path)
                .queryParam("expand", "renderedFields")
                .queryParam("project", project)
                .queryParam("issuetypeName", metadataIssueType)
                .queryParam("expand", "projects.issuetypes.fields");

        Response response = target.request().get();

        if (response.getStatus() == REDIRECT_REQUEST) {
            response = Redirector.forPath(path).usingClient(restClient()).followRedirectsIn(response);
        }

        if (resourceDoesNotExist(response)) {
            return Optional.empty();
        } else {
            checkValid(response);
            return Optional.of(response.readEntity(String.class));
        }
    }

    public Client restClient() {
        return ClientBuilder.newBuilder()
                .register(HttpAuthenticationFeature.basic(username, password))
                .property(ClientProperties.FOLLOW_REDIRECTS, Boolean.TRUE)
                .build();
    }

    private String stringValueOf(JsonElement field) {
        if (field != null) {
            if (field.isJsonPrimitive()) {
                return field.getAsJsonPrimitive().getAsString();
            } else if (field.isJsonObject()) {
                return field.getAsJsonObject().toString();
            }
            return field.toString();
        } else {
            return null;
        }
    }

    private boolean booleanValueOf(JsonElement field) {
        if (field != null) {
            return Boolean.valueOf(field.toString());
        } else {
            return false;
        }
    }

    public boolean resourceDoesNotExist(Response response) {
        return response.getStatus() == 404;
    }

    public boolean isEmpty(Response response) {
        return response.getStatus() == 400;
    }

    public void checkValid(Response response) {
        int status = response.getStatus();
        if (status != OK && (status != CREATE_ISSUE_OK) && (status != DELETE_ISSUE_OK)) {
            switch (status) {
                case 400:
                    return;
                case 401:
                    handleAuthenticationError("Authentication error (401) for user " + this.username);
                case 403:
                    handleAuthenticationError("Forbidden error (403) for user " + this.username);
                case 404:
                    handleConfigurationError("Service not found (404) - try checking the JIRA URL?");
                case 407:
                    handleConfigurationError("Proxy authentication required (407)");
                default:
                    throw new JQLException("JIRA query failed: error " + status);
            }
        }
    }

    private void handleAuthenticationError(String message) {
        throw new JIRAAuthenticationError(message);
    }

    private void handleConfigurationError(String message) {
        throw new JIRAConfigurationError(message);
    }


    public int getBatchSize() {
        return batchSize;
    }

    private Map<String, CustomField> getCustomFieldsIndex() {
        if (customFieldsIndex == null) {
            customFieldsIndex = indexCustomFields();
        }
        return customFieldsIndex;
    }

    private Map<String, String> getCustomFieldNameIndex() {
        if (customFieldNameIndex == null) {
            customFieldNameIndex = indexCustomFieldNames();
        }
        return customFieldNameIndex;
    }


    private Map<String, String> indexCustomFieldNames() {
        Map<String, String> index = Maps.newHashMap();
        for (CustomField field : getExistingCustomFields()) {
            index.put(field.getId(), field.getName());
        }
        return index;
    }


    private Map<String, CustomField> indexCustomFields() {
        Map<String, CustomField> index = Maps.newHashMap();
        for (CustomField field : getExistingCustomFields()) {
            index.put(field.getName(), field);
        }
        return index;
    }

    private List<CustomField> getExistingCustomFields() {

        Optional<String> jsonResponse = readFieldValues(url, "rest/api/2/field");

        if (jsonResponse.isPresent()) {
            JsonArray responseObject = new JsonParser().parse(jsonResponse.get()).getAsJsonArray();
            return convertToCustomFields(responseObject);
        }
        return EMPTY_LIST;
    }

    List<CustomField> getCustomFields() {
        List<CustomField> registeredCustomFields = Lists.newArrayList();
        for (String fieldName : customFields) {
            registeredCustomFields.add(getCustomFieldsIndex().get(fieldName));
        }
        return registeredCustomFields;
    }

    public List<CascadingSelectOption> findOptionsForCascadingSelect(String fieldName) {
        JsonObject responseObject;
        final List<CascadingSelectOption> result = new ArrayList<>();
        Optional<String> jsonResponse = readFieldMetadata(url, "rest/api/2/issue/createmeta");
        if (jsonResponse.isPresent()) {
            responseObject = new JsonParser().parse(jsonResponse.get()).getAsJsonObject();
            JsonArray projects = responseObject.getAsJsonArray("projects");
            for (final JsonElement pr : projects) {
                final JsonObject project = pr.getAsJsonObject();
                final JsonArray issueTypes = project.getAsJsonArray("issuetypes");
                for (final JsonElement st : issueTypes) {
                    final JsonObject issueType = st.getAsJsonObject();
                    final JsonObject fields = issueType.getAsJsonObject("fields");
                    Iterator<Map.Entry<String, JsonElement>> fieldKeys = fields.entrySet().iterator();
                    while (fieldKeys.hasNext()) {
                        String entryFieldName = fieldKeys.next().getKey();
                        JsonObject entry = fields.getAsJsonObject(entryFieldName);
                        if (entry.getAsJsonPrimitive("name").getAsString().equalsIgnoreCase(fieldName)
                                && entry.getAsJsonArray("allowedValues") != null) {
                            result.addAll(convertToCascadingSelectOptions(entry.getAsJsonArray("allowedValues")));
                        }
                    }
                }
            }
        }
        return removeDuplicated(result);
    }

    private List<CascadingSelectOption> removeDuplicated(final List<CascadingSelectOption> options) {
        List<CascadingSelectOption> filtered = new LinkedList<>();
        Map<String, CascadingSelectOption> filter = new HashMap<>();
        for (final CascadingSelectOption option : options) {
            filter.put(identification(option).toString(), option);
        }
        filtered.addAll(filter.values());
        return filtered;
    }

    private StringBuilder identification(final CascadingSelectOption option) {
        StringBuilder builder = new StringBuilder(option.getOption());
        Map<String, CascadingSelectOption> filter = new HashMap<>();
        for (final CascadingSelectOption children : option.getNestedOptions()) {
            filter.put(identification(children).toString(), children);
        }
        for (final String key : filter.keySet()) {
            builder.append(key);
        }
        return builder;
    }

    private List<CascadingSelectOption> convertToCascadingSelectOptions(JsonArray allowedValues) {
        return convertToCascadingSelectOptions(allowedValues, null);
    }

    private List<CascadingSelectOption> convertToCascadingSelectOptions(JsonArray allowedValues,
                                                                        CascadingSelectOption parentOption) {
        List<CascadingSelectOption> options = Lists.newArrayList();
        for (int i = 0; i < allowedValues.size(); i++) {
            JsonObject entry = (JsonObject) allowedValues.get(i);
            String value = entry.getAsJsonPrimitive("value").getAsString();

            CascadingSelectOption option = new CascadingSelectOption(value, parentOption);
            List<CascadingSelectOption> children = Lists.newArrayList();
            if (entry.has("children")) {
                children = convertToCascadingSelectOptions(entry.getAsJsonArray("children"), option);
            }
            option.addChildren(children);
            options.add(option);
        }
        return options;
    }


    public IssueSummary createIssue(IssueSummary issue) {
        WebTarget target = restClient().target(url).path(ISSUE);
        JsonObject fields = new JsonObject();

        JsonObject project = new JsonObject();
        project.add(Project.KEY_KEY, new JsonPrimitive(issue.getProject()));
        fields.add(IssueSummary.PROJECT_KEY, project);

        JsonObject issueType = new JsonObject();
        issueType.add(IssueSummary.TYPE_ID_KEY, new JsonPrimitive(issue.getType()));
        fields.add(IssueSummary.TYPE_KEY, issueType);

        fields.add(IssueSummary.SUMMARY_KEY, new JsonPrimitive(issue.getSummary()));
        fields.add(IssueSummary.DESCRIPTION_KEY, new JsonPrimitive("Lorem ipsum..."));

        JsonObject jsonIssue = new JsonObject();
        jsonIssue.add(IssueSummary.FIELDS_KEY, fields);

        Response response = target.request().post(Entity.json(jsonIssue.toString()));
        checkValid(response);
        return IssueSummary.fromJsonString(response.readEntity(String.class));
    }

    public void deleteIssue(IssueSummary issue) throws Exception {
        WebTarget target = restClient().target(issue.getSelf());
        checkValid(target.request().delete());
    }

    public Project getProjectByKey(String projectKey) {
        WebTarget target = restClient().target(url).path(PROJECT + "/" + projectKey);
        Response response = target.request().get();
        checkValid(response);
        return Project.fromJsonString(response.readEntity(String.class));
    }

    public IssueSummary getIssue(String issueKey) {
        WebTarget target = restClient().target(url).path(ISSUE + issueKey);
        Response response = target.request().get();
        checkValid(response);
        return IssueSummary.fromJsonString(response.readEntity(String.class));
    }


    public void addComment(String issueKey, IssueComment newComment) {
        String url = String.format(ADD_COMMENT, issueKey);
        WebTarget target = buildWebTargetFor(url);
        JsonObject jsonComment = new JsonObject();
        jsonComment.add(IssueComment.BODY_KEY, new JsonPrimitive(newComment.getBody()));
        Response response = target.request().post(Entity.json(jsonComment.toString()));
        checkValid(response);
        issueSummaryCache.invalidate(issueKey);
    }

    public void updateComment(String key, IssueComment updatedComment) {
        WebTarget target = restClient().target(updatedComment.getSelf());

        Response response = target.request(MediaType.APPLICATION_JSON_TYPE).get();

        JsonObject jsonComment = new JsonParser().parse(response.readEntity(String.class)).getAsJsonObject();
        jsonComment.addProperty("body", updatedComment.getBody());

        target.request(MediaType.APPLICATION_JSON_TYPE).put(Entity.entity(jsonComment.toString(), MediaType.APPLICATION_JSON));
        issueSummaryCache.invalidate(key);
    }

    public List<IssueComment> getComments(String issueKey) throws ParseException {
        WebTarget target = restClient().target(url).path(ISSUE + issueKey + "/comment");
        Response response = target.request().get();
        checkValid(response);
        String jsonIssueRepresentation = response.readEntity(String.class);
        JsonParser parser = new JsonParser();
        JsonObject jsonObject = parser.parse(jsonIssueRepresentation).getAsJsonObject();
        JsonArray commentsArray = jsonObject.getAsJsonArray(IssueSummary.COMMENTS_KEY);
        List<IssueComment> comments = new ArrayList<IssueComment>();
        for (int i = 0; i < commentsArray.size(); i++) {
            JsonObject currentCommentJson = commentsArray.get(i).getAsJsonObject();
            comments.add(IssueComment.fromJsonString(currentCommentJson.toString()));
        }
        return comments;
    }

    public List<IssueTransition> getAvailableTransitions(String issueKey) throws ParseException {
        List<IssueTransition> availableActions = new ArrayList<IssueTransition>();
        WebTarget target = buildWebTargetFor(String.format(GET_TRANSITIONS, issueKey));
        Response response = target.request().get();
        checkValid(response);
        String jsonIssueRepresentation = response.readEntity(String.class);
        JsonParser parser = new JsonParser();
        JsonObject jsonObject = parser.parse(jsonIssueRepresentation).getAsJsonObject();
        JsonArray transitionsArray = jsonObject.getAsJsonArray(IssueSummary.TRANSITIONS_KEY);
        for (int i = 0; i < transitionsArray.size(); i++) {
            JsonObject currentTransitionJson = transitionsArray.get(i).getAsJsonObject();
            availableActions.add(IssueTransition.fromJsonString(currentTransitionJson.toString()));
        }
        return availableActions;
    }

    public void progressWorkflowTransition(String issueKey, String transitionId) throws ParseException {
        WebTarget target = buildWebTargetFor(String.format(GET_TRANSITIONS, issueKey));
        JsonObject jsonTransition = new JsonObject();
        jsonTransition.add(IssueTransition.TRANSITION_KEY, new JsonPrimitive(transitionId));
        Response response = target.request().post(Entity.json(jsonTransition.toString()));
        checkValid(response);
    }
}
