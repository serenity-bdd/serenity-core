package net.thucydides.core.reports.json.jackson;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties({"shortName"})
public abstract class JSONTestTagMixin {
    JSONTestTagMixin(@JsonProperty("name") String id,
                     @JsonProperty("type") final String name) {};

}
