package net.thucydides.core.tags;

import ch.lambdaj.function.convert.Converter;
import net.thucydides.core.annotations.WithTag;
import net.thucydides.core.model.TestTag;
import org.apache.commons.lang3.StringUtils;

public class TagConverters {

    public static Converter<Object, TestTag> toTestTags() {
        return new Converter<Object, TestTag>() {

            public TestTag convert(Object tag) {
                return convertToTestTag((WithTag) tag);
            }
        };
    }

    public static Converter<Object, TestTag> fromStringValuesToTestTags() {
        return new Converter<Object, TestTag>() {

            public TestTag convert(Object tagValue) {
                return TestTag.withValue((String) tagValue);
            }
        };
    }

    private static TestTag convertToTestTag(WithTag withTag) {
        if (StringUtils.isEmpty(withTag.value())) {
            return TestTag.withName(withTag.name()).andType(withTag.type());
        } else {
            return TestTag.withValue(withTag.value());
        }
    }
}