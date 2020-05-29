package net.thucydides.core.requirements;

import com.google.gson.Gson;
import net.thucydides.core.model.TestResult;
import net.thucydides.core.model.TestResultList;
import net.thucydides.core.reports.html.ReportNameProvider;
import net.thucydides.core.reports.html.ResultIconFormatter;
import net.thucydides.core.requirements.model.Requirement;
import net.thucydides.core.requirements.reports.RequirementOutcome;
import net.thucydides.core.requirements.reports.RequirementsOutcomes;
import net.thucydides.core.requirements.tree.Node;
import net.thucydides.core.util.Inflector;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class JSONRequirementsTree {

    private final List<Node> nodes;
    private final boolean displayAsParent;

    private JSONRequirementsTree(List<Node> nodes,
                                 boolean displayAsParent) {
        this.nodes = nodes;
        this.displayAsParent = displayAsParent;
    }

    public JSONRequirementsTree(List<Requirement> requirements, RequirementsOutcomes requirementsOutcomes) {
        nodes = requirements.stream()
                .map(requirement -> toNode(requirement, requirementsOutcomes))
                .sorted()
                .collect(Collectors.toList());
        displayAsParent = false;
    }

    public static JSONRequirementsTree forRequirements(List<Requirement> requirements,
                                                       RequirementsOutcomes requirementsOutcomes) {
        return new JSONRequirementsTree(requirements, requirementsOutcomes);
    }

    public static JSONRequirementsTree forRequirements(List<Requirement> requirements) {
        return new JSONRequirementsTree(requirements, null);
    }

    private Node toNode(Requirement requirement, RequirementsOutcomes requirementsOutcomes) {
        List<Node> children = requirement.getChildren()
                .stream()
                .map(child -> toNode(child, requirementsOutcomes))
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        TestResult result = matchingOutcome(requirement, requirementsOutcomes);

        String label = new ResultIconFormatter().forResult(result, "#");

        String childCount = (children.isEmpty()) ? countScenariosIn(requirement, requirementsOutcomes) : countChildRequirementsIn(requirement);

        String report = new ReportNameProvider().forRequirement(requirement);
//        return new Node(requirement.getName(), requirement.getType(), report, label, childCount, children);
        return new Node(requirement.getDisplayName(), requirement.getType(), report, label, childCount, children);
    }

    private String countChildRequirementsIn(Requirement requirement) {
        Inflector inflection = new Inflector();
        return "<span class='feature-count'>"
                + requirement.getChildren().size() + " "
                + inflection.of(requirement.getChildren().size()).times(requirement.getChildren().get(0).getType()).inPluralForm().toString()
                + "</span>";
    }

    private String countScenariosIn(Requirement requirement, RequirementsOutcomes requirementsOutcomes) {
        Inflector inflection = new Inflector();
        int scenarioCount = scenariosUnder(requirement, requirementsOutcomes);

        if (scenarioCount == 0) return "";

        return "<span class='feature-count'>"
                + scenarioCount + " "
                + inflection.of(scenarioCount).times("scenario").inPluralForm().toString()
                + "</span>";
    }

    private int scenariosUnder(Requirement requirement, RequirementsOutcomes requirementsOutcomes) {
        int scenarioCount = 0;
        if ((requirementsOutcomes != null) && (requirementsOutcomes.requirementOutcomeFor(requirement) != null)
                && ((requirementsOutcomes.requirementOutcomeFor(requirement).getTestOutcomes() != null))) {
            scenarioCount = requirementsOutcomes.requirementOutcomeFor(requirement).getTestOutcomes().getOutcomes().size();
        }
        return scenarioCount;
    }

    private TestResult matchingOutcome(Requirement requirement,
                                       RequirementsOutcomes requirementsOutcomes) {

        if (requirementsOutcomes == null) {
            return TestResult.UNDEFINED;
        }

        Optional<RequirementOutcome> matchingOutcome = testOutcomeForRequirement(requirement, requirementsOutcomes);

        if (matchingOutcome.isPresent()) {
            if (matchingOutcome.get().getTestOutcomes().getTotal() == 0) {
                return TestResult.PENDING;
            } else if (unimplementedFeaturesExistFor(matchingOutcome.get(), requirementsOutcomes)) {
                return TestResultList.overallResultFrom(Arrays.asList(TestResult.PENDING, matchingOutcome.get().getTestOutcomes().getResult()));
            } else {

                return (matchingOutcome.get().getTestOutcomes().getResult());
            }
        }

        return TestResult.UNDEFINED;
    }

    private Optional<RequirementOutcome> testOutcomeForRequirement(Requirement requirement, RequirementsOutcomes requirementsOutcomes) {
        return requirementsOutcomes.getFlattenedRequirementOutcomes().stream()
                .filter(outcome -> outcome.getRequirement().equals(requirement))
                .findFirst();
    }

    private boolean unimplementedFeaturesExistFor(RequirementOutcome matchingOutcome, RequirementsOutcomes requirementsOutcomes) {
        return matchingOutcome.getFlattenedRequirements(matchingOutcome.getRequirement())
                .stream()
                .anyMatch(requirement -> noTestsExistFor(requirement, requirementsOutcomes));
    }

    private boolean noTestsExistFor(Requirement requirement, RequirementsOutcomes requirementsOutcomes) {
        Optional<RequirementOutcome> requirementOutcome = testOutcomeForRequirement(requirement, requirementsOutcomes);
        return !requirementOutcome.isPresent() || (requirementOutcome.get().getTestOutcomes().getTotal() == 0);
    }

    public String asString() {
        Gson gson = new Gson();
        return gson.toJson(nodes);
    }

    public JSONRequirementsTree asAParentRequirement() {
        return new JSONRequirementsTree(nodes, true);
    }
    public Boolean isALeafNode() {
        return !displayAsParent && (nodes.size() == 1 && nodes.get(0).getNodes().isEmpty());
    }
}
