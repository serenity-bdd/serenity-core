package net.thucydides.core.statistics.model;

import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestResult;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

//@Entity
//@Immutable
public class TestRun {

//    @Id
//    @GeneratedValue
    private Long id;

    private String title;
    private String projectKey;
    private TestResult result;
    private Date executionDate;
    private long duration;

//    @ManyToMany
//    @JoinTable(
//            name = "testrun_tags",
//            joinColumns = {@JoinColumn(name = "testrun_id")},
//            inverseJoinColumns = {@JoinColumn(name = "tag_id")}
//    )
    private Set<TestRunTag> tags = new HashSet<TestRunTag>();

    public TestRun() {}

    protected TestRun(String title, String projectKey, TestResult result, long duration, Date executionDate) {
        this.title = title;
        this.projectKey = projectKey;
        this.result = result;
        this.executionDate = executionDate;
        this.duration = duration;
    }

    public String getTitle() {
        return title;
    }

    public String getProjectKey() {
        return projectKey;
    }

    public TestResult getResult() {
        return result;
    }

    public Date getExecutionDate() {
        return (executionDate == null) ? null : new Date(executionDate.getTime());
    }

    public long getDuration() {
        return duration;
    }

    public Set<TestRunTag> getTags() {
        return tags;
    }

    public static TestRun from(final TestOutcome result) {
        return new TestRun(result.getTitle(), null, result.getResult(), result.getDuration(), null);
    }

    public TestRun inProject(final String projectKey) {
        return new TestRun(getTitle(), projectKey, getResult(), getDuration(), getExecutionDate());
    }

    public TestRun at(final Date executionDate) {
        return new TestRun(getTitle(), getProjectKey(), getResult(), getDuration(), executionDate);
    }
}
