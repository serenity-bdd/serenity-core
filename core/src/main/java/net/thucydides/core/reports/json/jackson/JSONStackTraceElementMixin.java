package net.thucydides.core.reports.json.jackson;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties({"nativeMethod"})
public abstract class JSONStackTraceElementMixin {
    JSONStackTraceElementMixin(@JsonProperty("declaringClass") String declaringClass,
                               @JsonProperty("methodName") String methodName,
                               @JsonProperty("fileName") String fileName,
                               @JsonProperty("fileName") int lineNumber) {}
}
