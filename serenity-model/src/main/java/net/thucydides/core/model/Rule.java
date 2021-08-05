package net.thucydides.core.model;

import io.cucumber.messages.Messages;

public class Rule {

    private String name;
    private String description;
    private RuleBackground background;

    public Rule(String name, String description, RuleBackground background) {
        this.name = name;
        this.description = description;
        this.background = background;
    }

    public static Rule from(Messages.GherkinDocument.Feature.FeatureChild.Rule cucumberRule) {
        String name = cucumberRule.getName();
        String description = cucumberRule.getDescription();
        RuleBackground ruleBackground = cucumberRule.getChildrenList().stream()
                .filter(ruleChild -> ruleChild.hasBackground())
                .map(ruleChild -> ruleChild.getBackground())
                .map(background -> RuleBackground.from(background))
                .findFirst().orElse(null);

        return new Rule(name, description, ruleBackground);
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
}
