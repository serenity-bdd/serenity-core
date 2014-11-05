package net.thucydides.core.reports.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.thucydides.core.model.features.ApplicationFeature;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;


@JsonIgnoreProperties({"name","reportName","featureClass","featureId"})
@JsonInclude(NON_NULL)
public abstract class JSONApplicationFeatureMixin {
    JSONApplicationFeatureMixin(@JsonProperty("id") String id,
                                @JsonProperty("storyName") final String storyName,
                                @JsonProperty("storyClassName") final String storyClassName,
                                @JsonProperty("path") final String path,
                                @JsonProperty("feature") final ApplicationFeature feature) {};

}
