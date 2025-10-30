package net.serenitybdd.cli

import net.thucydides.model.environment.MockEnvironmentVariables
import net.thucydides.model.requirements.FileSystemRequirementsTagProvider
import spock.lang.Specification

class WhenLoadingRequirementsFromAProvidedDirectoryStructure extends Specification{

    def "should load a requirements structure from a provided file directory"() {
        given:
            def environmentVariables = new MockEnvironmentVariables()
        when:
            def tagProvider = new FileSystemRequirementsTagProvider(environmentVariables,
                                                                    "src/test/resources/featuredir")
        then:
            tagProvider.requirements.collect {it.name } as Set == ["maintain_my_todo_list","record_todos"] as Set
            tagProvider.requirements.collect {it.displayName } as Set == ["Maintain my todo list","Record todos"] as Set
    }
}
