package net.thucydides.model.reports.json;

import net.thucydides.model.domain.TestOutcome;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;

public interface JSONConverter {
    java.util.Optional<TestOutcome> fromJson(InputStream inputStream) throws IOException;
    java.util.Optional<TestOutcome> fromJson(Reader in);
    void toJson(TestOutcome storedTestOutcome, OutputStream outputStream) throws IOException;
}
