package net.thucydides.core.reports.json.jackson;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.thucydides.core.model.TestResult;

import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;

@JsonIgnoreProperties({"stringValues"})
@JsonInclude(NON_EMPTY)
public abstract class JSONDataTableRowMixin {
    JSONDataTableRowMixin(@JsonProperty("values") List<? extends Object> values, @JsonProperty("result") TestResult result) {};

}
