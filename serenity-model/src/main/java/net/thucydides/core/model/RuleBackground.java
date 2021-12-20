package net.thucydides.core.model;

import io.cucumber.messages.types.Background;
import net.thucydides.core.requirements.reports.cucumber.RenderCucumber;

import java.util.List;
import java.util.stream.Collectors;

public class RuleBackground {
    private final String name;
    private final String description;
    private final List<String> steps;

    public RuleBackground(String name, String description, List<String> steps) {
        this.name = name;
        this.description = description;

        this.steps = steps;
    }

    public static RuleBackground from(Background background) {
        String name = background.getName();
        String description = background.getDescription();
        List<String> steps = background.getSteps().stream()
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
