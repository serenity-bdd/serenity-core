package net.thucydides.core.releases;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.model.Release;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestTag;
import net.thucydides.core.reports.TestOutcomes;
import net.thucydides.core.reports.html.ReportNameProvider;
import net.thucydides.core.requirements.RequirementsService;
import net.thucydides.core.requirements.RequirementsTagProvider;
import net.thucydides.core.requirements.reports.RequirementOutcome;
import net.thucydides.core.util.EnvironmentVariables;
import org.hamcrest.Matcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.apache.commons.lang3.StringUtils.capitalize;
import static org.apache.commons.lang3.StringUtils.lowerCase;
import static org.hamcrest.Matchers.containsString;

public class ReleaseManager {

    private final static String DEFAULT_RELEASE_TYPES = "Release,Iteration:Sprint";
    private List<String> releaseTypes;
    private ReportNameProvider reportNameProvider;
    private RequirementsService requirementsService;
    private static final Logger LOGGER = LoggerFactory.getLogger(RequirementsTagProvider.class);

    public ReleaseManager(EnvironmentVariables environmentVariables, ReportNameProvider reportNameProvider) {
        this.reportNameProvider = reportNameProvider;
        this.requirementsService = Injectors.getInjector().getInstance(RequirementsService.class);
        String typeValues = ThucydidesSystemProperty.THUCYDIDES_RELEASE_TYPES.from(environmentVariables, DEFAULT_RELEASE_TYPES);
        releaseTypes = Splitter.on(",").trimResults().splitToList(typeValues);
    }

    public String getJSONReleasesFrom(TestOutcomes testOutcomes) {
        List<Release> releases = getReleasesFrom(testOutcomes);
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        Gson gson = builder.create();
        return gson.toJson(releases);

    }

    public String getJSONReleasesFrom(Release release) {
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        Gson gson = builder.create();
        return gson.toJson(ImmutableList.of(release));

    }

    public List<Release> getFlattenedReleasesFrom(TestOutcomes testOutcomes) {
        return flattened(getReleasesFrom(testOutcomes));
    }

    private List<Release> flattened(List<Release> releases) {
        List<Release> flattenedReleases = Lists.newArrayList();
        for (Release release : releases) {
            flattenedReleases.add(release);
            flattenedReleases.addAll(flattened(release.getChildren()));
        }
        return ImmutableList.copyOf(flattenedReleases);
    }

    List<Release> NO_PARENTS = ImmutableList.of();

    public List<Release> getReleasesFrom(TestOutcomes testOutcomes) {

        List<Release> releases = requirementsService.getReleasesFromRequirements();
        LOGGER.debug("Loaded Releases: " + releases);

        enrichOutcomesWithReleaseTags(testOutcomes.getOutcomes());

        if (releases.isEmpty()) {
            releases = extractReleasesFromTestOutcomeAnnotations(testOutcomes);
        }
        return ImmutableList.copyOf(releases);
    }

    private List<Release> extractReleasesFromTestOutcomeAnnotations(TestOutcomes testOutcomes) {
        List<Release> releases = Lists.newArrayList();
        List<TestTag> releaseTags = testOutcomes.findMatchingTags()
                .withType("version")
                .withNameIn(matchingNames(releaseTypes.get(0)))
                .list();
        for (TestTag tag : releaseTags) {
            releases.add(extractReleaseFor(tag, testOutcomes.withTag(tag), 1, NO_PARENTS));
        }
        return releases;
    }

    private List<Matcher<String>> matchingNames(String possibleNames) {
        List<Matcher<String>> matchers = Lists.newArrayList();
        List<String> nameCandidates = Splitter.on(":").trimResults().splitToList(possibleNames.toLowerCase());
        for(String nameCandidate : nameCandidates) {
            matchers.add((containsString(nameCandidate)));
            matchers.add((containsString(capitalize(nameCandidate))));
            matchers.add((containsString(lowerCase(nameCandidate))));
        }
        return matchers;
    }

    public List<RequirementOutcome> enrichRequirementsOutcomesWithReleaseTags(List<? extends RequirementOutcome> outcomes) {
        List<RequirementOutcome> requirementOutcomes = Lists.newArrayList();
        for (RequirementOutcome outcome : outcomes) {
            List<TestOutcome> enrichedOutcomes = enrichOutcomesWithReleaseTags(outcome.getTestOutcomes().getOutcomes());
            requirementOutcomes.add(outcome.withTestOutcomes(TestOutcomes.of(enrichedOutcomes)));
        }
        return requirementOutcomes;
    }

    public List<TestOutcome> enrichOutcomesWithReleaseTags(List<? extends TestOutcome> outcomes) {
        List<TestOutcome> enrichedOutcomes = Lists.newArrayList();
        for (TestOutcome outcome : outcomes) {
            List<String> releaseVersions = requirementsService.getReleaseVersionsFor(outcome);
            outcome.addTags(releaseTagsFrom(releaseVersions));
            outcome.addVersions(releaseVersions);
            enrichedOutcomes.add(outcome);
        }
        return enrichedOutcomes;
    }

