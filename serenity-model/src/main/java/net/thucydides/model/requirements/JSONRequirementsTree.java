package net.thucydides.model.requirements;

import com.google.common.base.Splitter;
import com.google.gson.Gson;
import com.vladsch.flexmark.util.ast.Document;
import io.cucumber.tagexpressions.Expression;
import net.serenitybdd.model.environment.EnvironmentSpecificConfiguration;
import net.thucydides.model.ThucydidesSystemProperty;
import net.thucydides.model.environment.SystemEnvironmentVariables;
import net.thucydides.model.domain.TestResult;
import net.thucydides.model.domain.TestResultList;
import net.thucydides.model.domain.TestTag;
import net.thucydides.model.reports.html.ReportNameProvider;
import net.thucydides.model.reports.html.ResultIconFormatter;
import net.thucydides.model.requirements.model.Requirement;
import net.thucydides.model.requirements.model.TagParser;
import net.thucydides.model.requirements.reports.RequirementOutcome;
import net.thucydides.model.requirements.reports.RequirementsOutcomes;
import net.thucydides.model.requirements.tree.Node;
import net.thucydides.model.util.Inflector;
import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;

import static net.thucydides.model.util.NameConverter.humanize;

public class JSONRequirementsTree {

    private final List<Node> nodes;
    private final List<String> tags;
    private final boolean displayAsParent;
    private final boolean hideEmptyRequirements;
    private final Gson gson = new Gson();

    private JSONRequirementsTree(List<Node> nodes,
                                 boolean displayAsParent,
                                 List<String> tags) {
        this.hideEmptyRequirements = EnvironmentSpecificConfiguration.from(SystemEnvironmentVariables.currentEnvironmentVariables())
                .getBooleanProperty(ThucydidesSystemProperty.SERENITY_REPORT_HIDE_EMPTY_REQUIREMENTS, true);
        this.nodes = nodes;
        this.displayAsParent = displayAsParent;
        this.tags = tags;
    }

    public JSONRequirementsTree(List<Requirement> requirements,
                                RequirementsOutcomes requirementsOutcomes,
                                String tagsExpression) {
        hideEmptyRequirements = EnvironmentSpecificConfiguration.from(SystemEnvironmentVariables.currentEnvironmentVariables())
                .getBooleanProperty(ThucydidesSystemProperty.SERENITY_REPORT_HIDE_EMPTY_REQUIREMENTS, true);
        tags = Splitter.on(",").trimResults().omitEmptyStrings().splitToList(tagsExpression);
        nodes = requirements.stream()
                .filter(requirement -> shouldShow(requirement, requirementsOutcomes))
                .map(requirement -> toNode(requirement, requirementsOutcomes))
                .sorted()
                .collect(Collectors.toList());
        displayAsParent = false;
    }

    private boolean shouldShow(Requirement requirement, RequirementsOutcomes requirementsOutcomes) {
        // If tags are specified, only show requirements that match the tag expression
        if (!tags.isEmpty()) {
            Expression expression = TagParser.parseFromTagFilters(tags);
            List<String> tagsForThisRequirement = requirement.getAggregateTags()
                    .stream()
                    .map(TestTag::getName)
                    .map(tag -> addTagPrefixTo(tag))
                    .collect(Collectors.toList());


            if (!expression.evaluate(tagsForThisRequirement)) {
                return false;
            }
        }
        // We can opt to always show requirements even if there are no associated tests by setting the hideEmptyRequirements property to false
        if (!hideEmptyRequirements) {
            return true;
        }
        // Otherwise, only show the requirement if it has tests
        return requirementsOutcomes.requirementOutcomeFor(requirement).getTestCount() > 0;
    }

    private String addTagPrefixTo(String tag) {
        return tag.startsWith("@") ? tag : "@" + tag;
    }

    public static JSONRequirementsTree forRequirements(List<Requirement> requirements,
                                                       RequirementsOutcomes requirementsOutcomes,
                                                       String tags) {
        return new JSONRequirementsTree(requirements, requirementsOutcomes, tags);
    }

    private Node toNode(Requirement requirement, RequirementsOutcomes requirementsOutcomes) {
        List<Node> children = requirement.getChildren()
                .stream()
                .filter(childRequirement -> shouldShow(childRequirement, requirementsOutcomes))
                .map(child -> toNode(child, requirementsOutcomes))
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        TestResult result = matchingOutcome(requirement, requirementsOutcomes).orElse(TestResult.UNDEFINED);

        String label = new ResultIconFormatter().forResult(result, "#");

        String childCount = (children.isEmpty()) ?
                countScenariosIn(requirement, requirementsOutcomes)
                : countChildRequirementsIn(requirement) + " " + countScenariosIn(requirement, requirementsOutcomes);

        String report = new ReportNameProvider().forRequirement(requirement);

        String requirementName = humanize(getRequirementNameFrom(requirement));

        return new Node(requirementName, requirement.getType(), report, label, childCount, children);
    }

    private String requirementsAndScenariosFor(Requirement requirement, RequirementsOutcomes requirementsOutcomes) {
        String requirementChildren = countChildRequirementsIn(requirement);
        String scenarioCount = countScenariosIn(requirement, requirementsOutcomes);
        return (scenarioCount.isEmpty()) ? requirementChildren : requirementChildren + ", " + scenarioCount;
    }

    @NotNull
    private static String getRequirementNameFrom(Requirement requirement) {
        Parser parser = Parser.builder().build();
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        Document document = parser.parse(requirement.getDisplayName());
        return Jsoup.parse(renderer.render(document)).text();
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
        long scenarioCount = scenariosUnder(requirement, requirementsOutcomes);

        if (scenarioCount == 0) return "";


        String scenarioCountText = "<span class='feature-count'>"
                + scenarioCount + " "
                + inflection.of(scenarioCount).times("scenario").inPluralForm().toString()
                + "</span>";

        long testCaseCount = testCasesUnder(requirement, requirementsOutcomes);

        String testCaseCountText = "<span class='feature-count'>"
                + testCaseCount + " "
                + inflection.of(testCaseCount).times("test case").inPluralForm().toString()
                + "</span>";

        return scenarioCountText + ", " + testCaseCountText;
    }

    private long scenariosUnder(Requirement requirement, RequirementsOutcomes requirementsOutcomes) {
        return requirementsOutcomes.getTestOutcomes().directlyUnder(requirement).getScenarioCount();
    }

    private long testCasesUnder(Requirement requirement, RequirementsOutcomes requirementsOutcomes) {
        return requirementsOutcomes.getTestOutcomes().directlyUnder(requirement).getTestCaseCount();
    }

    private Optional<TestResult> matchingOutcome(Requirement requirement,
                                                 RequirementsOutcomes requirementsOutcomes) {

        if (requirementsOutcomes == null) {
            return Optional.empty();
        }

        Optional<RequirementOutcome> matchingOutcome = testOutcomeForRequirement(requirement, requirementsOutcomes);

        if (matchingOutcome.isPresent()) {
//            if (matchingOutcome.get().getTestOutcomes().getTotal() == 0
//                    || (requirement.getScenarioTags().size() > matchingOutcome.get().getTestOutcomes().getTotal())) {
            if (matchingOutcome.get().getTestOutcomes().getTotal() == 0) {
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
        return gson.toJson(nodes);
    }

    public JSONRequirementsTree asAParentRequirement() {
        return new JSONRequirementsTree(nodes, true, tags);
    }

    public Boolean isALeafNode() {
        return !displayAsParent && (nodes.size() == 1 && nodes.get(0).getNodes().isEmpty());
    }
}
