package net.thucydides.model.domain;

import net.thucydides.model.requirements.reports.ScenarioOutcome;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static java.util.stream.Collectors.joining;

public class ContextIcon {

    private final static Map<String, String> ICON_CLASSES_FOR_COMMON_CONTEXTS = new HashMap<>();
    static {
        ICON_CLASSES_FOR_COMMON_CONTEXTS.put("chrome", "browser-chrome");
        ICON_CLASSES_FOR_COMMON_CONTEXTS.put("firefox", "browser-firefox");
        ICON_CLASSES_FOR_COMMON_CONTEXTS.put("safari", "browser-safari");
        ICON_CLASSES_FOR_COMMON_CONTEXTS.put("ie", "browser-edge");
        ICON_CLASSES_FOR_COMMON_CONTEXTS.put("edge", "browser-edge");

        ICON_CLASSES_FOR_COMMON_CONTEXTS.put("linux", "ubuntu");
        ICON_CLASSES_FOR_COMMON_CONTEXTS.put("mac", "apple");
        ICON_CLASSES_FOR_COMMON_CONTEXTS.put("windows", "windows");
        ICON_CLASSES_FOR_COMMON_CONTEXTS.put("android", "android");
        ICON_CLASSES_FOR_COMMON_CONTEXTS.put("iphone", "apple");
        ICON_CLASSES_FOR_COMMON_CONTEXTS.put("ios", "apple");
    }

    private final static Map<String, String> CONTEXT_TITLES = new HashMap<>();
    static {
        CONTEXT_TITLES.put("chrome", "Chrome");
        CONTEXT_TITLES.put("firefox", "Firefox");
        CONTEXT_TITLES.put("safari", "Safari or WebKit");
        CONTEXT_TITLES.put("ie", "Microsoft Internet Explorer");
        CONTEXT_TITLES.put("edge", "Microsoft Edge");

        CONTEXT_TITLES.put("mac", "Mac OS X");
        CONTEXT_TITLES.put("iphone", "iPhone");
        CONTEXT_TITLES.put("ios", "iOS");
    }

    public static String forOutcome(ScenarioOutcome scenarioOutcome) {
        return forContext(scenarioOutcome.getContext());
    }

    public static String labelForOutcome(ScenarioOutcome scenarioOutcome) {
        return labelsFrom(scenarioOutcome.getContext());
    }

    public static String labelForOutcome(TestOutcome outcome) {
        return labelsFrom(outcome.getContext());
    }

    public static String forContext(String context) {
        if (context == null) {
            return "";
        }

        return String.format("<span class='context-icon'>%s</span>", Arrays.stream(context.split(","))
                .map(String::trim)
                .map(ContextIcon::iconFor)
                .collect(joining(" ")));
    }

    private static String labelsFrom(String context) {
        if (context == null) {
            return "";
        }

        return Arrays.stream(context.split(","))
                .map(String::trim)
                .map(item -> CONTEXT_TITLES.getOrDefault(item, item))
                .collect(joining(","));
    }

    public static String forOutcome(TestOutcome testOutcome) {
        return forContext(testOutcome.getContext());
    }

    private static String iconFor(String contextName) {
        // find the class for a context that starts with contextName
        return ICON_CLASSES_FOR_COMMON_CONTEXTS.entrySet().stream()
                .filter(entry -> contextName.toLowerCase().startsWith(entry.getKey().toLowerCase()))
                .map(entry -> String.format(
                        "<i class='bi bi-%s' title='%s'></i>",
                        entry.getValue(),
                        CONTEXT_TITLES.getOrDefault(entry.getKey(),StringUtils.capitalize(entry.getKey()))))
                .findFirst()
                .orElse(labelor(contextName));
    }


    private static String labelor(String contextName) {
        return CONTEXT_TITLES.containsKey(contextName)
                ? String.format(
                "<i class='bi bi-%s' title='%s'></i>",
                ICON_CLASSES_FOR_COMMON_CONTEXTS.get(contextName),
                CONTEXT_TITLES.getOrDefault(contextName,contextName))
                : contextName.toUpperCase();
    }

}
