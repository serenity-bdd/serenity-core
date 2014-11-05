package net.thucydides.core.requirements;

import net.thucydides.core.guice.Injectors;
import net.thucydides.core.requirements.model.RequirementsConfiguration;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.util.Inflector;

import java.util.List;

public class AbstractRequirementsTagProvider {

    protected final EnvironmentVariables environmentVariables;
    protected final String rootDirectory;
    protected final RequirementsConfiguration requirementsConfiguration;
    protected final RequirementsService requirementsService;

    protected AbstractRequirementsTagProvider(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
        this.requirementsConfiguration = new RequirementsConfiguration(environmentVariables);
        this.requirementsService = Injectors.getInjector().getInstance(RequirementsService.class);
        this.rootDirectory = getDefaultRootDirectory();
    }

    protected String humanReadableVersionOf(String name) {
        String underscoredName = Inflector.getInstance().underscore(name);
        return Inflector.getInstance().humanize(underscoredName);
    }

    protected String getDefaultType(int level) {
        List<String> types = getRequirementTypes();
        if (level > types.size() - 1) {
            return types.get(types.size() - 1);
        } else {
            return types.get(level);
        }
    }

    protected List<String> getRequirementTypes() {
        return requirementsConfiguration.getRequirementTypes();
    }

    protected String getDefaultRootDirectory() {
        return requirementsConfiguration.getDefaultRootDirectory();
    }
}
