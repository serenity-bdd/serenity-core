package net.serenitybdd.cli.reporters;

import net.thucydides.core.requirements.FileSystemRequirements;
import net.thucydides.core.requirements.Requirements;

/**
 * Created by john on 25/06/2016.
 */
public class RequirementsStrategy {
    public static Requirements forDirectory(String requirementsDirectory) {
        return new FileSystemRequirements(requirementsDirectory);
    }
}
