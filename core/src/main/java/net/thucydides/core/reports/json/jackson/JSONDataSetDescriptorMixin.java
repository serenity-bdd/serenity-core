package net.thucydides.core.reports.json.jackson;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;

@JsonInclude(NON_EMPTY)
public abstract class JSONDataSetDescriptorMixin {
    JSONDataSetDescriptorMixin(@JsonProperty("startRow") int startRow,
                               @JsonProperty("rowCount") int rowCount,
                               @JsonProperty("name") String name,
                               @JsonProperty("description") String description) {};

}
