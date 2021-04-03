package net.thucydides.core.model;

import io.cucumber.messages.Messages;
import net.thucydides.core.requirements.reports.cucumber.RenderCucumber;

import java.util.List;
import java.util.stream.Collectors;

public class RuleBackground {
    private String name;
    private String description;
    private List<String> steps;

    public RuleBackground(String name, String description, List<String> steps) {
        this.name = name;
        this.description = description;

        this.steps = steps;
    }

    public static RuleBackground from(Messages.GherkinDocument.Feature.Background background) {
        String name = background.getName();
        String description = background.getDescription();
        List<String> steps = background.getStepsList().stream()
                                       .map(RenderCucumber::step)
                                       .collect(Collectors.toList());
        return new RuleBackground(name, description, steps);
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getSteps() {
        return steps;
    }
}
