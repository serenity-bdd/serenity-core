package net.thucydides.core.requirements.model;

import java.util.List;

import static net.thucydides.core.requirements.classpath.PathElements.lastOf;

public class RequirementTypeAt {
    private final int level;

    protected RequirementTypeAt(int level) {
        this.level = level;
    }

    public static RequirementTypeAt level(int level) {
        return new RequirementTypeAt(level);
    }

    public String in(List<String> requirementTypes) {
        if (level >= requirementTypes.size()) {
            return lastOf(requirementTypes);
        } else {
            return requirementTypes.get(level);
        }
    }
}
