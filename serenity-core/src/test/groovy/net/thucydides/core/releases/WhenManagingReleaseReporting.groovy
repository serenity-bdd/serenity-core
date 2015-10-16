package net.thucydides.core.releases

import net.thucydides.core.model.Story
import net.thucydides.core.model.TestOutcome
import net.thucydides.core.model.TestTag
import net.thucydides.core.reports.TestOutcomes
import net.thucydides.core.reports.html.ReportNameProvider
import net.thucydides.core.util.MockEnvironmentVariables
import org.skyscreamer.jsonassert.JSONAssert
import org.skyscreamer.jsonassert.JSONCompareMode
import spock.lang.Specification

/**
 * Release reporting is done using tags and naming conventions.
 */
class WhenManagingReleaseReporting extends Specification {

    def outcome1 = TestOutcome.forTestInStory("someTest 1", Story.withId("1","story"))
    def outcome2 = TestOutcome.forTestInStory("someTest 2", Story.withId("1","story"))
    def outcome3 = TestOutcome.forTestInStory("someTest 3", Story.withId("1","story"))
    def outcome4 = TestOutcome.forTestInStory("someTest 4", Story.withId("1","story"))

    def outcome5 = TestOutcome.forTestInStory("someTest 5", Story.withIdAndPath("1","plant potatoes","grow_potatoes/grow_new_potatoes"))

    def release1 = TestTag.withName("PROJ Release 1").andType("version")
    def release2 = TestTag.withName("PROJ Release 2").andType("version")
    def iteration1 = TestTag.withName("Iteration 1").andType("version")
    def iteration2 = TestTag.withName("Iteration 2").andType("version")
    def iteration3 = TestTag.withName("Iteration 3").andType("version")

    def setup() {
        outcome1.addTags([release2, iteration3])
        outcome2.addTags([release1, iteration1])
        outcome3.addTags([release1, iteration2])
        outcome4.addTags([release1,iteration1])
    }

    def environmentVariables = new MockEnvironmentVariables()
    def reportNameProvider = new ReportNameProvider()

    def "should be able to list the top-level releases"() {
        given:
            def releaseManager = new ReleaseManager(environmentVariables, reportNameProvider);
            def testOutcomes = TestOutcomes.of([outcome1, outcome2, outcome3, outcome4])
        when:
            def releases = releaseManager.getReleasesFrom(testOutcomes)
        then:
            releases.collect { it.label } == ["Release 1.0", "Release 2.0"]
    }

    def "should be able to derive release tags from requirements"() {
        given:
            def releaseManager = new ReleaseManager(environmentVariables, reportNameProvider);
            def testOutcomes = TestOutcomes.of([outcome5])
        when:
            def releases = releaseManager.getReleasesFrom(testOutcomes)
        then:
            releases.collect { it.label } == ["Release 1.0","Release 2.0"]
    }



    def "should be able to obtain a flattened list of the releases"() {
        given:
            def releaseManager = new ReleaseManager(environmentVariables, reportNameProvider);
            def testOutcomes = TestOutcomes.of([outcome1, outcome2, outcome3, outcome4])
        when:
            def flatReleases = releaseManager.getFlattenedReleasesFrom(testOutcomes)
        then:
            flatReleases.size() == 5
    }

    def "Releases should have parents stored"() {
        given:
            def releaseManager = new ReleaseManager(environmentVariables, reportNameProvider);
            def testOutcomes = TestOutcomes.of([outcome1, outcome2, outcome3, outcome4])
        when:
            def releases = releaseManager.getReleasesFrom(testOutcomes)
            def release1 = releases[0]
            def iteration1 = release1.children[0]
        then:
            release1.parents == []
        and:
            iteration1.parents == [release1]
    }

    def "Release  entries should store children"() {
        given:
            def releaseManager = new ReleaseManager(environmentVariables, reportNameProvider);
            def testOutcomes = TestOutcomes.of([outcome1, outcome2, outcome3, outcome4])
        when:
            def releases = releaseManager.getReleasesFrom(testOutcomes)
            def release1 = releases[0]
        then:
            release1.children.collect {it.name} == ["Iteration 1.1","Iteration 1.3"]
    }


    def "Release parent entries should not store children"() {
        given:
            def releaseManager = new ReleaseManager(environmentVariables, reportNameProvider);
            def testOutcomes = TestOutcomes.of([outcome1, outcome2, outcome3, outcome4])
        when:
            def releases = releaseManager.getReleasesFrom(testOutcomes)
            def release1 = releases[0]
            def iteration1 = release1.children[0]
        then:
            iteration1.parents[0].children == []
    }

    def "Releases should be available in JSON format"() {
        given:
            def releaseManager = new ReleaseManager(environmentVariables, reportNameProvider);
            def testOutcomes = TestOutcomes.of([outcome1, outcome2, outcome3, outcome4])
        when:
            def releasesData = releaseManager.getJSONReleasesFrom(testOutcomes)
        then:
            def expectedReleasesData = """[
  {
    "releaseTag": {
      "name": "Release 1.0",
      "type": "version"
    },
    "children": [
      {
        "releaseTag": {
          "name": "Iteration 1.1",
          "type": "version"
        },
        "children": [],
        "label": "Iteration 1.1",
        "reportName": "8e5dc84bfe3cb643f0a317a065f8e1ea4c72db50cd352256db8fa690bacb3f3f.html",
        "parents": [
          {
            "releaseTag": {
              "name": "Release 1.0",
              "type": "version"
            },
            "children": [],
            "label": "Release 1.0",
            "reportName": "5c2ec3497821f4f23d865a2ff33fe12563fbc80d44661a7ba83f78f34d37a1a5.html",
            "parents": []
          }
        ]
      },
      {
        "releaseTag": {
          "name": "Iteration 1.3",
          "type": "version"
        },
        "children": [],
        "label": "Iteration 1.3",
        "reportName": "24307a38716cb7c650b8a0202a9a15680813e6ede7aadd9440fbfe90c5f55ed2.html",
        "parents": [
          {
            "releaseTag": {
              "name": "Release 1.0",
              "type": "version"
            },
            "children": [],
            "label": "Release 1.0",
            "reportName": "5c2ec3497821f4f23d865a2ff33fe12563fbc80d44661a7ba83f78f34d37a1a5.html",
            "parents": []
          }
        ]
      }
    ],
    "label": "Release 1.0",
    "reportName": "5c2ec3497821f4f23d865a2ff33fe12563fbc80d44661a7ba83f78f34d37a1a5.html",
    "parents": []
  },
  {
    "releaseTag": {
      "name": "Release 2.0",
      "type": "version"
    },
    "children": [
      {
        "releaseTag": {
          "name": "Iteration 2.1",
          "type": "version"
        },
        "children": [],
        "label": "Iteration 2.1",
        "reportName": "f687e4e3341aa1d8b2648a37036f7f26c1adee02d0d379a03847fb25554c8518.html",
        "parents": [
          {
            "releaseTag": {
              "name": "Release 2.0",
              "type": "version"
            },
            "children": [],
            "label": "Release 2.0",
            "reportName": "ebf50a7a7c6b63aa4494768dd9732cd383c1db686107a6e81bd896aa95939de5.html",
            "parents": []
          }
        ]
      }
    ],
    "label": "Release 2.0",
    "reportName": "ebf50a7a7c6b63aa4494768dd9732cd383c1db686107a6e81bd896aa95939de5.html",
    "parents": []
  }
]"""
        releasesData == expectedReleasesData
        JSONAssert.assertEquals(releasesData, expectedReleasesData, JSONCompareMode.LENIENT)

    }
}