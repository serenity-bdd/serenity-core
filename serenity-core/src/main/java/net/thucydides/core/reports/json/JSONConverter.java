package net.thucydides.core.reports.json;

import com.google.common.base.Optional;
import net.thucydides.core.model.TestOutcome;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;

public interface JSONConverter {
    Optional<TestOutcome> fromJson(InputStream inputStream) throws IOException;
    Optional<TestOutcome> fromJson(Reader in);
    void toJson(TestOutcome storedTestOutcome, OutputStream outputStream) throws IOException;
}
