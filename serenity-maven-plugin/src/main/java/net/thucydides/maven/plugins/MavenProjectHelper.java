package net.thucydides.maven.plugins;

import org.apache.maven.project.MavenProject;

/**
 * Utility class designed to help analyse Maven project structures.
 */
public class MavenProjectHelper {
    public static String getProjectIdentifier(final MavenProject project) {
        if (project != null) {
            return project.getGroupId() + "-" + project.getArtifactId();
        } else {
            return "";
        }
    }
}
