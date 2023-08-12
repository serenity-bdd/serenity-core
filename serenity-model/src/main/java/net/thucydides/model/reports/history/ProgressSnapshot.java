package net.thucydides.model.reports.history;

import org.joda.time.DateTime;

public class ProgressSnapshot implements Comparable<ProgressSnapshot> {

    private final DateTime time;
    private final String requirementType;
    private final int total;
    private final int estimated;
    private final int completed;
    private final int failed;
    private final String buildId;

    private ProgressSnapshot(DateTime time, String requirementType, int total, int completed, int estimated, int failed, String buildId) {
        this.time = time;
        this.requirementType = requirementType;
        this.total = total;
        this.completed = completed;
        this.estimated = estimated;
        this.failed = failed;
        this.buildId = buildId;
    }

    public static ProgressSnapshotBuilder forRequirementType(String requirementType) {
        return new ProgressSnapshotBuilder(requirementType);
    }

    public static ProgressSnapshot copyOf(ProgressSnapshot progressSnapshot) {
        return new ProgressSnapshot(progressSnapshot.time,
                                    progressSnapshot.requirementType,
                                    progressSnapshot.total,
                                    progressSnapshot.completed,
                                    progressSnapshot.estimated,
                                    progressSnapshot.failed,
                                    progressSnapshot.buildId);
    }

    public static class ProgressSnapshotBuilder {
        private final String requirementType;
        private int completed;
        private int estimated;
        private int total;
        private int failing;
        private DateTime time;
        private boolean useEstimatedValue = false;

        public ProgressSnapshotBuilder(String requirementType) {
            this.requirementType = requirementType;
            this.time = DateTime.now();
        }

        public ProgressSnapshotBuilder atTime(DateTime time) {
            this.time = time;
            return this;
        }

        public ResultCountBuilder with(int count) {
            return new ResultCountBuilder(count, this);
        }

        public ResultCountBuilder and(int count) {
            return new ResultCountBuilder(count, this);
        }

        public ProgressSnapshotBuilder outOf(int totalRequirements) {
            this.total = totalRequirements;
            return this;
        }

        public ProgressSnapshot forBuild(String buildId) {
            return new ProgressSnapshot(time, requirementType, total, completed,
                                        useEstimatedValue ? estimated : completed,
                                        failing, buildId);
        }

        public static class ResultCountBuilder {
            private final int count;
            private final ProgressSnapshotBuilder snapshotBuilder;

            public ResultCountBuilder(int count, ProgressSnapshotBuilder snapshotBuilder) {
                this.count = count;
                this.snapshotBuilder = snapshotBuilder;
            }

            public ProgressSnapshotBuilder completed() {
                snapshotBuilder.completed = count;
                return snapshotBuilder;
            }

            public ProgressSnapshotBuilder estimated() {
                snapshotBuilder.estimated = count;
                snapshotBuilder.useEstimatedValue = true;
                return snapshotBuilder;
            }

            public ProgressSnapshotBuilder failed() {
                snapshotBuilder.failing = count;
                return snapshotBuilder;
            }

        }
    }

    public DateTime getTime() {
        return time;
    }

    public String getRequirementType() {
        return requirementType;
    }

    public int getTotal() {
        return total;
    }

    public int getCompleted() {
        return completed;
    }

    public int getEstimated() {
        return estimated;
    }

    public int getFailed() {
        return failed;
    }

    public String getBuildId() {
        return buildId;
    }

    public String getFormattedTime() {
        return time.toString("yyyy/MM/dd hh:mm:ss");
    }

    public int compareTo(ProgressSnapshot other) {
        if (this == other) {
            return 0;
        } else {
            return this.getTime().compareTo(other.getTime());
        }
    }
}
