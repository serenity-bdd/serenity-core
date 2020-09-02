package net.thucydides.core.model;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

public class ReportData {
    private final String title;
    private final String contents;
    private final String path;
    private Boolean isEvidence;
    private String id;

    public ReportData(String title, String contents, String path) {

        this(title, contents, path, false);
    }

    public ReportData(String title, String contents, String path, Boolean isEvidence) {

        this.title = title;
        this.contents = contents;
        this.path = path;
        this.isEvidence = isEvidence;
        this.id = "report-data-" + UUID.randomUUID().toString();
    }

    public static ReportDataBuilder withTitle(String title) {
        return new ReportDataBuilder(title);
    }

    public String getTitle() {
        return title;
    }

    public String getContents() {
        return contents;
    }

    public String getPath() { return path; }

    public String getId() { return id; }

    public static class ReportDataBuilder {
        private final String title;
        private boolean asEvidence = false;

        public ReportDataBuilder(String title) {
            this.title = title;
        }

        public ReportDataBuilder asEvidence() {
            this.asEvidence = true;
            return this;
        }

        public ReportData andContents(String contents) {
            return new ReportData(title, contents, null);
        }

        public ReportData fromFile(Path source, Charset encoding) throws IOException {
            byte[] encoded = Files.readAllBytes(source);
            return new ReportData(title, new String(encoded, encoding), null, asEvidence);
        }

        public ReportData fromPath(Path path) throws IOException {
            String storedRelativePath = Downloadables.copyDownloadableFileFrom(path);
            return new ReportData(title, null, storedRelativePath);
        }

    }

    public ReportData asEvidence(Boolean isEvidence) {
        return new ReportData(title, contents, path, isEvidence);
    }

    public Boolean isEvidence() {
        return isEvidence;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ReportData that = (ReportData) o;

        if (title != null ? !title.equals(that.title) : that.title != null) return false;
        return contents != null ? contents.equals(that.contents) : that.contents == null;
    }

    @Override
    public int hashCode() {
        int result = title != null ? title.hashCode() : 0;
        result = 31 * result + (contents != null ? contents.hashCode() : 0);
        return result;
    }
}
