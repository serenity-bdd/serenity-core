package net.serenitybdd.model.tags;

import com.google.common.base.Splitter;
import net.thucydides.model.domain.TestTag;
import net.thucydides.model.util.EnvironmentVariables;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static net.thucydides.model.ThucydidesSystemProperty.TAGS;

public class EnvironmentDefinedTags {

    private static final List<TestTag> NO_TAGS = new ArrayList<>();
    public static List<TestTag> definedIn(EnvironmentVariables environmentVariables) {
        String tagListValue = TAGS.from(environmentVariables);
        if (StringUtils.isNotEmpty(tagListValue)) {
            List<String> tagList = Splitter.on(",").trimResults().splitToList(tagListValue);
            return tagList.stream()
                    .map(TestTag::withValue)
                    .collect(Collectors.toList());
        } else {
            return NO_TAGS;
        }
    }
}
