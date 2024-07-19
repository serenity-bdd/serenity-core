package net.thucydides.model.requirements;

import net.thucydides.model.domain.Release;

import java.util.List;

/**
 * This interface is used to implement plugins that provide a complete list of the known releases.
 * It should be used to extend a class that implements the RequirementsTagProvider interface.
 *
 */
public interface ReleaseProvider {
    /**
     * @return a full tree-structure of known releases.
     */
    List<Release> getReleases();

    /**
     * Some release providers can be deactivated via system properties.
     * @return Is this provider currently activated
     */
    boolean isActive();
}
