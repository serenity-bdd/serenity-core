package net.thucydides.core.util;

import net.thucydides.core.ThucydidesSystemProperty;
import org.junit.Before;
import org.junit.Test;

import static java.lang.String.format;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class TagInflectorTest {

    private static final String PROPERTY_UNDER_TEST = ThucydidesSystemProperty.REPORT_RAW_TAG_LIST.getPropertyName();
    private EnvironmentVariables environmentVariables;

    @Before
    public void setUp() {
        environmentVariables = new MockEnvironmentVariables();
    }

    @Test
    public void shouldKeepTagRaw() {
        environmentVariables.setProperty(PROPERTY_UNDER_TEST, "another,one,bites,the,dust");
        TagInflector tagInflector = new TagInflector(environmentVariables);
        String outputValue = tagInflector.ofTag("another", "tag_with_underscores").toFinalView();
        assertEquals(format("tag should not be formatted as it was defined in '%s' property", PROPERTY_UNDER_TEST),
            "tag_with_underscores", outputValue);
    }

    @Test
    public void shouldFormatNotDefinedTag() {
        environmentVariables.setProperty(PROPERTY_UNDER_TEST, "anothertag1,anothertag2");
        TagInflector tagInflector = new TagInflector(environmentVariables);
        String outputValue = tagInflector.ofTag("rawtype", "tag_with_underscores").toFinalView();
        assertNotEquals(format("tag should be formatted as it was not defined in '%s' property", PROPERTY_UNDER_TEST),
            "tag_with_underscores", outputValue);
    }

    @Test
    public void shouldFormatTagByDefault() {
        TagInflector tagInflector = new TagInflector(new MockEnvironmentVariables());
        String outputValue = tagInflector.ofTag("camel", "tagCamelCase").toFinalView();
        assertNotEquals(format("tag should be formatted as property '%s' not defined", PROPERTY_UNDER_TEST),
            "tagCamelCase", outputValue);
    }
}
