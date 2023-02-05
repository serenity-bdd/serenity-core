package net.serenitybdd.plugins.jira.domain;


import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Project  {

    public static final String KEY_KEY = "key";

    private String description;

    private String lead;

    private String projectUrl;

    private String url;

    private String id;

    private String name;

    private String key;

    public Project(
            String id,
            String name,
            String description,
            String key,
            String lead,
            String projectUrl,
            String url) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.key = key;
        this.lead = lead;
        this.projectUrl = projectUrl;
        this.url = url;
    }

    public static Project fromJsonString(String jsonStringRepresentation) {
        JsonParser parser = new JsonParser();
        JsonObject jsonObject = parser.parse(jsonStringRepresentation).getAsJsonObject();
        System.out.println("JSON object"  + jsonObject) ;
        String id = jsonObject.getAsJsonPrimitive("id").getAsString();
        String name = jsonObject.getAsJsonPrimitive("name").getAsString();
        String description = jsonObject.getAsJsonPrimitive("description").getAsString();
        String key = jsonObject.getAsJsonPrimitive("key").getAsString();
        String lead = jsonObject.getAsJsonObject("lead").getAsJsonPrimitive("displayName").getAsString();
        //setUrl(jsonObject.getAsJsonPrimitive("url").getAsString());
        return new Project(id, name, description, key,lead,"","");
    }


    /**
     * Gets the description value for this Project.
     *
     * @return description
     */
    public String getDescription() {
        return description;
    }


    /**
     * Sets the description value for this Project.
     * @param description description value for this Project.
     */
    public void setDescription(String description) {
        this.description = description;
    }


    /**
     * Gets the lead value for this RemoteProject.
     * @return lead value for this RemoteProject.
     */
    public String getLead() {
        return lead;
    }


    /**
     * Gets the lead value for this RemoteProject.
     * @param lead value for this RemoteProject.
     */
    public void setLead(String lead) {
        this.lead = lead;
    }


    /**
     * Gets the projectUrl value for this RemoteProject.
     * @return projectUrl value for this RemoteProject.
     */
    public String getProjectUrl() {
        return projectUrl;
    }


    /**
     * Sets the projectUrl value for this RemoteProject.
     */
    /**
     * Sets the projectUrl value for this RemoteProject.
     * @param projectUrl value for this RemoteProject.
     */
    public void setProjectUrl(String projectUrl) {
        this.projectUrl = projectUrl;
    }


    /**
     * Gets the url value for this RemoteProject.
     * @return url value for this RemoteProject.
     */
    public String getUrl() {
        return url;
    }


    /**
     * Sets the url value for this RemoteProject.
     * @param url
     */
    public void setUrl(String url) {
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the key value for this Entity.
     * @return key value for this Entity.
     */
    public String getKey() {
        return key;
    }


    /**
     * Sets the key value for this Entity.
     * @param key value for this Entity
     */
    public void setKey(String key) {
        this.key = key;
    }

}
