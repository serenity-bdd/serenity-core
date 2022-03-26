package net.thucydides.core.model;

import io.cucumber.messages.types.RuleChild;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Rule {

    private String name;
    private String description;
    private final RuleBackground background;
    private final List<TestTag> tags;

    public Rule(String name, String description, List<TestTag> tags, RuleBackground background) {
        this.name = name;
        this.description = description;
        this.background = background;
        this.tags = tags;
    }

    public static Rule from(io.cucumber.messages.types.Rule cucumberRule) {
        String name = cucumberRule.getName();
        String description = cucumberRule.getDescription();
        RuleBackground ruleBackground = cucumberRule.getChildren().stream()
                .map(RuleChild::getBackground)
                .filter(Objects::nonNull)
                .map(RuleBackground::from)
                .findFirst().orElse(null);

        List<TestTag> tags = cucumberRule.getTags()
                                         .stream()
                                         .map(tag -> TestTag.withValue(tag.getName())).collect(Collectors.toList());
        return new Rule(name, description, tags, ruleBackground);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean hasBackground() {
        return background != null;
    }

    public RuleBackground getBackground() {
        return background;
    }

    @Override
    public boolean equals(Object other) {
        if(other == null) return false;
        if (! (other instanceof Rule)) return false;
        if(other == this) return true;
        return this.getName().equals(((Rule) other).getName());
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    public List<TestTag> getTags() {
        return tags;
    }
}
