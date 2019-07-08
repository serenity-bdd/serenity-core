package net.serenitybdd.core.tags;

import com.google.common.base.Splitter;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.model.TestTag;
import net.thucydides.core.util.EnvironmentVariables;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EnvironmentDefinedTags {

    public static List<TestTag> definedIn(EnvironmentVariables environmentVariables) {
        String tagListValue = environmentVariables.getProperty(ThucydidesSystemProperty.TAGS);
        if (StringUtils.isNotEmpty(tagListValue)) {
            List<String> tagList = Splitter.on(",").trimResults().splitToList(tagListValue);
            return tagList.stream()
                    .map(TestTag::withValue)
                    .collect(Collectors.toList());
        } else {
            return new ArrayList<>();
        }
    }
}
