package net.serenitybdd.maven.plugins;

import org.apache.maven.execution.MavenSession;
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

    /**
     * If maven will be executed in module or in project without children, that serenity.properties will be loaded from
     * correct path, but if mvn executed from root project with children, properties will be loaded root project folder,
     * but should from module folder, and only after that from root folder - this is fix for it.
     * @param session current Maven session
     */
    protected static void propagateBuildDir(MavenSession session){
        System.setProperty("project.build.directory", session.getCurrentProject().getBasedir().getAbsolutePath());
    }
}
