package net.thucydides.core.tags;

import net.thucydides.core.annotations.WithTag;
import net.thucydides.core.model.TestTag;
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