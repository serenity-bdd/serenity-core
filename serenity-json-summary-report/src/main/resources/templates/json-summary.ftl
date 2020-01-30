{
  "report": {
    "title": "${report.title}",
    "tagCategoryTitle": "${report.tagCategoryTitle}",
    "version": "${report.version}",
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
    "totalTestDuration": "${results.totalTestDuration}",
    "totalClockDuration": "${results.clockTestDuration}",
    "minTestDuration": "${results.minTestDuration}",
    "maxTestDuration": "${results.maxTestDuration}",
    "averageTestDuration": "${results.averageTestDuration}"
  },
  "resultsByFeature": [
    <#list resultsByFeature as feature>
    {
      "featureName": "${feature.featureName}",
      "scenarios": [
        <#list feature.scenarios as scenario>
        {
          "title": "${scenario.title}",
          "result": "${scenario.result}",
          <#if (scenario.results?has_content)>
          "outcomes": [
            <#list scenario.results as scenarioOutcome>
            {
              "result": "${scenarioOutcome.result}",
              "description": "${scenarioOutcome.description}",
              "errorMessage": "${scenarioOutcome.errorMessage}"
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
      "name": "${frequentFailure.name}",
      "count": ${frequentFailure.count}
    }<#sep>,</#sep>
    </#list>
  ],
  "unstableFeatures": [
    <#list unstableFeatures as unstableFeature>
    {
      "name": "${unstableFeature.name}",
      "failurePercentage": ${unstableFeature.failurePercentage}
    }<#sep>,</#sep>
    </#list>
  ],
  "coverage": [
    <#list coverage as tagCoverageByType>
    {
      "tagTitle": "${tagCoverageByType.tagTitle}",
      "tagCoverages": [
        <#list tagCoverageByType.tagCoverage as tagCoverage>
        {
          "tagName": "${tagCoverage.tagName}",
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
      "tagType": "${tagResultGroup.tagType}",
      "tagResults": [
        <#list tagResultGroup.tagResults as tagResult>
        {
          "tagName": "${tagResult.tag.name}",
          "count": ${tagResult.count}
        }<#sep>,</#sep>
        </#list>
      ]
    }<#sep>,</#sep>
    </#list>
  ]
}



