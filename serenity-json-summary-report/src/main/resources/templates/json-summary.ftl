{
  "report": {
    "title": "${report.title?json_string}",
    "tagCategoryTitle": "${report.tagCategoryTitle?json_string}",
    "version": "${report.version?json_string}",
    "date": "${report.date}"
  },
  "results": {
    "counts": {
      "total": ${results.totalCount},
      "success": ${results.countByResult.SUCCESS},
      "pending": ${results.countByResult.PENDING},
      "ignored": ${results.countByResult.IGNORED},
      "skipped": ${results.countByResult.SKIPPED},
      "failure": ${results.countByResult.FAILURE},
      "error": ${results.countByResult.ERROR},
      "compromised": ${results.countByResult.COMPROMISED}
    },
    "percentages": {
      "success": ${results.percentageByResult.SUCCESS},
      "pending": ${results.percentageByResult.PENDING},
      "ignored": ${results.percentageByResult.IGNORED},
      "skipped": ${results.percentageByResult.SKIPPED},
      "failure": ${results.percentageByResult.FAILURE},
      "error": ${results.percentageByResult.ERROR},
      "compromised": ${results.percentageByResult.COMPROMISED}
    },
    "totalTestDuration": ${results.totalTestDuration},
    "totalClockDuration": ${results.clockTestDuration},
    "minTestDuration": ${results.minTestDuration},
    "maxTestDuration": ${results.maxTestDuration},
    "averageTestDuration": ${results.averageTestDuration}
  },
  "resultsByFeature": [
    <#list resultsByFeature as feature>
    {
      "featureName": "${feature.featureName?json_string}",
      "scenarios": [
        <#list feature.scenarios as scenario>
        {
          "title": "${scenario.title?json_string}",
          "result": "${scenario.result}",
          <#if (scenario.results?has_content)>
          "outcomes": [
            <#list scenario.results as scenarioOutcome>
            {
              "result": "${scenarioOutcome.result}",
              "description": "${scenarioOutcome.description?json_string}",
              "errorMessage": "${scenarioOutcome.errorMessage?json_string}"
            }<#sep>,</#sep>
            </#list>
          ]
          <#else>
          "outcomes": []
          </#if>
        }<#sep>,</#sep>
        </#list>
      ]
    }<#sep>,</#sep>
    </#list>
  ],
  "frequentFailures": [
    <#list frequentFailures as frequentFailure>
    {
      "name": "${frequentFailure.name?json_string}",
      "count": ${frequentFailure.count}
    }<#sep>,</#sep>
    </#list>
  ],
  "unstableFeatures": [
    <#list unstableFeatures as unstableFeature>
    {
      "name": "${unstableFeature.name?json_string}",
      "failurePercentage": ${unstableFeature.failurePercentage}
    }<#sep>,</#sep>
    </#list>
  ],
  "coverage": [
    <#list coverage as tagCoverageByType>
    {
      "tagTitle": "${tagCoverageByType.tagTitle?json_string}",
      "tagCoverages": [
        <#list tagCoverageByType.tagCoverage as tagCoverage>
        {
          "tagName": "${tagCoverage.tagName?json_string}",
          "testCount": ${tagCoverage.testCount},
          "successRate": "${tagCoverage.successRate}",
          "counts": {
            "total": ${results.totalCount},
            "success": ${tagCoverage.countByResult.SUCCESS},
            "pending": ${tagCoverage.countByResult.PENDING},
            "ignored": ${tagCoverage.countByResult.IGNORED},
            "skipped": ${tagCoverage.countByResult.SKIPPED},
            "failure": ${tagCoverage.countByResult.FAILURE},
            "error": ${tagCoverage.countByResult.ERROR},
            "compromised": ${tagCoverage.countByResult.COMPROMISED}
          },
          "percentages": {
            "success": ${tagCoverage.percentageByResult.SUCCESS},
            "pending": ${tagCoverage.percentageByResult.PENDING},
            "ignored": ${tagCoverage.percentageByResult.IGNORED},
            "skipped": ${tagCoverage.percentageByResult.SKIPPED},
            "failure": ${tagCoverage.percentageByResult.FAILURE},
            "error": ${tagCoverage.percentageByResult.ERROR},
            "compromised": ${tagCoverage.percentageByResult.COMPROMISED}
          }
        }<#sep>,</#sep>
        </#list>
      ]
    }<#sep>,</#sep>
    </#list>
  ],
  "tags": [
    <#list tagResults as tagResultGroup>
    {
      "tagType": "${tagResultGroup.tagType?json_string}",
      "tagResults": [
        <#list tagResultGroup.tagResults as tagResult>
        {
          "tagName": "${tagResult.tag.name?json_string}",
          "count": ${tagResult.count}
        }<#sep>,</#sep>
        </#list>
      ]
    }<#sep>,</#sep>
    </#list>
  ]
}



