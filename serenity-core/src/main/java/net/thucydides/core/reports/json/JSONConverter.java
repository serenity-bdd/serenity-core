package net.thucydides.core.reports.json;

import net.thucydides.core.model.TestOutcome;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface JSONConverter {
    TestOutcome fromJson(InputStream inputStream) throws IOException;
    void toJson(TestOutcome storedTestOutcome, OutputStream outputStream) throws IOException;
}
