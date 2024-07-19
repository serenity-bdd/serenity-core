package net.thucydides.model.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * A release or version of a software project.
 * Releases are identified by a tag, usually of type 'version'
 */
public class Release implements Comparable {
    private final TestTag releaseTag;
    private final List<Release> children;
    private final String label;
    private final String reportName;
    private final List<Release> parents;

    public static Release ofVersion(String versionName) {
        return new Release(TestTag.withName(versionName).andType("version"));
    }
    public Release(TestTag releaseTag) {
        this.releaseTag = releaseTag;
        this.label = releaseTag.getName();
        this.children = new ArrayList<>();
        this.parents = new ArrayList<>();
        this.reportName = null;
    }

    public Release(TestTag releaseTag, List<Release> children, List<Release> parents, String reportName) {
        this.releaseTag = releaseTag;
        this.label = releaseTag.getName();
        this.children = new ArrayList<>(children);
        this.parents = new ArrayList<>(parents);
        this.reportName = reportName;
    }

    public Release withChildren(List<Release> children) {
        return new Release(releaseTag, children, parents, reportName);
    }

    public Release withParents(List<Release> parents) {
        return new Release(releaseTag, children, parents, reportName);
    }

    public Release withReport(String reportName) {
        return new Release(releaseTag, children, parents, reportName);
    }
    public String getName() {
        return releaseTag.getName();
    }

    public String getLabel() {
        return label;
    }

    public List<Release> getChildren() {
        return children;
    }

    public List<Release> getParents() {
        return parents;
    }

    public TestTag getReleaseTag() {
        return releaseTag;
    }

    public String getReportName() {
        return reportName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Release release = (Release) o;

        if (!releaseTag.equals(release.releaseTag)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return releaseTag.hashCode();
    }

    @Override
    public int compareTo(Object otherRelease) {
        return getName().compareTo(((Release) otherRelease).getName());
    }

    @Override
    public String toString() {
        return "Release{" +
                "label='" + label + '\'' +
                '}';
    }
}
