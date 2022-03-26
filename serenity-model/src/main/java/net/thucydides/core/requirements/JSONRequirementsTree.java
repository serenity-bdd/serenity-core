package net.thucydides.core.requirements;

import com.google.gson.Gson;
import net.serenitybdd.core.environment.EnvironmentSpecificConfiguration;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.model.TestResult;
import net.thucydides.core.model.TestResultList;
import net.thucydides.core.reports.html.ReportNameProvider;
import net.thucydides.core.reports.html.ResultIconFormatter;
import net.thucydides.core.requirements.model.Requirement;
import net.thucydides.core.requirements.reports.RequirementOutcome;
import net.thucydides.core.requirements.reports.RequirementsOutcomes;
import net.thucydides.core.requirements.tree.Node;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.util.Inflector;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class JSONRequirementsTree {

    private final List<Node> nodes;
    private final boolean displayAsParent;
    private final boolean hideEmptyRequirements;

    private JSONRequirementsTree(List<Node> nodes,
                                 boolean displayAsParent) {
        this.hideEmptyRequirements = EnvironmentSpecificConfiguration.from(Injectors.getInjector().getInstance(EnvironmentVariables.class))
                .getBooleanProperty(ThucydidesSystemProperty.SERENITY_REPORT_HIDE_EMPTY_REQUIREMENTS, true);
        this.nodes = nodes;
        this.displayAsParent = displayAsParent;
    }

    public JSONRequirementsTree(List<Requirement> requirements, RequirementsOutcomes requirementsOutcomes) {
        hideEmptyRequirements = EnvironmentSpecificConfiguration.from(Injectors.getInjector().getInstance(EnvironmentVariables.class))
                .getBooleanProperty(ThucydidesSystemProperty.SERENITY_REPORT_HIDE_EMPTY_REQUIREMENTS, true);
        nodes = requirements.stream()
                .filter(requirement -> shouldShow(requirement, requirementsOutcomes))
                .map(requirement -> toNode(requirement, requirementsOutcomes))
                .sorted()
                .collect(Collectors.toList());
        displayAsParent = false;
    }

    private boolean shouldShow(Requirement requirement, RequirementsOutcomes requirementsOutcomes) {
        if (!hideEmptyRequirements) {
            return true;
        }
        return requirementsOutcomes.requirementOutcomeFor(requirement).getTestCount() > 0;
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

        TestResult result = matchingOutcome(requirement, requirementsOutcomes).orElse(TestResult.UNDEFINED);

        String label = new ResultIconFormatter().forResult(result, "#");

        String childCount = (children.isEmpty()) ? countScenariosIn(requirement, requirementsOutcomes) : countChildRequirementsIn(requirement);

        String report = new ReportNameProvider().forRequirement(requirement);
        return new Node(requirement.getDisplayName(), requirement.getType(), report, label, childCount, children);
    }

    private String countChildRequirementsIn(Requirement requirement) {
        Inflector inflection = Inflector.getInstance();
        return "<span class='feature-count'>"
                + requirement.getChildren().size() + " "
                + inflection.of(requirement.getChildren().size()).times(requirement.getChildren().get(0).getType()).inPluralForm().toString()
                + "</span>";
    }

    private String countScenariosIn(Requirement requirement, RequirementsOutcomes requirementsOutcomes) {
        Inflector inflection = Inflector.getInstance();
        int scenarioCount = scenariosUnder(requirement, requirementsOutcomes);

        if (scenarioCount == 0) return "";

        return "<span class='feature-count'>"
                + scenarioCount + " "
                + inflection.of(scenarioCount).times("scenario").inPluralForm().toString()
                + "</span>";
    }

    private int scenariosUnder(Requirement requirement, RequirementsOutcomes requirementsOutcomes) {
        return requirementsOutcomes.getTestOutcomes().forRequirement(requirement).getTestCount();
    }

    private Optional<TestResult> matchingOutcome(Requirement requirement,
                                                 RequirementsOutcomes requirementsOutcomes) {

        if (requirementsOutcomes == null) {
            return Optional.empty();
        }

        Optional<RequirementOutcome> matchingOutcome = testOutcomeForRequirement(requirement, requirementsOutcomes);

        if (matchingOutcome.isPresent()) {
            if (matchingOutcome.get().getTestOutcomes().getTotal() == 0 || (requirement.getScenarioTags().size() > matchingOutcome.get().getTestOutcomes().getTotal())) {
                return Optional.of(TestResult.PENDING);
            } else if (unimplementedFeaturesExistFor(matchingOutcome.get(), requirementsOutcomes)) {
                return Optional.of(TestResultList.overallResultFrom(Arrays.asList(TestResult.PENDING, matchingOutcome.get().getTestOutcomes().getResult())));
            } else {
                return Optional.of(matchingOutcome.get().getTestOutcomes().getResult());
            }
        }
        return Optional.empty();
    }

    private Optional<RequirementOutcome> testOutcomeForRequirement(Requirement requirement,
                                                                   RequirementsOutcomes requirementsOutcomes) {
        if (requirementsOutcomes.getParentRequirement().isPresent() && requirement.equals(requirementsOutcomes.getParentRequirement().get())) {
            return Optional.of(requirementsOutcomes.requirementOutcomeFor(requirement));
        } else if (requirementsOutcomes.getRequirements().contains(requirement)) {
            return requirementsOutcomes.getOutcomeFor(requirement);
        } else {
            return requirementsOutcomes.getFlattenedRequirementOutcomes().stream()
                    .filter(outcome -> outcome.getRequirement().equals(requirement))
                    .findFirst();
        }
    }

    private boolean unimplementedFeaturesExistFor(RequirementOutcome matchingOutcome, RequirementsOutcomes
            requirementsOutcomes) {
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
