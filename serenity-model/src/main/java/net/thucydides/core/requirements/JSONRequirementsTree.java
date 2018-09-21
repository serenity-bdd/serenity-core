package net.thucydides.core.requirements;

import com.google.gson.Gson;
import net.thucydides.core.model.TestResult;
import net.thucydides.core.reports.html.ReportNameProvider;
import net.thucydides.core.reports.html.ResultIconFormatter;
import net.thucydides.core.requirements.model.Requirement;
import net.thucydides.core.requirements.reports.RequirementOutcome;
import net.thucydides.core.requirements.reports.RequirementsOutcomes;
import net.thucydides.core.requirements.tree.Node;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class JSONRequirementsTree {

    private final List<Node> nodes;

    public JSONRequirementsTree(List<Requirement> requirements, RequirementsOutcomes requirementsOutcomes) {
        nodes = requirements.stream().map(requirement -> toNode(requirement, requirementsOutcomes))
                            .collect(Collectors.toList());
    }

    public static JSONRequirementsTree forRequirements(List<Requirement> requirements,
                                                       RequirementsOutcomes requirementsOutcomes) {
        return new JSONRequirementsTree(requirements, requirementsOutcomes);
    }

    private Node toNode(Requirement requirement,RequirementsOutcomes requirementsOutcomes) {
        List<Node> children = requirement.getChildren().stream()
                                         .map(child -> toNode(child, requirementsOutcomes))
                                         .collect(Collectors.toList());

        TestResult result = matchingOutcome(requirement, requirementsOutcomes);

        String label = new ResultIconFormatter().forResult(result);

        String report = new ReportNameProvider().forRequirement(requirement);
        return new Node(requirement.getName(), report, label, children);
    }

    private TestResult matchingOutcome(Requirement requirement,
                                       RequirementsOutcomes requirementsOutcomes) {

        if (requirementsOutcomes.getParentRequirement().isPresent() && requirementsOutcomes.getParentRequirement().get() == requirement) {
            return requirementsOutcomes.getTestOutcomes().getResult();
        }

//        if (!requirement.hasChildren()) {
//            return requirementsOutcomes.getTestOutcomes().getResult();
//        }
//
        Optional<RequirementOutcome> matchingOutcome = requirementsOutcomes.getFlattenedRequirementOutcomes().stream()
                                                            .filter(outcome -> outcome.getRequirement().equals(requirement))
                                                            .findFirst();

        if (matchingOutcome.isPresent()) {
            return (matchingOutcome.get().getTestOutcomes().getResult());
        }

        return TestResult.UNDEFINED;
    }

    public String asString() {
        Gson gson = new Gson();
        return gson.toJson(nodes);
    }

    public Boolean isALeafNode() {
        return nodes.size() == 1 && nodes.get(0).getNodes().isEmpty();
    }
}
