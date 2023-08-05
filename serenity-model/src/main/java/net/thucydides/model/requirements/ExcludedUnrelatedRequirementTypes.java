package net.thucydides.model.requirements;

import com.google.common.base.Splitter;
import net.serenitybdd.model.collect.NewList;
import net.thucydides.model.ThucydidesSystemProperty;
import net.thucydides.model.requirements.model.RequirementsConfiguration;
import net.thucydides.model.util.EnvironmentVariables;

import java.util.List;


public class ExcludedUnrelatedRequirementTypes {

    private final static String DEFAULT_EXCLUDE_UNRELATED_REQUIREMENTS_OF_TYPE = "theme,epic,capability,feature";

    private final static List<String> EXCLUDE_NONE = NewList.of("none");

    private final List<String> excludedTypes;

    private final RequirementsConfiguration requirementsConfiguration;

    public ExcludedUnrelatedRequirementTypes(List<String> excludedTypes, RequirementsConfiguration requirementsConfiguration) {

        this.excludedTypes = excludedTypes;
        this.requirementsConfiguration = requirementsConfiguration;
    }

    public static ExcludedUnrelatedRequirementTypes definedIn(EnvironmentVariables environmentVariables) {
        String unrleatedRequirementTypes =
                ThucydidesSystemProperty.SERENITY_EXCLUDE_UNRELATED_REQUIREMENTS_OF_TYPE.from(environmentVariables,
                        DEFAULT_EXCLUDE_UNRELATED_REQUIREMENTS_OF_TYPE);

        return new ExcludedUnrelatedRequirementTypes(
                Splitter.on(",").trimResults().splitToList(unrleatedRequirementTypes),
                new RequirementsConfiguration(environmentVariables));
    }

    public boolean excludeUntestedRequirementOfType(String type) {
        if (excludedTypes == EXCLUDE_NONE) { return false; }

        return excludedTypes.contains(type);
    }

    public boolean excludeUntestedChildrenOfRequirementOfType(String type) {
        if (excludedTypes == EXCLUDE_NONE) { return false; }

        int requirementLevel = requirementsConfiguration.getRequirementTypes().indexOf(type.toLowerCase());
        String childRequirementType = requirementsConfiguration.getRequirementType(requirementLevel + 1);
        return excludedTypes.contains(childRequirementType);
    }

}
