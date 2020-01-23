package net.thucydides.core.requirements.model;

import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

/**
 * Created by john on 30/5/17.
 */
public class RequirementTree {
    private static final int INDENT_STEPS = 4;
    private final List<Requirement> requirements;

    public RequirementTree(List<Requirement> requirements) {
        this.requirements = requirements;
    }

    public static RequirementTree withRequirements(List<Requirement> requirements) {
        return new RequirementTree(requirements);
    }

    public String toString() {
        if (requirements.isEmpty()) { return "NO REQUIREMENTS FOUND"; }

        ByteArrayOutputStream printedTree = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(printedTree);

        printStream.println("REQUIREMENTS:");

        for(Requirement requirement : requirements) {
            printStream.print(stringFormOf(requirement));
        }

        return printedTree.toString();

    }

    private RequirementAsString stringFormOf(Requirement requirement) {
        return new RequirementAsString(requirement, 1);
    }

    private class RequirementAsString {

        private final Requirement requirement;
        private final int level;
        private final  String indent;

        public RequirementAsString(Requirement requirement, int level) {
            this.requirement = requirement;
            this.level = level;
            this.indent = StringUtils.repeat(" ", level * INDENT_STEPS);
        }

        RequirementAsString withIndentationLevel(int newLevel) {
            return new RequirementAsString(requirement, newLevel);
        }

        public String toString() {
            ByteArrayOutputStream printedRequirement = new ByteArrayOutputStream();
            PrintStream printStream = new PrintStream(printedRequirement);

            printStream.println(indent + "- " + requirement.getType() + " : " + requirement.getName() + detailsOf(requirement));

            for(Requirement child : requirement.getChildren()) {
                printStream.print(stringFormOf(child).withIndentationLevel(level + 1));
            }

            return printedRequirement.toString();
        }

        private String detailsOf(Requirement requirement) {
            return " {" +
                    "id: " + requirement.getId() + ", " +
                    "displayName: '" + requirement.getDisplayName() + "', " +
                    "path: " + requirement.getPath() + ", " +
                    "parent: '" + requirement.getParent() + "'}";
        }
    }
}
