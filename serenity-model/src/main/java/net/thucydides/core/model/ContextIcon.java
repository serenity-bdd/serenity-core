package net.thucydides.core.model;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static java.util.stream.Collectors.joining;

public class ContextIcon {

    private final static Map<String, String> FONTAWESOME_CLASSES_FOR_COMMON_CONTEXTS = new HashMap();
    static {
        FONTAWESOME_CLASSES_FOR_COMMON_CONTEXTS.put("chrome", "chrome");
        FONTAWESOME_CLASSES_FOR_COMMON_CONTEXTS.put("firefox", "firefox");
        FONTAWESOME_CLASSES_FOR_COMMON_CONTEXTS.put("safari", "safari");
        FONTAWESOME_CLASSES_FOR_COMMON_CONTEXTS.put("opera", "opera");
        FONTAWESOME_CLASSES_FOR_COMMON_CONTEXTS.put("ie", "internet-explorer");
        FONTAWESOME_CLASSES_FOR_COMMON_CONTEXTS.put("edge", "edge");
        FONTAWESOME_CLASSES_FOR_COMMON_CONTEXTS.put("phantomjs", "snapchat-ghost");

        FONTAWESOME_CLASSES_FOR_COMMON_CONTEXTS.put("linux", "linux");
        FONTAWESOME_CLASSES_FOR_COMMON_CONTEXTS.put("mac", "apple");
        FONTAWESOME_CLASSES_FOR_COMMON_CONTEXTS.put("windows", "windows");
        FONTAWESOME_CLASSES_FOR_COMMON_CONTEXTS.put("android", "android");
        FONTAWESOME_CLASSES_FOR_COMMON_CONTEXTS.put("iphone", "apple");
        FONTAWESOME_CLASSES_FOR_COMMON_CONTEXTS.put("ios", "apple");
    }

    public static String forOutcome(TestOutcome testOutcome) {

        String context = testOutcome.getContext();
        if (context == null) {
            return "";
        }

        return String.format("<span class='context-icon'>%s</span>", Arrays.stream(context.split(","))
                .map(String::trim)
                .map(ContextIcon::iconFor)
                .collect(joining(" ")));
    }

    private static String iconFor(String contextName) {
        return FONTAWESOME_CLASSES_FOR_COMMON_CONTEXTS.containsKey(contextName)
                ? String.format(
                        "<i class='fa fa-%s' aria-hidden='true'></i>",
                        FONTAWESOME_CLASSES_FOR_COMMON_CONTEXTS.get(contextName))
                : contextName.toUpperCase();
    }
}