    private List<TestTag> releaseTagsFrom(List<String> releaseVersions) {
        List<TestTag> tags = Lists.newArrayList();
        for (String releaseVersion : releaseVersions) {
            tags.add(TestTag.withName(releaseVersion).andType("version"));
        }
        return tags;
    }

    private Release extractReleaseFor(TestTag releaseTag, TestOutcomes testOutcomes,
                                      int level, List<Release> parents) {
        Release release = new Release(releaseTag);
        String reportName = reportNameProvider.forRelease(release);
        release = release.withReport(reportName).withParents(parents);

        List<Release> ancestors = ancestorsFor(release);

        if (level < releaseTypes.size()) {
            String childReleaseType = releaseTypes.get(level);
            List<TestTag> childReleaseTags = testOutcomes.findMatchingTags()
                    .withType("version")
                    .withNameIn(matchingNames(childReleaseType))
                    .list();
            List<Release> children = Lists.newArrayList();
            for (TestTag tag : childReleaseTags) {
                children.add(extractReleaseFor(tag, testOutcomes.withTag(tag), level + 1, ancestors));
            }
            release = release.withChildren(children);
        }

        return release;
    }

    private List<Release> ancestorsFor(Release release) {
        List<Release> ancestors = new ArrayList<Release>(release.getParents());
        ancestors.add(release);
        return ancestors;
    }

    public List<Release> extractReleasesFrom(List<List<String>> releaseVersions) {
        return extractReleasesOfTypeLevel(releaseVersions);
    }

    private ReportNameProvider defaultNameProvider;

    private ReportNameProvider getReportNameProvider() {
        if (defaultNameProvider == null) {
            defaultNameProvider = new ReportNameProvider();
        }
        return defaultNameProvider;
    }

    private List<Release> extractReleasesOfTypeLevel(List<List<String>> releaseVersions) {
        List<Release> releases = Lists.newArrayList();
        Set<String> distinctReleases = getDistinct(releaseVersions);
        for (String release : distinctReleases) {
            if (releaseIsOfType(release, releaseTypes.get(0))) {
                List<Release> subReleases = extractSubReleasesOfLevel(releaseVersions,
                                                                      release,
                                                                      1,
                                                                      Collections.EMPTY_LIST);
                String reportName = getReportNameProvider().forRelease(release);
                addUniqueRelease(releases,
                                 Release.ofVersion(release)
                                        .withChildren(subReleases)
                                        .withReport(reportName));
            }
        }
        Collections.sort(releases);
        return releases;
    }

    private boolean releaseIsOfType(String release, String releaseTypes) {
        for(Matcher matcher : matchingNames(releaseTypes)) {
            if (matcher.matches(release.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    private void addUniqueRelease(List<Release> releases, Release release) {
        if (!containsReleaseWithName(releases,release.getName()))
            releases.add(release);
        }

    private boolean containsReleaseWithName(List<Release> releases, String name) {
        for(Release release : releases) {
            if (release.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }


    private List<Release> extractSubReleasesOfLevel(List<List<String>> releaseVersions,
                                                    String release,
                                                    int level,
                                                    List<Release> parents) {
        List<Release> subReleases = Lists.newArrayList();
        if (level < releaseTypes.size()) {
            for (List<String> releaseVersionSet : releaseVersions) {
                if (releaseVersionSet.contains(release)) {
                    for (String subRelease : releasesOfType(releaseTypes.get(level), releaseVersionSet)) {
                        List<Release> ancestors = Lists.newArrayList();
                        ancestors.add(lightweightReleaseNamed(release));
                        List<Release> subSubReleases = extractSubReleasesOfLevel(releaseVersions,
                                                                                 subRelease,
                                                                                 level + 1,
                                                                                 ancestors);
                        String reportName = getReportNameProvider().forRelease(subRelease);
                        addUniqueRelease(subReleases, Release.ofVersion(subRelease).withChildren(subSubReleases)
                                                                                   .withReport(reportName)
                                                                                   .withParents(ancestors));
                    }
                }
            }
        }
        Collections.sort(subReleases);
        return subReleases;
    }

    private Release lightweightReleaseNamed(String release) {
        return Release.ofVersion(release).withReport(reportNameProvider.forRelease(release));
    }

    private List<String> releasesOfType(String type, List<String> releaseVersionSet) {
        List<String> matchingReleases = Lists.newArrayList();
        for (String release : releaseVersionSet) {
            if (releaseIsOfType(release, type)) {
                matchingReleases.add(release);
            }
        }
        return matchingReleases;
    }


    private Set<String> getDistinct(List<List<String>> releaseVersions) {
        Set<String> distinctVersions = Sets.newHashSet();
        for (List<String> releaseVersionSets : releaseVersions) {
            distinctVersions.addAll(releaseVersionSets);
        }
        return distinctVersions;
    }
}
