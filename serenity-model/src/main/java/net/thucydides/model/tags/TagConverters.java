package net.thucydides.model.tags;

import net.serenitybdd.annotations.WithTag;
import net.thucydides.model.domain.TestTag;
import org.apache.commons.lang3.StringUtils;

public class TagConverters {

    public static TestTag convertToTestTag(WithTag withTag) {
        if (StringUtils.isEmpty(withTag.value())) {
            return TestTag.withName(withTag.name()).andType(withTag.type());
        } else {
            return TestTag.withValue(withTag.value());
        }
    }
}
