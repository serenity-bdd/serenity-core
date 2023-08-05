package net.serenitybdd.cli

import net.thucydides.model.requirements.FileSystemRequirementsTagProvider
import net.thucydides.model.environment.MockEnvironmentVariables
import spock.lang.Specification

class WhenLoadingRequirementsFromAProvidedDirectoryStructure extends Specification{

    def "should load a requirements structure from a provided file directory"() {
        given:
            def environmentVariables = new MockEnvironmentVariables()
        when:
            def tagProvider = new FileSystemRequirementsTagProvider(environmentVariables,
                                                                    "src/test/resources/featuredir")
        then:
            tagProvider.requirements.collect {it.name } as Set == ["Maintain my todo list","Record todos"] as Set
    }
}
