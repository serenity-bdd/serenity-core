package net.thucydides.core.reports.json.jackson;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.thucydides.core.model.features.ApplicationFeature;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;


@JsonIgnoreProperties({"name","reportName","featureClass","featureId"})
@JsonInclude(NON_EMPTY)
public abstract class JSONStoryMixin {
    JSONStoryMixin(@JsonProperty("id") String id,
                   @JsonProperty("storyName") final String storyName,
                   @JsonProperty("storyClassName") final String storyClassName,
                   @JsonProperty("path") final String path,
                   @JsonProperty("feature") final ApplicationFeature feature) {};

}
