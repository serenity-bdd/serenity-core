package net.serenitybdd.core.reports.styling;

import com.google.common.base.Splitter;
import net.thucydides.model.domain.TestTag;
import net.thucydides.model.util.EnvironmentVariables;

import java.util.*;

/**
 * The color of different types of tags in the Serenity reports can be configured using Serenity properties.
 * You can set the background and foreground colors using the `tag.color.for.*` tags. For example, to make
 * error tags red, you could add `tag.color.for.error=red`.
 */
public class TagStylist {

    private final static String BACKGROUND_STYLE = "background:%s;";
    private final static String BACKGROUND_AND_COLOR_STYLE = "background:%s; color:%s;";
    private final static String DEFAULT_COLOR_STYLE = "default";
    private final static String TAG_COLOR_PREFIX = "tag.color.for";

    private final Map<String, String> backgroundColorsByType;
    private final Map<String, String> foregroundColorsByType;

    private TagStylist(EnvironmentVariables environmentVariables) {

        Properties tagColorProperties = environmentVariables.getPropertiesWithPrefix(TAG_COLOR_PREFIX);

        backgroundColorsByType = new HashMap<>();
        tagColorProperties.keySet().forEach(
                key -> {
                    Optional<String> backgroundColor = backgroundColorIn(tagColorProperties.get(key));
                    backgroundColor.ifPresent(
                            color -> backgroundColorsByType.put(tagTypeFrom(key), color)
                    );

                }
        );

        foregroundColorsByType = new HashMap<>();
        tagColorProperties.keySet().forEach(
                key -> {
                    Optional<String> foregroundColor = foregroundColorIn(tagColorProperties.get(key));
                    foregroundColor.ifPresent(
                            color -> foregroundColorsByType.put(tagTypeFrom(key), color)
                    );

                }
        );
    }

    private Optional<String> backgroundColorIn(Object value) {
        List<String> colors = definedColorsIn(value);

        if (colors.isEmpty()) { return Optional.empty(); }

        return Optional.of(colors.get(0));
    }

    private List<String> definedColorsIn(Object value) {
        return Splitter.on(",").trimResults().omitEmptyStrings().splitToList(value.toString());
    }
    private Optional<String> foregroundColorIn(Object value) {
        List<String> colors = Splitter.on(",")
                .trimResults()
                .omitEmptyStrings()
                .splitToList(value.toString());

        if (colors.size() <= 1) {
            return Optional.empty();
        }

        return Optional.of(colors.get(1));

    }

    private String tagTypeFrom(Object key) {
        return key.toString().replace(TAG_COLOR_PREFIX + ".", "");
    }

    public static TagStylist from(EnvironmentVariables environmentVariables) {
        return new TagStylist(environmentVariables);
    }

    public String tagStyleFor(TestTag tag) {
        if (tag == null) return "";

        String backgroundColor = backgroundColorsByType.getOrDefault(tag.getType(), DEFAULT_COLOR_STYLE);
        String foregroundColor = foregroundColorsByType.getOrDefault(tag.getType(), DEFAULT_COLOR_STYLE);

        if (foregroundColorsByType.containsKey(tag.getType())) {
            return String.format(BACKGROUND_AND_COLOR_STYLE, backgroundColor, foregroundColor);
        } else {
            return String.format(BACKGROUND_STYLE, backgroundColor);
        }
    }
}
