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
        "reportName": "30ba48934f711d51fc05abc1b94a9cbf.html",
        "parents": [
          {
            "releaseTag": {
              "name": "Release 1.0",
              "type": "version"
            },
            "children": [],
            "label": "Release 1.0",
            "reportName": "26f026356fbb43a9edfabb12638b1e22.html",
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
        "reportName": "9e9f764cb1c1c42483255af597e4f45f.html",
        "parents": [
          {
            "releaseTag": {
              "name": "Release 1.0",
              "type": "version"
            },
            "children": [],
            "label": "Release 1.0",
            "reportName": "26f026356fbb43a9edfabb12638b1e22.html",
            "parents": []
          }
        ]
      }
    ],
    "label": "Release 1.0",
    "reportName": "26f026356fbb43a9edfabb12638b1e22.html",
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
        "reportName": "457064a7c30a1499b6f851b4a067fd3d.html",
        "parents": [
          {
            "releaseTag": {
              "name": "Release 2.0",
              "type": "version"
            },
            "children": [],
            "label": "Release 2.0",
            "reportName": "2004077d2caed9cbf16f8cd4a6f83922.html",
            "parents": []
          }
        ]
      }
    ],
    "label": "Release 2.0",
    "reportName": "2004077d2caed9cbf16f8cd4a6f83922.html",
    "parents": []
  }
]"""
        releasesData == expectedReleasesData
        JSONAssert.assertEquals(releasesData, expectedReleasesData, JSONCompareMode.LENIENT)

    }
}