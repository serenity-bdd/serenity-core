package net.thucydides.core.model;

import java.util.HashMap;
import java.util.Map;

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
    }

    public static String forOutcome(TestOutcome testOutcome) {

        String context = testOutcome.getContext();
        if (context == null) {
            return "";
        }

        if (FONTAWESOME_CLASSES_FOR_COMMON_CONTEXTS.containsKey(context.toLowerCase())) {
            return String.format("<span class='context-icon'><i class='fa fa-%s' aria-hidden='true'></i></span>",
                    FONTAWESOME_CLASSES_FOR_COMMON_CONTEXTS.get(context.toLowerCase()));
        }
        return "<span class='context-icon'>" + context.toUpperCase() + "</span>";
    }
}
