package net.thucydides.core.reports.json;

import net.thucydides.core.model.TestOutcome;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;

public interface JSONConverter {
    TestOutcome fromJson(InputStream inputStream) throws IOException;
    TestOutcome fromJson(Reader in);
    void toJson(TestOutcome storedTestOutcome, OutputStream outputStream) throws IOException;
}
