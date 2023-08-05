package net.thucydides.core.requirements

import com.google.common.collect.Maps
import net.thucydides.model.requirements.RequirementPersister
import net.thucydides.model.requirements.model.Requirement
import spock.lang.Specification

import java.nio.file.Files

class WhenStoringRequirementsAsJSON extends Specification {

    File tempDirectory;

    def setup() {
        tempDirectory = Files.createTempDirectory("tmp").toFile()
    }


    def "should be able to store a requirement in JSON form"() {
        given:
            def persister = new RequirementPersister(tempDirectory, "annotatedstories")
        and:
            SortedMap<String, Requirement> map = Maps.newTreeMap()
            map.put("apples", Requirement.named("Grow Apples").withType("feature").withNarrative("A feature"))
            map.put("oranges", Requirement.named("Grow Oranges").withType("feature").withNarrative("A feature"))
        when:
            persister.write(map);
        then:
            def storedRequirements = new File(tempDirectory,"annotatedstories.json")
            storedRequirements.exists()
    }

    def "should be able to store nested requirements in JSON form"() {
        given:
            def persister = new RequirementPersister(tempDirectory, "annotatedstories")
        and:
            SortedMap<String, Requirement> map = Maps.newTreeMap()
            map.put("apples", Requirement.named("Grow Apples").withType("capability").withNarrative("A capability"))
            map.put("apples.green", Requirement.named("Grow Green Apples").withType("feature").withNarrative("A feature"))
            map.put("oranges", Requirement.named("Grow Oranges").withType("feature").withNarrative("A feature"))
        when:
            persister.write(map);
        then:
            def storedRequirements = new File(tempDirectory,"annotatedstories.json")
            storedRequirements.exists()
    }


    def "should be able to store and reload nested requirements in JSON form"() {
        given:
            def persister = new RequirementPersister(tempDirectory, "annotatedstories")
        and:
            SortedMap<String, Requirement> map = Maps.newTreeMap()
            map.put("apples", Requirement.named("Grow Apples").withType("capability").withNarrative("A capability"))
            map.put("apples.green", Requirement.named("Grow Green Apples").withType("feature").withNarrative("A feature"))
            map.put("oranges", Requirement.named("Grow Oranges").withType("feature").withNarrative("A feature"))
        and:
            persister.write(map);
        when:
            SortedMap<String, Requirement> reloadedMap = persister.read()
        then:
            reloadedMap == map
    }

    def "should return an empty map if the file does not exist"() {
        given:
            def persister = new RequirementPersister(tempDirectory, "unknownstories")
        when:
            SortedMap<String, Requirement> reloadedMap = persister.read()
        then:
            reloadedMap == [:]
    }
}
