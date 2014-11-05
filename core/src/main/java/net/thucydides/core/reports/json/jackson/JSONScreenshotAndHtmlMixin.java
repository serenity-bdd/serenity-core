package net.thucydides.core.reports.json.jackson;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@JsonInclude(NON_NULL)

@JsonIgnoreProperties({"screenshotFile","sourcecode"})
public abstract class JSONScreenshotAndHtmlMixin {
    JSONScreenshotAndHtmlMixin(@JsonProperty("screenshot") String screenshot,
                               @JsonProperty("sourcecode") String sourcecode) {};

    @JsonProperty("screenshot")
    public abstract String getScreenshotName();

    @JsonProperty("htmlSource")
    public abstract String getHtmlSourceName();

}