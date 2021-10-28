package io.cucumber.core.plugin;

import io.cucumber.messages.types.Tag;
import net.thucydides.core.model.TestResult;

import java.util.*;

public class TaggedScenario {
    private static final List<String> SKIPPED_TAGS = Arrays.asList("@skip", "@wip");
    private static final List<String> IGNORED_TAGS = Arrays.asList("@ignore", "@ignored");

    private static final Map<String, TestResult> MANUAL_TEST_RESULTS;

    static {
        MANUAL_TEST_RESULTS = new HashMap<>();
        MANUAL_TEST_RESULTS.put("pass", TestResult.SUCCESS);
        MANUAL_TEST_RESULTS.put("passed", TestResult.SUCCESS);
        MANUAL_TEST_RESULTS.put("success", TestResult.SUCCESS);
        MANUAL_TEST_RESULTS.put("successful", TestResult.SUCCESS);
        MANUAL_TEST_RESULTS.put("failure", TestResult.FAILURE);
        MANUAL_TEST_RESULTS.put("failed", TestResult.FAILURE);
        MANUAL_TEST_RESULTS.put("fail", TestResult.FAILURE);
        MANUAL_TEST_RESULTS.put("compromised", TestResult.COMPROMISED);
    }

    static boolean isPending(List<Tag> tags) {
        return hasTag("@pending", tags);
    }

    static boolean isManual(List<Tag> tags) {
        return tags.stream().anyMatch(tag -> tag.getName().toLowerCase().startsWith("@manual"));
    }

    public static Optional<TestResult> manualResultDefinedIn(List<Tag> tags) {
        if (!isManual(tags)) {
            return Optional.empty();
        }
        Optional<Tag> manualTagWithResult
                = tags.stream()
                      .filter(tag -> tag.getName().toLowerCase().startsWith("@manual:") || tag.getName().toLowerCase().startsWith("@manual-result:"))
                      .findFirst();
        if (manualTagWithResult.isPresent()) {
            String tagName = manualTagWithResult.get().getName();
            int resultIndex = tagName.indexOf(":") + 1;
            String result = manualTagWithResult.get().getName().substring(resultIndex);
            return Optional.of(MANUAL_TEST_RESULTS.getOrDefault(result.toLowerCase(), TestResult.PENDING));
        } else {
            return Optional.of(TestResult.PENDING);
        }
    }

    static boolean isSkippedOrWIP(List<Tag> tags) {
        for (Tag tag : tags) {
            if (SKIPPED_TAGS.contains(tag.getName().toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    static boolean isIgnored(List<Tag> tags) {
        for (Tag tag : tags) {
            if (IGNORED_TAGS.contains(tag.getName().toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    private static boolean hasTag(String tagName, List<Tag> tags) {
        for (Tag tag : tags) {
            if (tag.getName().equalsIgnoreCase(tagName)) {
                return true;
            }
        }
        return false;
    }

}
