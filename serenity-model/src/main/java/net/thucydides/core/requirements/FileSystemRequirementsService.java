package net.thucydides.core.requirements;

import net.serenitybdd.core.collect.NewList;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.requirements.model.Requirement;
import net.thucydides.core.util.EnvironmentVariables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public class FileSystemRequirementsService extends BaseRequirementsService implements RequirementsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RequirementsTagProvider.class);

    private RequirementsTagProvider fileSystemRequirementsTagProvider;

    public FileSystemRequirementsService(String rootPath) {
        super(Injectors.getInjector().getProvider(EnvironmentVariables.class).get());
        this.fileSystemRequirementsTagProvider = new FileSystemRequirementsTagProvider(environmentVariables, rootPath);
    }

    @Override
    public List<Requirement> getRequirements() {
        if (requirements == null) {
            requirements = fileSystemRequirementsTagProvider.getRequirements();
            requirements = RequirementAncestry.addParentsTo(requirements);
            indexRequirements();
            LOGGER.info("\nREQUIREMENTS HIERARCHY LOADED FROM THE FILE SYSTEM:\n" + RequirementsTree.from(requirements));
        }
        return requirements;

    }

    @Override
    public List<RequirementsTagProvider> getRequirementsTagProviders() {
        return NewList.of(fileSystemRequirementsTagProvider);
    }

    @Override
    public Optional<ReleaseProvider> getReleaseProvider() {
        return Optional.empty();
    }

}
