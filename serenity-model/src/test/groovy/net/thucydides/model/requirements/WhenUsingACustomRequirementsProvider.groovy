package net.thucydides.model.requirements

import net.thucydides.model.statistics.service.ClasspathTagProviderService
import net.thucydides.model.statistics.service.TagProvider
import spock.lang.Specification

class WhenUsingACustomRequirementsProvider extends Specification {

    def tagProviderService = new ClasspathTagProviderService();
    def ClasspathRequirementsProviderService requirementsProviderService = new ClasspathRequirementsProviderService(tagProviderService)

    def "Should return the default file system requirements provider on the classpath if no others are present"() {
        given:
            def tagProviderService = Mock(ClasspathTagProviderService)
            def requirementsProviderService = new ClasspathRequirementsProviderService(tagProviderService)
            tagProviderService.getTagProviders() >> [new FileSystemRequirementsTagProvider(), new PackageAnnotationBasedTagProvider()]
        when: "We get the list of default requirements providers"
            List<RequirementsTagProvider> requirementsProviders = requirementsProviderService.getRequirementsProviders();
        then: "We obtain the default requirements provider"
            requirementsProviders.collect { it.class.simpleName }.containsAll(["FileSystemRequirementsTagProvider", "PackageAnnotationBasedTagProvider"])
    }

    def "Should not return the default file system requirements provider on the classpath if others are defined"() {
        given:
            ClasspathTagProviderService tagProviderService = new ClasspathTagProviderService() {
                @Override
                protected Iterable<TagProvider> loadTagProvidersFromPath(String testSource) {
                    return [ new FileSystemRequirementsTagProvider(), new CustomRequirementsTagProvider()]
                }
            }
        when: "We get the list of default requirements providers"
            List<RequirementsTagProvider> tagProviders = tagProviderService.getTagProviders()
        then: "We should not obtain the default requirements provider"
            tagProviders.collect { it.class.simpleName } == ["CustomRequirementsTagProvider"]
    }
}
