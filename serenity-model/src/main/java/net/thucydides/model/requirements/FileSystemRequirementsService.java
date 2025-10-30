package net.thucydides.model.requirements;

import net.serenitybdd.model.collect.NewList;
import net.thucydides.model.domain.TestOutcome;
import net.thucydides.model.environment.SystemEnvironmentVariables;
import net.thucydides.model.requirements.model.Requirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public class FileSystemRequirementsService extends BaseRequirementsService implements RequirementsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RequirementsTagProvider.class);

    private final RequirementsTagProvider fileSystemRequirementsTagProvider;

    public FileSystemRequirementsService(String rootPath) {
        super(SystemEnvironmentVariables.currentEnvironmentVariables());
        this.fileSystemRequirementsTagProvider = new FileSystemRequirementsTagProvider(environmentVariables, rootPath);
    }

    @Override
    public List<Requirement> getRequirements() {
        if (requirements == null) {
            requirements = fileSystemRequirementsTagProvider.getRequirements();
            RequirementAncestry.addParentsTo(requirements);
            indexRequirements();
            LOGGER.trace("\nREQUIREMENTS HIERARCHY LOADED FROM THE FILE SYSTEM:\n" + RequirementsTree.from(requirements));
        }
        return requirements;

    }

    @Override
    public void addRequirementTagsTo(TestOutcome outcome) {
        fileSystemRequirementsTagProvider.addRequirementTagsTo(outcome);
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
